package com.jersuen.im.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.jersuen.im.R;
import com.jersuen.im.ui.fragment.ContactsFragment;
import com.jersuen.im.ui.fragment.ContactsFragment.Item;
import com.jersuen.im.ui.view.PinnedSectionListView.PinnedSectionListAdapter;

/** 联系人适配器 */
public class ContactsAdapter extends ArrayAdapter<ContactsFragment.Item> implements PinnedSectionListAdapter, SectionIndexer {

	public ContactsAdapter(Context context, int resource) {
		super(context, resource);
	}

	// 标题颜色
	private static final int[] COLORS = new int[] { R.color.green_light, R.color.orange_light, R.color.blue_light, R.color.red_light };
	private ContactsFragment.Item[] sections;

	public ContactsAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (TextView) super.getView(position, convertView, parent);
		view.setTextColor(Color.DKGRAY);
		view.setTag("" + position);
		ContactsFragment.Item item = getItem(position);
		if (item.type == ContactsFragment.Item.SECTION) {
			view.setBackgroundColor(parent.getResources().getColor(COLORS[item.sectionPosition % COLORS.length]));
		}
		return view;
	}

	public void prepareSections(int sectionsNumber) {
		sections = new ContactsFragment.Item[sectionsNumber];
	}

	public void onSectionAdded(ContactsFragment.Item section, int sectionPosition) {
		sections[sectionPosition] = section;
	}

	public ContactsFragment.Item[] getSections() {
		return sections;
	}

	public int getPositionForSection(int section) {
		if (section >= sections.length) {
			section = sections.length - 1;
		}
		return sections[section].listPosition;
	}

	public int getSectionForPosition(int position) {
		if (position >= getCount()) {
			position = getCount() - 1;
		}
		return getItem(position).sectionPosition;
	}

	public int getViewTypeCount() {
		return 2;
	}

	public int getItemViewType(int position) {
		return getItem(position).type;
	}

	public boolean isItemViewTypePinned(int viewType) {
		return viewType == Item.SECTION;
	}
}