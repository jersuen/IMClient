package com.jersuen.im.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import com.jersuen.im.R;

/**
 * 欢迎界面
 * @author JerSuen
 */
public class WelcomeActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, AccountActivity.class));
                WelcomeActivity.this.finish();
            }
        }, 1000);
    }

}
