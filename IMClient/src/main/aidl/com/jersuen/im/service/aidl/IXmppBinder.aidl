package com.jersuen.im.service.aidl;
import com.jersuen.im.service.aidl.IXmppManager;

interface IXmppBinder {
    IXmppManager createConnection();
    boolean setRosterEntryName(String jid, String name);
    boolean setVCard(String jid, in byte[] avatarBytes, String nickName);

}