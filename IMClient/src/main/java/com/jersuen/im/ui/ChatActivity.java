package com.jersuen.im.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.jersuen.im.IMService;
import com.jersuen.im.R;
import com.jersuen.im.provider.SMSProvider;
import com.jersuen.im.provider.SMSProvider.SMSColumns;
import com.jersuen.im.service.aidl.Contact;
import com.jersuen.im.service.aidl.IXmppManager;
import com.jersuen.im.ui.adapter.ChatAdapter;

/**
 * 单聊界面
 * @author JerSuen
 */
public class ChatActivity extends FragmentActivity implements OnClickListener {
	public static final String EXTRA_CONTACT = "contact";
	private Contact contact;
	private ListView listView;
	private EditText input;
	private ServiceConnection serviceConnect = new XmppServiceConnect();
	private ChatAdapter adapter;
	private ContentObserver co;
	private Cursor cursor;
    private IXmppManager xmppManager;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		listView = (ListView) findViewById(R.id.activity_chat_list);
		input = (EditText) findViewById(R.id.activity_chat_send_input);
		findViewById(R.id.activity_chat_send_btn).setOnClickListener(this);

		// 传递的联系人
		contact = getIntent().getParcelableExtra(EXTRA_CONTACT);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(contact.name);
        // 改变输入框
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
		// 装配适配器
		adapter = new ChatAdapter(contact.account);
        // 给适配器设置监听器
        adapter.setOnChatViewClickListener(this);
		listView.setAdapter(adapter);

		// 内容观察者
		co = new ContentObserver(new Handler()) {
			public void onChange(boolean selfChange) {
				Cursor cursor = getContentResolver().query(SMSProvider.SMS_URI, null, SMSColumns.SESSION_ID + " = ?", new String[]{contact.account}, null);
				//adapter.changeCursor(cursor);
                adapter.swapCursor(cursor);
			}
		};
		
		// 注册观察者
		getContentResolver().registerContentObserver(SMSProvider.SMS_URI, true, co);
	}

	public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_chat_send_btn:
                String bodyStr = input.getText().toString();
                if (!TextUtils.isEmpty(bodyStr)) {
                    try {
                        xmppManager.sendMessage(contact.account, contact.name, bodyStr, "chat");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    // 清空输入框
                    input.setText(null);
                }
                break;
            case R.id.activity_chat_item_avatar:
                startActivity(new Intent(ChatActivity.this, UserActivity.class).putExtra(UserActivity.EXTRA_ID, v.getTag().toString()));
                break;
        }
	}

	/** XMPP连接服务 */
	private class XmppServiceConnect implements ServiceConnection {
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
        getContentResolver().unregisterContentObserver(co);
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
}
