package com.jersuen.im.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.jersuen.im.IMService;
import com.jersuen.im.R;
import com.jersuen.im.service.aidl.Contact;
import com.jersuen.im.service.aidl.IXmppManager;
import com.jersuen.im.ui.adapter.AddViewAdapter;
import com.jersuen.im.ui.view.RoundedImageView;
import org.jivesoftware.smack.util.Base64;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加好友
 * @author JerSuen
 */
public class AddActivity extends Activity implements View.OnClickListener {
    private ViewPager viewPager;
    private View searchView,examineView;
    private AddViewAdapter adapter;
    private ServiceConnection serviceConnect = new XMPPServiceConnection();
    private IXmppManager xmppManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_add);
        viewPager = (ViewPager) findViewById(R.id.activity_add_view_pager);
        // 禁止滑动
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        searchView = getLayoutInflater().inflate(R.layout.activity_add_view_search_account, null);
        examineView = getLayoutInflater().inflate(R.layout.activity_add_view_examine_account, null);

        List<View> views = new ArrayList<View>();
        views.add(searchView);
        views.add(examineView);

        adapter = new AddViewAdapter(views);
        // 适配器内容监听器
        adapter.setOnSignViewClickListener(this);
        viewPager.setAdapter(adapter);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_add_view_search_account_commit:
                String accountStr = ((EditText)searchView.findViewById(R.id.activity_add_view_search_account_input)).getText().toString().trim();
                if (TextUtils.isEmpty(accountStr) || xmppManager == null) {
                    return;
                }
                try {
                    String jidStr = xmppManager.searchAccount(accountStr);
                    if (!TextUtils.isEmpty(jidStr)) {
                        viewPager.setCurrentItem(1);
                        Contact contact = xmppManager.getVCard(jidStr);
                        byte[] bytes = Base64.decode(contact.avatar);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);

                        examineView.findViewById(R.id.activity_user_name_layout).setVisibility(View.GONE);
                        examineView.findViewById(R.id.activity_user_commit).setVisibility(View.GONE);

                        TextView account = (TextView) examineView.findViewById(R.id.activity_user_account);
                        account.setText(contact.account);
                        account.setFocusable(false);

                        TextView name = (TextView) examineView.findViewById(R.id.activity_user_nickname);
                        name.setText(contact.name);
                        name.setFocusable(false);

                        RoundedImageView avatar = (RoundedImageView) examineView.findViewById(R.id.activity_user_avatar);
                        avatar.setImageDrawable(drawable);
                    } else {
                        Toast.makeText(AddActivity.this, "没有此用户", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.activity_add_view_examine_account_commit:
                break;
        }
    }
    /**连接服务*/
    private class XMPPServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            xmppManager = IXmppManager.Stub.asInterface(iBinder);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            xmppManager = null;
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
}
