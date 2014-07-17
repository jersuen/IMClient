package com.jersuen.im.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.jersuen.im.ui.fragment.CreateAccountFragment;
import com.jersuen.im.ui.fragment.SessionsFragment;

/**
 * 注册页适配器
 * @author JerSuen
 */
public class SignViewAdapter extends FragmentAdapter {
    public SignViewAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new CreateAccountFragment();
                break;
            case 1:
                fragment = new SessionsFragment();
                break;
        }
        return fragment;
    }

    public int getCount() {
        return 2;
    }
}
