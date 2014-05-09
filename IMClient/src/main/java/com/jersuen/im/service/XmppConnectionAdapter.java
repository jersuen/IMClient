package com.jersuen.im.service;

import android.os.RemoteException;
import com.jersuen.im.service.aidl.IXmppConnection;
import org.jivesoftware.smack.*;

import java.io.IOException;

/**
 * XMPP连接适配器
 * Created by JerSuen on 14-5-4.
 */
public class XmppConnectionAdapter extends IXmppConnection.Stub{

    private XMPPConnection connection;
    private String account, password;
    private ConnectionListener connectionListener;

    public XmppConnectionAdapter(String serviceName, String account, String password) {
       this(new XMPPTCPConnection(serviceName), account, password);
    }

    public XmppConnectionAdapter( ConnectionConfiguration config, String account, String password) {
        this(new XMPPTCPConnection(config), account, password);
    }

    public XmppConnectionAdapter(XMPPConnection connection, String account, String password) {
        this.connection = connection;
        this.account = account;
        this.password = password;
    }

    /**建立XMPP连接*/
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
                    connectionListener = new MConnectionListener();
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

    /**登陆XMPP服务器*/
    public boolean login() throws RemoteException {
        // 未建立XMPP连接
        if (connection.isConnected()) {
            return false;
        }
        // 应经登陆过
        if (connection.isAuthenticated()) {
            return true;
        } else {
            try {
                // 开始登陆
                connection.login(account, password);
                return true;
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**关闭XMPP连接*/
    public boolean disconnect() throws RemoteException {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            return true;

        }
        return false;
    }

    /**XMPP连接监听器*/
    private class MConnectionListener implements ConnectionListener {

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
}
