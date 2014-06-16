package com.jersuen.im.service;

import android.content.ContentValues;
import android.os.RemoteException;
import com.jersuen.im.IMService;
import com.jersuen.im.R;
import com.jersuen.im.provider.ContactsProvider;
import com.jersuen.im.service.aidl.IXmppManager;
import com.jersuen.im.util.LogUtils;
import com.jersuen.im.util.PinYin;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Presence;

import java.io.IOException;
import java.util.Collection;

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
                Roster roster = connection.getRoster();
                if (rosterListener == null) {
                    rosterListener = new IMClientRosterListener();
                }
                if (roster != null && roster.getEntries().size() > 0) {
                    // 添加花名册监听器
                    roster.addRosterListener(rosterListener);
                    for (RosterEntry entry : roster.getEntries()) {
                        ContentValues values = new ContentValues();
                        values.put(ContactsProvider.ContactColumns.ACCOUNT, entry.getUser());
                        values.put(ContactsProvider.ContactColumns.AVATAR, "");
                        LogUtils.LOGD(XmppManager.class, "name :" + entry.getName());
                        values.put(ContactsProvider.ContactColumns.NICKNAME, entry.getName());
                        values.put(ContactsProvider.ContactColumns.SORT, PinYin.getPinYin(entry.getName()));
                        // 储存联系人
                        if (imService.getContentResolver().update(ContactsProvider.CONTACT_URI, values, ContactsProvider.ContactColumns.ACCOUNT + " = ?", new String[]{entry.getUser()}) == 0) {
                            imService.getContentResolver().insert(ContactsProvider.CONTACT_URI, values);
                        }
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
}
