package com.jersuen.im.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.jersuen.im.R;
import com.jersuen.im.ui.fragment.LoginFragment;

/**
 * 账户（登陆+注册）
 * @author JerSuen
 */
public class AccountActivity extends FragmentActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, LoginFragment.newInstance()).commit();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
