package com.jersuen.im;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.jersuen.im.ui.adapter.FragmentAdapter;
import com.jersuen.im.ui.view.PageIndicator;


/**
 * 主界面
 * @author JerSuen
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private PageIndicator indicator;
    private FragmentPagerAdapter adapter;

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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
