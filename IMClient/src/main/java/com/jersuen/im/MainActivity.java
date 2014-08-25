package com.jersuen.im;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.Toast;
import com.jersuen.im.service.LoginAsyncTask;
import com.jersuen.im.service.aidl.IXmppManager;
import com.jersuen.im.ui.AddActivity;
import com.jersuen.im.ui.UserActivity;
import com.jersuen.im.ui.adapter.FragmentAdapter;
import com.jersuen.im.ui.view.PageIndicator;

import java.lang.reflect.Field;


/**
 * 主界面
 *
 * @author JerSuen
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private PageIndicator indicator;
    private FragmentPagerAdapter adapter;
    private ServiceConnection serviceConnect = new LoginServiceConnect();
    private IXmppManager xmppManager;
    private LoginAsyncTask loginTask = new LoginTask();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceShowOverflowMenu();
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.activity_main_pager);
        adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        indicator = (PageIndicator) findViewById(R.id.activity_main_pager_indicator);
        indicator.setViewPager(viewPager);

        findViewById(R.id.activity_main_btn_contact).setOnClickListener(this);
        findViewById(R.id.activity_main_btn_session).setOnClickListener(this);

    }


    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, IMService.class), serviceConnect, BIND_AUTO_CREATE);
    }

    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnect);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            // 发送home action
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_add:
                startActivity(new Intent(this, AddActivity.class));
                return true;
            case R.id.action_account:
                startActivity(new Intent(this, UserActivity.class).putExtra(UserActivity.EXTRA_ID, IM.getString(IM.ACCOUNT_JID)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_main_btn_session:
                viewPager.setCurrentItem(0);
                break;
            case R.id.activity_main_btn_contact:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    private void forceShowOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务连接
     */
    private class LoginServiceConnect implements ServiceConnection {

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            xmppManager = IXmppManager.Stub.asInterface(iBinder);
            loginTask.execute(xmppManager);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            xmppManager = null;
        }
    }

    private class LoginTask extends LoginAsyncTask {
        protected void onPostExecute(Integer integer) {
            switch (integer) {
                case LoginAsyncTask.LOGIN_OK:
                    Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_LONG).show();
                    break;
                case LoginAsyncTask.LOGIN_ERROR:
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                    break;
                case LoginAsyncTask.CONNECTION_ERROR:
                    Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
