package com.jersuen.im;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.jersuen.im.service.LoginAsyncTask;
import com.jersuen.im.service.aidl.IXmppBinder;
import com.jersuen.im.ui.UserActivity;
import com.jersuen.im.ui.adapter.FragmentAdapter;
import com.jersuen.im.ui.view.PageIndicator;


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
    private IXmppBinder binder;
    private LoginAsyncTask loginTask = new LoginTask();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
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

    /**
     * 服务连接
     */
    private class LoginServiceConnect implements ServiceConnection {

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = IXmppBinder.Stub.asInterface(iBinder);
            loginTask.execute(binder);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
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
