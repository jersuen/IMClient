package com.jersuen.im.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.jersuen.im.ui.fragment.ContactFragment;
import com.jersuen.im.ui.fragment.SessionFragment;

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
                fragment = new SessionFragment();
                break;
            case 1:
                fragment = new ContactFragment();
                break;
        }
        return fragment;
    }

    public int getCount() {
        return 2;
    }
}
