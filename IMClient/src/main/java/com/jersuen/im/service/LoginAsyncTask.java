package com.jersuen.im.service;

import android.os.AsyncTask;
import android.os.RemoteException;
import com.jersuen.im.service.aidl.IXmppManager;

/**
 * 登陆任务
 * @author JerSuen
 */
public class LoginAsyncTask extends AsyncTask<IXmppManager, Void, Integer> {
    public static final int LOGIN_OK = 1;
    public static final int LOGIN_ERROR = 2;
    public static final int CONNECTION_ERROR = 3;

    protected Integer doInBackground(IXmppManager... xmppBinders) {
        try {
            IXmppManager connection = xmppBinders[0];
            // 连接成功
            if (connection.connect()) {
                // 登陆成功
                if (connection.login()) {
                    return LOGIN_OK;
                // 登陆失败
                } else {
                    return LOGIN_ERROR;
                }
            // 连接失败
            } else {
                return CONNECTION_ERROR;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
