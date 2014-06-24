package com.jersuen.im.ui.fragment;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
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
import com.jersuen.im.provider.ContactsProvider.ContactColumns;
import com.jersuen.im.service.Contact;
import com.jersuen.im.ui.ChatActivity;
import com.jersuen.im.ui.adapter.ContactsAdapter;
import com.jersuen.im.util.LogUtils;

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
        Cursor cursor = getActivity().getContentResolver().query(ContactsProvider.CONTACT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            initContacts();
        }
        // 内容观察者
        co = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                initContacts();
                LogUtils.LOGD(ContactsFragment.class, "selfChange() : " + selfChange);
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
        getListView().setFastScrollEnabled(true);
        getListView().setFastScrollAlwaysVisible(true);
        getListView().setOnItemClickListener(this);
    }

    public void onDestroy() {
        super.onDestroy();
        // 移除观察者
        getActivity().getContentResolver().unregisterContentObserver(co);
    }

    /** 初始化联系人*/
    private void initContacts() {
        if (adapter == null) {
            // 初始化联系人适配器
            adapter = new ContactsAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1);
        } else {
            adapter.clear();
            adapter.notifyDataSetChanged();
        }

        Cursor group = getActivity().getContentResolver().query(ContactsProvider.CONTACT_GROUP_URI, null, null, null, ContactColumns.SECTION);
        // 组
        if (group != null && group.moveToFirst()) {
            int sectionPosition = 0, listPosition = 0;
            // 标题大小
            adapter.prepareSections(group.getCount());
            for (int i = 0; i < group.getCount(); i++) {
                group.moveToPosition(i);
                String index = group.getString(group.getColumnIndex(ContactColumns.SECTION));
                // 初始化标题
                Item section = new Item(Item.SECTION, index, null);
                section.sectionPosition = sectionPosition;
                section.listPosition = listPosition++;
                adapter.onSectionAdded(section, sectionPosition);
                adapter.add(section);
                Cursor entry = getActivity().getContentResolver().query(ContactsProvider.CONTACT_URI, null, ContactColumns.SECTION + " = ?", new String[] { index }, ContactColumns.SORT);
                // 成员
                if (entry != null && entry.moveToFirst()) {
                    for (int j = 0; j < entry.getCount(); j++) {
                        entry.moveToPosition(j);
                        String avatar = entry.getString(entry.getColumnIndex(ContactColumns.AVATAR));
                        String name = entry.getString(entry.getColumnIndex(ContactColumns.NICKNAME));
                        String account = entry.getString(entry.getColumnIndex(ContactColumns.ACCOUNT));
                        String sort = entry.getString(entry.getColumnIndex(ContactColumns.SORT));

                        Contact contact = new Contact();
                        contact.account = account;
                        contact.avatar = avatar;
                        contact.name = name;
                        contact.sort = sort;
                        contact.index = index;

                        Item item = new Item(Item.ITEM, name, contact);
                        item.sectionPosition = sectionPosition;
                        item.listPosition = listPosition++;
                        adapter.add(item);
                    }
                }
                entry.close();
                sectionPosition++;
            }
            group.close();
        }

        // 适配器有内容
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public static class Item {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final String text;
        public final Contact contact;
        public int sectionPosition;
        public int listPosition;

        public Item(int type, String text, Contact contact) {
            this.type = type;
            this.text = text;
            this.contact = contact;
        }

        public String toString() {
            return text;
        }

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = adapter.getItem(position);
        if(item.contact != null) {
            startActivity(new Intent(getActivity(), ChatActivity.class).putExtra(ChatActivity.EXTRA_CONTACT, item.contact));
        }
    }
}
