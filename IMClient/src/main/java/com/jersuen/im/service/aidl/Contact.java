package com.jersuen.im.service.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 联系人模型
 * @author JerSuen
 */
public class Contact implements Parcelable{
	
	public String avatar,account,name,sort,index;

	public static final Creator<Contact> CREATOR = new Creator<Contact>() {

		public Contact createFromParcel(Parcel source) {
			Contact contact = new Contact();
			contact.avatar = source.readString();
			contact.account = source.readString();
			contact.name = source.readString();
			contact.sort = source.readString();
			contact.index = source.readString();
			return contact;
		}

		public Contact[] newArray(int size) {
			return new Contact[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(avatar);
		dest.writeString(account);
		dest.writeString(name);
		dest.writeString(sort);
		dest.writeString(index);
	}
}
