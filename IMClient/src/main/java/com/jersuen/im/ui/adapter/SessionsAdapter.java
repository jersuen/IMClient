package com.jersuen.im.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import com.jersuen.im.IM;
import com.jersuen.im.R;
import com.jersuen.im.provider.SMSProvider;
import com.jersuen.im.ui.view.RoundedImageView;
import org.jivesoftware.smack.util.StringUtils;

/**
 * 会话列表
 * @author JerSuen
 */
public class SessionsAdapter extends SimpleCursorAdapter{


    public SessionsAdapter() {
        super(
                IM.im,
                R.layout.fragment_sessions_item,
                IM.im.getContentResolver().query(SMSProvider.SMS_SESSIONS_URI, null, null, null, null),
                new String[]{SMSProvider.SMSColumns.BODY, SMSProvider.SMSColumns.SESSION_NAME},
                new int[]{R.id.fragment_sessions_item_content, R.id.fragment_sessions_item_name},
                FLAG_REGISTER_CONTENT_OBSERVER);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        RoundedImageView avatar = (RoundedImageView) view.findViewById(R.id.fragment_sessions_item_avatar);
        String account = cursor.getString(cursor.getColumnIndex(SMSProvider.SMSColumns.SESSION_ID));
        avatar.setImageDrawable(IM.getAvatar(StringUtils.parseName(account)));
        super.bindView(view, context, cursor);
    }
}
