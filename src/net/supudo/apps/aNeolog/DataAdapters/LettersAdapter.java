package net.supudo.apps.aNeolog.DataAdapters;

import net.supudo.apps.aNeolog.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LettersAdapter extends ArrayAdapter<String> {

	private LayoutInflater mInflater;
	private String[] items;

	public LettersAdapter(Context context, int textViewResourceId, String[] items) {
		super(context, textViewResourceId, items);
		mInflater = LayoutInflater.from(context);
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_simple, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();

		String letter = (String)items[position];

		holder.title.setTag(letter);
		holder.title.setText(letter);
		holder.title.setTypeface(null, Typeface.BOLD);
		holder.title.setGravity(Gravity.CENTER);

		return convertView;
 	}

  	static class ViewHolder {
 		TextView title;
 	}

}
