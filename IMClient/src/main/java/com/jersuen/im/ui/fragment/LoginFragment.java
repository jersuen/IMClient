package com.jersuen.im.ui.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.jersuen.im.IM;
import com.jersuen.im.IMService;
import com.jersuen.im.MainActivity;
import com.jersuen.im.R;
import com.jersuen.im.util.LogUtils;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.IOException;

/**
 * 登陆
 * @author JerSuen
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private EditText inAccount, inPassword;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }
    public LoginFragment() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        // 账户输入
        inAccount = (EditText) view.findViewById(R.id.fragment_login_input_account);
        // 密码输入
        inPassword = (EditText) view.findViewById(R.id.fragment_login_input_password);
        view.findViewById(R.id.fragment_login_btn_login).setOnClickListener(this);
        return view;
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_login_btn_login:
                String accountStr = inAccount.getText().toString().trim();
                String passwordStr = inPassword.getText().toString().trim();

                if (TextUtils.isEmpty(accountStr) && TextUtils.isEmpty(passwordStr)) {
                    Toast.makeText(getActivity(), "账户和密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(accountStr)) {
                    Toast.makeText(getActivity(), "请输入账户", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordStr)) {
                    Toast.makeText(getActivity(), "请输入密码", Toast.LENGTH_LONG).show();
                    return;
                }

                new AsyncTask<String, Void, Integer>(){

                    private final int OK = 0;
                    private final int ERROR_ACCOUNT = 1;
                    private final int ERROR_CONNECT = 2;
                    private ProgressDialog dialog;

                    protected void onPreExecute() {
                        dialog = ProgressDialog.show(getActivity(), null, "正在验证账户,请稍后...");
                    }

                    protected Integer doInBackground(String... strings) {
                        ConnectionConfiguration config = new ConnectionConfiguration(IM.HOST, IM.PORT);
                        config.setDebuggerEnabled(true);
                        // 关闭安全模式
                        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                        XMPPConnection connection = new XMPPTCPConnection(config);
                        try {
                            connection.connect();
                            connection.login(strings[0], strings[1], getResources().getString(R.string.app_name));
                            // 1. 保存账户信息,并启动XMPP后台
                            getActivity().startService(new Intent(getActivity(), IMService.class));
                            VCard me = new VCard();
                            me.load(connection);
                            // 2. 保存账户信息
                            IM.putString(IM.ACCOUNT_JID, StringUtils.parseBareAddress(connection.getUser()));
                            IM.putString(IM.ACCOUNT_PASSWORD, inPassword.getText().toString());
                            IM.putString(IM.ACCOUNT_NICKNAME, me.getNickName());
                            IM.saveAvatar(me.getAvatar(), StringUtils.parseName(connection.getUser()));
                            return OK;
                        } catch (XMPPException e) {
                            e.printStackTrace();
                            return ERROR_ACCOUNT;
                        } catch (SmackException e) {
                            e.printStackTrace();
                            return ERROR_CONNECT;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return ERROR_CONNECT;
                        } finally {
                            connection.disconnect();
                        }
                    }
                    protected void onPostExecute(Integer integer) {
                        dialog.dismiss();
                        switch (integer) {
                            case OK:
                                // 3. 跳转
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();
                                break;
                            case ERROR_ACCOUNT:
                                Toast.makeText(getActivity(), "账户验证失败", Toast.LENGTH_LONG).show();
                                break;
                            case ERROR_CONNECT:
                                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                }.execute(inAccount.getText().toString(), inPassword.getText().toString());
                break;
        }
    }
}
