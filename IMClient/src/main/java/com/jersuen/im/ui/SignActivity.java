package com.jersuen.im.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.jersuen.im.IM;
import com.jersuen.im.IMService;
import com.jersuen.im.MainActivity;
import com.jersuen.im.R;
import com.jersuen.im.ui.adapter.SignViewAdapter;
import com.jersuen.im.ui.view.RoundedImageView;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注册
 * @author JerSuen
 */
public class SignActivity extends Activity implements View.OnClickListener {
    /**
     * 选择照片返回码
     */
    private static final int selectCode = 123;

    /**
     * 拍照返回码
     */
    private static final int cameraCode = 124;
    /**
     * 系统裁剪返回码
     */
    private static final int picCode = 125;

    // 拍照文件
    private File tempFile;
    private ViewPager viewPager;
    private SignViewAdapter adapter;
    private View createAccount, perfectAccount, uploadAvatar;
    private AlertDialog dialog;
    private RoundedImageView avatar;
    private String accountJid, accountPassword,accountNickName;
    private XMPPConnection connection;
    private AccountManager accountManager;
    private byte[] avatarBytes;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_sign);
        viewPager = (ViewPager) findViewById(R.id.activity_sign_view_pager);
        // 禁止滑动
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        createAccount = getLayoutInflater().inflate(R.layout.activity_sign_view_create_account, null);
        uploadAvatar = getLayoutInflater().inflate(R.layout.activity_sign_view_upload_avatar, null);
        perfectAccount = getLayoutInflater().inflate(R.layout.activity_sign_view_perfect_account, null);
        avatar = (RoundedImageView) uploadAvatar.findViewById(R.id.activity_sign_view_upload_avatar_avatar);

        List<View> views = new ArrayList<View>();
        views.add(createAccount);
        views.add(perfectAccount);
        views.add(uploadAvatar);

        adapter = new SignViewAdapter(views);
        // 适配器内容监听器
        adapter.setOnSignViewClickListener(this);
        viewPager.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(final View v) {
        switch (v.getId()) {
            // 创建账户布局监听
            case R.id.activity_sign_view_create_account_commit:
                TextView account = (TextView) createAccount.findViewById(R.id.activity_sign_view_create_account_account);
                TextView password = (TextView) createAccount.findViewById(R.id.activity_sign_view_create_account_password);
                String accountStr = account.getText().toString().trim();
                String passwordStr = password.getText().toString().trim();

                if (TextUtils.isEmpty(accountStr)) {
                    Toast.makeText(SignActivity.this, "请检查账户", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordStr) || passwordStr.length() < 6) {
                    Toast.makeText(SignActivity.this, "密码不能为空，并且长度大于6位", Toast.LENGTH_LONG).show();
                    return;
                }

                // 创建账户任务
                new AsyncTask<String, Void, Boolean>(){
                    private ProgressDialog dialog;
                    protected void onPreExecute() {
                        dialog = ProgressDialog.show(SignActivity.this, null, "正在联系服务器...");
                    }

                    protected Boolean doInBackground(String... strings) {
                        connection = ConfigConnection();
                        try {
                            connection.connect();
                            accountManager = AccountManager.getInstance(connection);
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("name",strings[0]);
                            accountManager.createAccount(strings[0], strings[1], map);
                            connection.login(strings[0], strings[1]);
                            accountJid = StringUtils.parseBareAddress(connection.getUser());
                            accountPassword = strings[1];
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }

                    protected void onPostExecute(Boolean aBoolean) {
                        dialog.dismiss();
                        if (aBoolean) {
                            viewPager.setCurrentItem(1);
                        } else {
                            Toast.makeText(SignActivity.this, "服务器好像不愿意哦", Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute(accountStr, passwordStr);

                break;
            // 完善资料布局监听
            case R.id.activity_sign_view_perfect_account_commit:
                TextView nickname = (TextView) perfectAccount.findViewById(R.id.activity_sign_view_perfect_account_nickname);
                accountNickName = nickname.getText().toString().trim();
                if (TextUtils.isEmpty(accountNickName)) {
                    Toast.makeText(SignActivity.this, "昵称不能胡来", Toast.LENGTH_LONG).show();
                    return;
                }
                viewPager.setCurrentItem(2);
                break;
            // 上传头像布局监听
            case R.id.activity_sign_view_upload_avatar_avatar:
            case R.id.activity_sign_view_upload_avatar_layout:
                showDialog();
                break;
            case R.id.activity_sign_view_upload_avatar_commit:
                if (avatarBytes == null) {
                    showDialog();
                } else {
                    // 上传头像，完成注册任务
                    new AsyncTask<Void, Void, Boolean>(){
                        private ProgressDialog dialog;
                        protected void onPreExecute() {
                            dialog = ProgressDialog.show(SignActivity.this, null, "正在保存账户...");
                        }

                        protected Boolean doInBackground(Void... voids) {
                            try {
                                VCard vCard = new VCard();
                                vCard.load(connection);
                                vCard.setNickName(accountNickName);
                                vCard.setAvatar(avatarBytes);
                                vCard.save(connection);
                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return false;
                        }

                        protected void onPostExecute(Boolean aBoolean) {
                            dialog.dismiss();
                            if(aBoolean) {
                                // 1. 保存账户
                                IM.putString(IM.ACCOUNT_JID, accountJid);
                                IM.putString(IM.ACCOUNT_PASSWORD, accountPassword);
                                IM.putString(IM.ACCOUNT_NICKNAME, accountNickName);
                                IM.saveAvatar(avatarBytes, StringUtils.parseName(accountJid));

                                // 2. 启动XMPP后台
                                startService(new Intent(SignActivity.this, IMService.class));
                                // 3. 跳转
                                startActivity(new Intent(SignActivity.this, MainActivity.class));
                                // 4. 销毁登陆页面
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(SignActivity.this, "额,就差这一步了,再试一次" ,Toast.LENGTH_LONG).show();
                            }
                        }


                    }.execute();
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            // 拍照
            case cameraCode:
                // 获取照片,开始裁剪
                IM.doCropPhoto(SignActivity.this, Uri.fromFile(tempFile), picCode);
                break;
            // 图库
            case selectCode:
                Uri uri = data.getData();
                String[] pojo = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, pojo, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        String pathStr = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        if (!TextUtils.isEmpty(pathStr)) {
                            // 文件后缀判断
                            if (pathStr.endsWith("jpg") || pathStr.endsWith("png")) {
                                // 获取照片,开始裁剪
                                IM.doCropPhoto(SignActivity.this, uri, picCode);
                            }
                        }
                    }
                }
                break;
            // 裁剪
            case picCode:
                if (data != null) {
                    Bitmap photoPic = data.getParcelableExtra("data");
                    if (photoPic != null) {
                        avatar.setImageDrawable(IM.Bitmap2Drawable(photoPic));
                        avatarBytes = IM.Bitmap2Bytes(photoPic);
                    }
                }
                break;
        }
    }

    private void showDialog() {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle("选择照片")
                    .setItems(R.array.select_photo_items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    tempFile = IM.getCameraFile();
                                    // 进入拍照
                                    Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                                    startActivityForResult(intentCamera, cameraCode);
                                    break;
                                case 1:
                                    // 浏览图库
                                    Intent intentSelect = new Intent();
                                    intentSelect.setType("image/*");
                                    intentSelect.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intentSelect, selectCode);
                                    break;
                            }
                        }
                    }).create();
        }
        dialog.show();
    }

    private XMPPConnection ConfigConnection() {
        ConnectionConfiguration configuration = new ConnectionConfiguration(IM.HOST, IM.PORT);
        configuration.setDebuggerEnabled(true);
        // 关闭安全模式
        configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        return new XMPPTCPConnection(configuration);
    }

    public void onBackPressed() {
        // 退出注册判断
        if (TextUtils.isEmpty(accountJid)) {
            finish();
        } else {
            new AlertDialog.Builder(SignActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("确定放弃注册")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            try {
                                // 删除账号，必须要登录注册账号
                                accountManager.deleteAccount();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // 关闭链接
                            connection.disconnect();
                            createAccount = null;
                            connection = null;
                            SignActivity.this.finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {}
                    }).create().show();
        }
    }
}
