package com.jersuen.im.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import com.jersuen.im.IMService;
import com.jersuen.im.R;
import com.jersuen.im.service.aidl.IXmppBinder;
import com.jersuen.im.ui.adapter.AddViewAdapter;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

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
    private IXmppBinder binder;
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
                if (TextUtils.isEmpty(accountStr) || binder == null) {
                    return;
                }
                //UserSearchManager search = new UserSearchManager(null);
                break;
            case R.id.activity_add_view_examine_account_commit:
                break;
        }
    }
    /**连接服务*/
    private class XMPPServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = IXmppBinder.Stub.asInterface(iBinder);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
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
