package com.jersuen.im.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.jersuen.im.R;

/**
 * 创建账户
 * @author JerSuen
 */
public class CreateAccountFragment extends Fragment {

    public CreateAccountFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creat_account, null);
        return view;
    }
}
