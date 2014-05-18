package com.jersuen.im.service;

import android.os.RemoteException;
import com.jersuen.im.service.aidl.IXmppManager;
import org.jivesoftware.smack.*;

import java.io.IOException;

/**
 * XMPP连接管理
 * @author JerSuen
 */
public class XmppManager extends IXmppManager.Stub {

    private XMPPConnection connection;
    private String account, password;
    private ConnectionListener connectionListener;

    public XmppManager(ConnectionConfiguration config, String account, String password) {
        this(new XMPPTCPConnection(config), account, password);
    }

    public XmppManager(XMPPConnection connection, String account, String password) {
        this.connection = connection;
        this.account = account;
        this.password = password;
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
                connection.login(account, password);
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
