package com.jersuen.im.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.jersuen.im.IM;
import com.jersuen.im.R;
import com.jersuen.im.ui.view.RoundedImageView;
import org.jivesoftware.smack.util.StringUtils;

public class UserActivity extends Activity implements View.OnClickListener {
    public static final String EXTRA_ID = "account";
    private RoundedImageView avatar;
    private String account;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_user);
        avatar = (RoundedImageView) findViewById(R.id.activity_user_account_avatar);
        account = getIntent().getStringExtra(EXTRA_ID);
        if (!TextUtils.isEmpty(account)) {
            if (account.equals(IM.getString(IM.ACCOUNT_JID))) {
                avatar.setOnClickListener(this);
                findViewById(R.id.activity_user_account_layout).setOnClickListener(this);
                avatar.setImageDrawable(IM.getAvatar(StringUtils.parseName(account)));
            }
        }
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_user_account_avatar:
            case R.id.activity_user_account_layout:
                break;
        }
    }
}
