package com.jersuen.im.ui.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.jersuen.im.IM;
import com.jersuen.im.MainActivity;
import com.jersuen.im.R;
import org.jivesoftware.smack.*;

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
        if (getArguments() != null) {
        }
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
                new AsyncTask<String, Void, Boolean>(){
                    protected Boolean doInBackground(String... strings) {
                        ConnectionConfiguration config = new ConnectionConfiguration(IM.HOST, IM.PORT);
                        config.setDebuggerEnabled(true);
                        // 关闭安全模式
                        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                        XMPPConnection connection = new XMPPTCPConnection(config);
                        try {
                            connection.connect();
                            connection.login(strings[0], strings[1]);
                        } catch (XMPPException e) {
                            e.printStackTrace();
                            return false;
                        } catch (SmackException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            connection.disconnect();
                        }
                        return true;
                    }

                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            // 1. 保存账户信息
                            // 2. 跳转
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                        }
                    }
                }.execute(inAccount.getText().toString(), inPassword.getText().toString());
                break;
        }
    }
}
