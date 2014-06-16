package com.jersuen.im.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.jersuen.im.ui.fragment.ContactsFragment;
import com.jersuen.im.ui.fragment.SessionsFragment;

/**
 * 主界面碎片适配器
 * @author JerSuen
 */
public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new SessionsFragment();
                break;
            case 1:
                fragment = new ContactsFragment();
                break;
        }
        return fragment;
    }

    public int getCount() {
        return 2;
    }
}
