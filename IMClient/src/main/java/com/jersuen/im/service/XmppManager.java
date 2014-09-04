package com.jersuen.im.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import com.jersuen.im.IM;
import com.jersuen.im.IMService;
import com.jersuen.im.R;
import com.jersuen.im.provider.ContactsProvider;
import com.jersuen.im.provider.SMSProvider;
import com.jersuen.im.service.aidl.Contact;
import com.jersuen.im.service.aidl.IXmppManager;
import com.jersuen.im.util.LogUtils;
import com.jersuen.im.util.PinYin;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.Base64;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.util.*;

/**
 * XMPP连接管理
 * @author JerSuen
 */
public class XmppManager extends IXmppManager.Stub {

    private XMPPConnection connection;
    private String account, password;
    private ConnectionListener connectionListener;
    private RosterListener rosterListener;
    private IMService imService;
    private PacketListener messageListener;
    private Map<String, Chat> jidChats = Collections.synchronizedMap(new HashMap<String, Chat>());

    public XmppManager(ConnectionConfiguration config, String account, String password, IMService imService) {
        this(new XMPPTCPConnection(config), account, password, imService);
    }

    public XmppManager(XMPPConnection connection, String account, String password, IMService imService) {
        this.connection = connection;
        this.account = account;
        this.password = password;
        this.imService = imService;
    }

    /**
     * 建立XMPP连接
     */
    public boolean connect() throws RemoteException {
        // 已经连接
        if (connection.isConnected()) {
            return true;
        } else {
            try {
                // 开始连接
                connection.connect();
                if (connectionListener == null) {
                    // 添加一个连接监听器
                    connectionListener = new IMClientConnectListener();
                }
                connection.addConnectionListener(connectionListener);
                return true;
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 登陆XMPP服务器
     */
    public boolean login() throws RemoteException {
        // 未建立XMPP连接
        if (!connection.isConnected()) {
            return false;
        }
        // 应经登陆过
        if (connection.isAuthenticated()) {
            return true;
        } else {
            // 开始登陆
            try {
                connection.login(account, password, imService.getString(R.string.app_name));
                if (messageListener == null) {
                    messageListener = new MessageListener();
                }
                // 添加消息监听器
                connection.addPacketListener(messageListener, new PacketTypeFilter(Message.class));
                Roster roster = connection.getRoster();
                Presence presence = roster.getPresence("jid");
                if (rosterListener == null) {
                    rosterListener = new IMClientRosterListener();
                }
                // 添加花名册监听器
                roster.addRosterListener(rosterListener);
                // 获取花名册
                if (roster != null && roster.getEntries().size() > 0) {
                    Uri uri = null;
                    for (RosterEntry entry : roster.getEntries()) {
                        LogUtils.LOGD(XmppManager.class, "name: " + entry.getName());
                        LogUtils.LOGD(XmppManager.class, "user: " + entry.getUser());
                        // 用户备注
                        String remarks = entry.getName();
                        // 获取联系人名片信息
                        VCard vCard = new VCard();
                        vCard.load(connection, entry.getUser());
                        if (vCard != null) {
                            IM.saveAvatar(vCard.getAvatar(), StringUtils.parseName(entry.getUser()));
                        }
                        ContentValues values = new ContentValues();
                        values.put(ContactsProvider.ContactColumns.ACCOUNT, entry.getUser());
                        values.put(ContactsProvider.ContactColumns.NAME, remarks);
                        String sortStr =  PinYin.getPinYin(remarks);
                        values.put(ContactsProvider.ContactColumns.SORT, sortStr);
                        values.put(ContactsProvider.ContactColumns.SECTION, sortStr.substring(0,1).toUpperCase(Locale.ENGLISH));
                        // 储存联系人
                        if (imService.getContentResolver().update(ContactsProvider.CONTACT_URI, values, ContactsProvider.ContactColumns.ACCOUNT + " = ?", new String[]{entry.getUser()}) == 0) {
                            uri = imService.getContentResolver().insert(ContactsProvider.CONTACT_URI, values);
                        }
                    }
                    // 发生改变，通知刷新
                    if (uri != null) {
                        imService.getContentResolver().notifyChange(uri, null);
                    }
                }
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    /**
     * 关闭XMPP连接
     */
    public boolean disconnect() throws RemoteException {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
        return true;
    }

    public XMPPConnection getConnection() {
        return connection;
    }

    /**发送消息*/
    public void sendMessage(String sessionJID, String sessionName, String message, String type) throws RemoteException {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        Chat chat;
        // 查找Chat对策
        if (jidChats.containsKey(sessionJID)) {
            chat = jidChats.get(sessionJID);
        // 创建Chat
        } else {
            chat = chatManager.createChat(sessionJID, null);
            // 添加到集合
            jidChats.put(sessionJID, chat);
        }

        if (chat != null) {
            try {
                // 发送消息
                chat.sendMessage(message);

                // 保存聊天记录
                ContentValues values = new ContentValues();
                values.put(SMSProvider.SMSColumns.BODY, message);
                values.put(SMSProvider.SMSColumns.TYPE, type);
                values.put(SMSProvider.SMSColumns.TIME, System.currentTimeMillis());

                values.put(SMSProvider.SMSColumns.WHO_ID, IM.getString(IM.ACCOUNT_JID));

                values.put(SMSProvider.SMSColumns.SESSION_ID, sessionJID);
                values.put(SMSProvider.SMSColumns.SESSION_NAME, sessionName);

                imService.getContentResolver().insert(SMSProvider.SMS_URI, values);

            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    /**设置联系人备注*/
    public boolean setRosterEntryName(String jid, String name) {
        try {
            connection.getRoster().getEntry(jid).setName(name);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**设置名片信息*/
    public boolean setVCard(Contact contact) throws RemoteException {
        VCard vCard = new VCard();
        try {
            // 加载自己的VCard
            vCard.load(connection);
            // 设置昵称
            if (!TextUtils.isEmpty(contact.name)) {
                vCard.setNickName(contact.name);
            }
            // 设置头像
            if (!TextUtils.isEmpty(contact.avatar)) {
                vCard.setAvatar(Base64.decode(contact.avatar));
            }
            // 保存
            vCard.save(connection);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**获取名片信息*/
    public Contact getVCard(String jid) throws RemoteException {
        if (!TextUtils.isEmpty(jid)) {
            VCard vCard = new VCard();
            try {
                vCard.load(getConnection(), jid);
                Contact contact = new Contact();
                contact.account = jid;
                contact.name = vCard.getNickName();
                contact.avatar = Base64.encodeBytes(vCard.getAvatar());
                return contact;
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**搜索账户 XEP-0055*/
    public String searchAccount(String accountName) throws RemoteException {
        try {
            // 创建搜索
            UserSearchManager searchManager = new UserSearchManager(getConnection());
            // 获取搜索表单
            Form searchForm = searchManager.getSearchForm("search." + getConnection().getServiceName());
            // 提交表单
            Form answerForm = searchForm.createAnswerForm();
            // 设置搜索内容
            answerForm.setAnswer("search", accountName);
            // 设置搜索的列
            answerForm.setAnswer("Username", true);
            // 提交搜索表单
            ReportedData data = searchManager.getSearchResults(answerForm, "search." + getConnection().getServiceName());
            // 遍历结果列
            for (ReportedData.Row row : data.getRows()) {
                // 获取jid
                return row.getValues("jid").get(0);
            }
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * XMPP连接监听器
     */
    private class IMClientConnectListener implements ConnectionListener {

        public void connected(XMPPConnection connection) {

        }

        public void authenticated(XMPPConnection connection) {

        }

        public void connectionClosed() {

        }

        public void connectionClosedOnError(Exception e) {

        }

        public void reconnectingIn(int seconds) {

        }

        public void reconnectionSuccessful() {

        }

        public void reconnectionFailed(Exception e) {

        }
    }

    /**花名册监听器*/
    private class IMClientRosterListener implements RosterListener {

        public void entriesAdded(Collection<String> strings) {

        }

        public void entriesUpdated(Collection<String> strings) {

        }

        public void entriesDeleted(Collection<String> strings) {

        }

        public void presenceChanged(Presence presence) {

        }
    }

    /**消息监听器*/
    private class MessageListener implements PacketListener {

        public void processPacket(Packet packet) throws SmackException.NotConnectedException {
            if (packet instanceof Message) {
                Message message = (Message) packet;
                // 聊天消息
                if (message.getType() == Message.Type.chat) {
                    String whoAccountStr = StringUtils.parseBareAddress(message.getFrom());
                    String whoNameStr = "";

                    // 查询联系人的名称
                    Cursor cursor = imService.getContentResolver().query(ContactsProvider.CONTACT_URI, null, ContactsProvider.ContactColumns.ACCOUNT + " = ?", new String[]{whoAccountStr},null);
                    if (cursor != null && cursor.moveToFirst()) {
                        cursor.moveToPosition(0);
                        whoNameStr = cursor.getString(cursor.getColumnIndex(ContactsProvider.ContactColumns.NAME));
                    }

                    String bodyStr = message.getBody();
                    String typeStr = "chat";

                    // 插入消息
                    ContentValues values = new ContentValues();
                    values.put(SMSProvider.SMSColumns.BODY, bodyStr);
                    values.put(SMSProvider.SMSColumns.TYPE, typeStr);
                    values.put(SMSProvider.SMSColumns.TIME, System.currentTimeMillis());

                    values.put(SMSProvider.SMSColumns.WHO_ID, whoAccountStr);

                    values.put(SMSProvider.SMSColumns.SESSION_ID, whoAccountStr);
                    values.put(SMSProvider.SMSColumns.SESSION_NAME, whoNameStr);

                    imService.getContentResolver().insert(SMSProvider.SMS_URI, values);
                }
            }
        }
    }
}
