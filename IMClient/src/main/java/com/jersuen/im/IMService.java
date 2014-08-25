package com.jersuen.im;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.jersuen.im.service.XmppManager;
import com.jersuen.im.service.aidl.IXmppManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;

/**
 * XMPP后台服务
 * @author JerSuen
 */
public class IMService extends Service {

    private XmppManager connection;
    private ConnectionConfiguration connectionConfig;
    private IXmppManager.Stub binder;

    public void onCreate() {
        super.onCreate();
        configureProviderManager(ProviderManager.getInstance());
        binder = createConnection();
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            createConnection().connect();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
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
            connection = new XmppManager(initConnectionConfig(), IM.getString(IM.ACCOUNT_JID), IM.getString(IM.ACCOUNT_PASSWORD), this);
        }
        return connection;
    }

    public void configureProviderManager (ProviderManager pm){
        // VCard
        pm.addIQProvider(VCardManager.ELEMENT, VCardManager.NAMESPACE, new VCardProvider());
    }
}
