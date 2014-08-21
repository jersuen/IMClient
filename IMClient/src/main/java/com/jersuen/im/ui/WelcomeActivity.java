package com.jersuen.im.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import com.jersuen.im.R;
import com.jersuen.im.ui.view.SecretTextView;

/**
 * 欢迎界面
 * @author JerSuen
 */
public class WelcomeActivity extends Activity {
    private SecretTextView hintView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        hintView = (SecretTextView) findViewById(R.id.activity_welcome_hint);
        findViewById(R.id.activity_welcome_hint).startAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_hint_bottom_in));
        hintView.setmDuration(1500);
        hintView.toggle();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                WelcomeActivity.this.finish();
            }
        }, 1000);
    }
}
