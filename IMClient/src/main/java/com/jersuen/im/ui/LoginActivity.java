package com.jersuen.im.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.jersuen.im.IM;
import com.jersuen.im.IMService;
import com.jersuen.im.MainActivity;
import com.jersuen.im.R;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.IOException;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText inAccount, inPassword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // 账户输入
        inAccount = (EditText) findViewById(R.id.activity_login_input_account);
        // 密码输入
        inPassword = (EditText) findViewById(R.id.activity_login_input_password);
        findViewById(R.id.activity_login_btn_login).setOnClickListener(this);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SignActivity.class), RESULT_FIRST_USER);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_login_btn_login:
                String accountStr = inAccount.getText().toString().trim();
                String passwordStr = inPassword.getText().toString().trim();

                if (TextUtils.isEmpty(accountStr) && TextUtils.isEmpty(passwordStr)) {
                    Toast.makeText(this, "账户和密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(accountStr)) {
                    Toast.makeText(this, "请输入账户", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordStr)) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_LONG).show();
                    return;
                }

                new AsyncTask<String, Void, Integer>(){

                    private final int OK = 0;
                    private final int ERROR_ACCOUNT = 1;
                    private final int ERROR_CONNECT = 2;
                    private ProgressDialog dialog;

                    protected void onPreExecute() {
                        dialog = ProgressDialog.show(LoginActivity.this, null, "正在验证账户,请稍后...");
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
                            LoginActivity.this.startService(new Intent(LoginActivity.this, IMService.class));
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
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                LoginActivity.this.finish();
                                break;
                            case ERROR_ACCOUNT:
                                Toast.makeText(LoginActivity.this, "账户验证失败", Toast.LENGTH_LONG).show();
                                break;
                            case ERROR_CONNECT:
                                Toast.makeText(LoginActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                }.execute(inAccount.getText().toString(), inPassword.getText().toString());
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            LoginActivity.this.finish();
        }
    }
}
