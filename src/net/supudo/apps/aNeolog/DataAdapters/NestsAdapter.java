package net.supudo.apps.aNeolog.DataAdapters;

import java.util.ArrayList;

import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.Database.Models.NestModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NestsAdapter extends ArrayAdapter<NestModel> {

	private LayoutInflater mInflater;
	private ArrayList<NestModel> items;

	public NestsAdapter(Context context, int textViewResourceId, ArrayList<NestModel> items) {
		super(context, textViewResourceId, items);
		mInflater = LayoutInflater.from(context);
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
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

		NestModel nest = (NestModel)items.get(position);

		holder.title.setTag(nest.NestID);
		holder.title.setText(nest.Title);
		holder.nid = nest.NestID;

		return convertView;
 	}

  	static class ViewHolder {
 		TextView title;
 		Integer nid;
 	}

}
