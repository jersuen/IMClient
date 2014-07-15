package com.jersuen.im.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.jersuen.im.R;
import com.jersuen.im.ui.view.RoundedImageView;

public class UserActivity extends Activity {

    private RoundedImageView avatar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_user);
        avatar = (RoundedImageView) findViewById(R.id.activity_user_avatar);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
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
