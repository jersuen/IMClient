package com.jersuen.im.ui.fragment;

import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.jersuen.im.R;
import com.jersuen.im.provider.ContactsProvider;
import com.jersuen.im.ui.ChatActivity;
import com.jersuen.im.ui.adapter.ContactsAdapter;
import com.jersuen.im.ui.adapter.ContactsAdapter.Item;
import com.jersuen.im.ui.view.PinnedSectionListView;

/**
 * 联系人列表
 * @author JerSuen
 */
public class ContactsFragment extends ListFragment implements OnItemClickListener {
    private ContactsAdapter adapter;
    private ContentObserver co;

    public ContactsFragment() {}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 内容观察者
        co = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {

            }
        };
        // 注册观察者
        getActivity().getContentResolver().registerContentObserver(ContactsProvider.CONTACT_URI, true, co);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 适配器有内容
        adapter = new ContactsAdapter();
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    public void onDestroy() {
        super.onDestroy();
        // 移除观察者
        getActivity().getContentResolver().unregisterContentObserver(co);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = adapter.getItem(position);
        if (item.contact != null) {
            startActivity(new Intent(getActivity(),ChatActivity.class).putExtra(ChatActivity.EXTRA_CONTACT,item.contact));
        }
    }
}
