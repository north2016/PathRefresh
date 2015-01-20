package com.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

	Context mContext;
	private String[] users;

	public ListAdapter(Context context, String[] mStrings) {
		mContext = context;
		this.users = mStrings;
	}

	@Override
	public int getCount() {
		return users != null ? users.length : 0;
	}

	@Override
	public Object getItem(int position) {
		if (users != null)
			return users[position];
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item, null);
		}
		TextView text = (TextView) convertView.findViewById(R.id.title);
		RoundImageView img = (RoundImageView) convertView
				.findViewById(R.id.img);
		String user = users[position];
		if (user != null)
			text.setText(user);
		//img.setImageResource(R.drawable.e);
		return convertView;
	}

}
