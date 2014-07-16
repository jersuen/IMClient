package com.jersuen.im.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jersuen.im.IM;
import com.jersuen.im.R;
import com.jersuen.im.provider.SMSProvider.SMSColumns;
import com.jersuen.im.ui.view.RoundedImageView;
import org.jivesoftware.smack.util.StringUtils;

/**
 * 单聊适配器
 * @author JerSuen
 */
public class ChatAdapter extends CursorAdapter{
	private final int ITEM_RIGHT = 0;
	private final int ITEM_LEFT = 1;

	public ChatAdapter(Cursor cursor) {
		super(
				IM.im,
				cursor,
				FLAG_REGISTER_CONTENT_OBSERVER);
	}

	public View getView(int position, View view, ViewGroup group) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            switch (getItemViewType(position)) {
                case ITEM_LEFT:
                    view = LayoutInflater.from(group.getContext()).inflate(R.layout.activity_chat_item_left, null);
                    break;
                case ITEM_RIGHT:
                    view = LayoutInflater.from(group.getContext()).inflate(R.layout.activity_chat_item_right, null);
                    break;
            }
            holder.avatar = (RoundedImageView) view.findViewById(R.id.activity_chat_item_avatar);
            holder.content = (TextView) view.findViewById(R.id.activity_chat_item_content);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // 装配
		Cursor cursor = (Cursor) getItem(position);
		String bodyStr = cursor.getString(cursor.getColumnIndex(SMSColumns.BODY));
		String account = cursor.getString(cursor.getColumnIndex(SMSColumns.WHO_ID));
        holder.content.setText(bodyStr);
        holder.avatar.setImageDrawable(IM.getAvatar(StringUtils.parseName(account)));
		return view;
		
	}

    public int getViewTypeCount() {
		return ITEM_RIGHT + ITEM_LEFT;
	}

	public int getItemViewType(int position) {
		Cursor cursor = (Cursor) getItem(position);
		String whoJid = cursor.getString(cursor.getColumnIndex(SMSColumns.WHO_ID));
        // 用户判断
		if (IM.getString(IM.ACCOUNT_JID).equals(whoJid)) {
			return ITEM_RIGHT;
		} else {
			return ITEM_LEFT;
		}
	}

    public View newView(Context context, Cursor cursor, ViewGroup parent) {return null;}

    public void bindView(View view, Context context, Cursor cursor) {}

    private static class ViewHolder {
        TextView content;
        RoundedImageView avatar;
    }
}
