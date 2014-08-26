package com.jersuen.im.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.jersuen.im.IM;
import com.jersuen.im.IMService;
import com.jersuen.im.R;
import com.jersuen.im.provider.ContactsProvider;
import com.jersuen.im.service.aidl.Contact;
import com.jersuen.im.service.aidl.IXmppManager;
import com.jersuen.im.ui.view.RoundedImageView;
import org.jivesoftware.smack.util.Base64;
import org.jivesoftware.smack.util.StringUtils;

import java.io.File;

public class UserActivity extends Activity implements View.OnClickListener {
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
    public static final String EXTRA_ID = "account";
    private RoundedImageView avatar;
    private String account,name,nickname;
    private EditText inNickName,inName,inAccount;
    private ServiceConnection serviceConnect = new XMPPServiceConnection();
    private IXmppManager xmppManager;
    private byte[] avatarBytes;
    private AlertDialog dialog;
    private boolean isMe;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_user);
        avatar = (RoundedImageView) findViewById(R.id.activity_user_avatar);
        // 昵称
        inNickName = (EditText) findViewById(R.id.activity_user_nickname);
        // 备注
        inName = (EditText) findViewById(R.id.activity_user_name);
        // 账户
        inAccount = (EditText) findViewById(R.id.activity_user_account);
        account = getIntent().getStringExtra(EXTRA_ID);
        findViewById(R.id.activity_user_commit).setOnClickListener(this);
        avatar.setImageDrawable(IM.getAvatar(StringUtils.parseName(account)));

        if (!TextUtils.isEmpty(account)) {
            if (account.equals(IM.getString(IM.ACCOUNT_JID))) {
                // 自己头像监听
                avatar.setOnClickListener(this);
                findViewById(R.id.activity_user_avatar_layout).setOnClickListener(this);
                // 自己没有备注
                findViewById(R.id.activity_user_name_layout).setVisibility(View.GONE);
                inNickName.setText(IM.getString(IM.ACCOUNT_NICKNAME));
                isMe = true;
            } else {
                // 不可以修改好友昵称
                inNickName.setFocusable(false);
                Cursor cursor = getContentResolver().query(ContactsProvider.CONTACT_URI, null, ContactsProvider.ContactColumns.ACCOUNT + " = ?", new String[]{account}, null);
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsProvider.ContactColumns.NAME));
                    // 设置通讯录里的备注
                    inName.setText(name);
                }
                isMe = false;
            }
        }
        inAccount.setText(account);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            // 头像事件
            case R.id.activity_user_avatar:
            case R.id.activity_user_avatar_layout:
                showDialog();
                break;
            // 修改事件
            case R.id.activity_user_commit:
                if (isMe) {
                    nickname = inNickName.getText().toString().trim();
                    if (TextUtils.isEmpty(nickname)) {
                        return;
                    }
                    // 修改自己的名片
                    boolean result;
                    Contact contact = new Contact();
                    contact.name = nickname;
                    if (avatarBytes == null) {
                        contact.avatar = Base64.encodeBytes(avatarBytes);
                    }

                    try {
                        result = xmppManager.setVCard(contact);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        result = false;
                    }

                    Toast.makeText(UserActivity.this, (result) ? "修改名片成功" : "修改名片失败", Toast.LENGTH_LONG).show();
                } else {
                    name = inName.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        return;
                    }
                    // 修改好友的备注
                    boolean result;
                    try {
                        result = xmppManager.setRosterEntryName(account, name);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        result = false;
                    }
                    Toast.makeText(UserActivity.this, (result) ? "修改备注成功" : "修改备注失败", Toast.LENGTH_LONG).show();
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
                IM.doCropPhoto(UserActivity.this,Uri.fromFile(tempFile),picCode);
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
                                IM.doCropPhoto(UserActivity.this, uri, picCode);
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


    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, IMService.class), serviceConnect, BIND_AUTO_CREATE);
    }

    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnect);
    }

    /**连接服务*/
    private class XMPPServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            xmppManager = IXmppManager.Stub.asInterface(iBinder);
            if (!isMe) {
                try {
                    Contact contact = xmppManager.getVCard(account);
                    if (contact != null) {
                        String nickName = contact.name;
                        if (!TextUtils.isEmpty(nickName)) {
                            inNickName.setText(nickName);
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            xmppManager = null;
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
}
