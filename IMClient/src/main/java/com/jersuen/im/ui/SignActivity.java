package com.jersuen.im.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import com.jersuen.im.R;
import com.jersuen.im.ui.adapter.SignViewAdapter;

public class SignActivity extends FragmentActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private SignViewAdapter adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_sign);
        findViewById(R.id.activity_sign_next_btn).setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.activity_sign_view_pager);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        adapter = new SignViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_sign_next_btn:
                viewPager.setCurrentItem(1);
                break;
        }
    }
}
