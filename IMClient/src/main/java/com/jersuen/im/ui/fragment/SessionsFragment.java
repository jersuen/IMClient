package com.jersuen.im.ui.fragment;



import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jersuen.im.R;
import com.jersuen.im.provider.SMSProvider;
import com.jersuen.im.ui.adapter.SessionsAdapter;

/**
 * 会话列表
 * @author JerSuen
 */
public class SessionsFragment extends ListFragment {

    private SessionsAdapter adapter;
    private ContentObserver co;
    public SessionsFragment() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter  = new SessionsAdapter();

        // 内容观察者
        co = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                Cursor cursor = getActivity().getContentResolver().query(SMSProvider.SMS_SESSIONS_URI, null, null, null, null);
                // 数据库内容改变，刷新适配器
                adapter.changeCursor(cursor);
            }
        };

        // 注册内容观察者
        getActivity().getContentResolver().registerContentObserver(SMSProvider.SMS_URI, true, co);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sessions, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setAdapter(adapter);
    }


    public void onDestroy() {
        // 销毁内容观察者
        getActivity().getContentResolver().unregisterContentObserver(co);
        super.onDestroy();
    }
}
