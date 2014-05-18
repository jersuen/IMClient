package com.jersuen.im;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.jersuen.im.service.XmppBinder;
import com.jersuen.im.service.XmppManager;
import com.jersuen.im.service.aidl.IXmppBinder;
import org.jivesoftware.smack.ConnectionConfiguration;

/**
 * XMPP后台服务
 * @author JerSuen
 */
public class IMService extends Service {

    private XmppManager connection;
    private ConnectionConfiguration connectionConfig;
    private IXmppBinder.Stub binder;
    public void onCreate() {
        super.onCreate();
        binder = new XmppBinder(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            createConnection().connect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**初始化ConnectionConfiguration*/
    private ConnectionConfiguration initConnectionConfig() {
        if (connectionConfig == null) {
            connectionConfig = new ConnectionConfiguration(IM.HOST, IM.PORT);
            connectionConfig.setDebuggerEnabled(true);
            connectionConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        }
        return connectionConfig;
    }


    /**创建XmppManager*/
    public XmppManager createConnection() {
        if (connection == null) {
            connection = new XmppManager(initConnectionConfig(), IM.getString(IM.ACCOUNT_USERNAME), IM.getString(IM.ACCOUNT_PASSWORD));
        }
        return connection;
    }
}
