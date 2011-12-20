package net.supudo.apps.aNeolog.DataAdapters;

import java.util.ArrayList;

import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.Database.Models.WordModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WordsAdapter extends ArrayAdapter<WordModel> {

	private LayoutInflater mInflater;
	private ArrayList<WordModel> items;

	public WordsAdapter(Context context, int textViewResourceId, ArrayList<WordModel> items) {
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
			holder.word = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();

		WordModel word = (WordModel)items.get(position);

		holder.word.setTag(word.WordID);
		holder.word.setText(word.Word);
		holder.wid = word.WordID;

		return convertView;
 	}

  	static class ViewHolder {
 		TextView word;
 		Integer wid;
 	}

}
