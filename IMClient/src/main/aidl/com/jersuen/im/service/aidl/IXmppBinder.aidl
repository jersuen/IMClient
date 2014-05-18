package com.jersuen.im.service.aidl;
import com.jersuen.im.service.aidl.IXmppManager;

interface IXmppBinder {

    IXmppManager createConnection();

}