package com.jersuen.im.ui.fragment;



import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import com.jersuen.im.IM;
import com.jersuen.im.R;
import com.jersuen.im.provider.ContactProvider;
import com.jersuen.im.ui.adapter.ContactAdapter;

/**
 * 联系人列表
 *@author JerSuen
 */
public class ContactFragment extends Fragment {

    private ListView listView;

    private ContactAdapter adapter;

    private ContentObserver co;

    public ContactFragment() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ContactAdapter();
        // 内容观察者
        co = new ContentObserver(null) {
            public void onChange(boolean selfChange) {
                Cursor cursor = getActivity().getContentResolver().query(ContactProvider.CONTACT_URI,null,null,null,null);
                adapter.changeCursor(cursor);
            }
        };
        // 注册内容观察者
        getActivity().getContentResolver().registerContentObserver(ContactProvider.CONTACT_URI, true, co);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        listView = (ListView) view.findViewById(R.id.fragment_contact_list);
        listView.setAdapter(adapter);
        return view;
    }

    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(co);
    }
}
