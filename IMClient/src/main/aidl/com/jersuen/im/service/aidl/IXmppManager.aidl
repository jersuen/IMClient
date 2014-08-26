package com.jersuen.im.service.aidl;

import com.jersuen.im.service.aidl.Contact;

interface IXmppManager {

        /**建立连接*/
        boolean connect();

        /**登陆*/
        boolean login();

        /**关闭连接*/
        boolean disconnect();

        /**发送消息*/
        void sendMessage(String sessionJID, String sessionName, String message, String type);

        /**设置联系人备注*/
        boolean setRosterEntryName(String jid, String name);

        /**设置名片信息*/
        boolean setVCard(in Contact contact);

        /**搜索账户*/
        String searchAccount(String accountName);

        /**获取名片信息*/
        Contact getVCard(String jid);
}