package net.supudo.apps.aNeolog.DataAdapters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.Database.Models.WordCommentModel;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;

public class WordCommentsAdapter extends ArrayAdapter<WordCommentModel> {

	private LayoutInflater mInflater;
	private ArrayList<WordCommentModel> items;
	private Context context;

	public WordCommentsAdapter(Context _context, int textViewResourceId, ArrayList<WordCommentModel> items) {
		super(_context, textViewResourceId, items);
		mInflater = LayoutInflater.from(_context);
		this.items = items;
		this.context = _context;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_webview, parent, false);
			holder = new ViewHolder();
			holder.content = (WebView) convertView.findViewById(R.id.web_content);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();

		WordCommentModel comm = (WordCommentModel)items.get(position);

		holder.content.setTag(comm.CommentID);

		String pDate = "";
		try {
			DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			Date date = (Date)formatter.parse(comm.CommentDate);
			int month = date.getMonth() + 1;
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			pDate += cal.get(Calendar.DAY_OF_MONTH);

			String monthLabel = "monthsShort_" + month;
			pDate += " " + (String) context.getResources().getText(context.getResources().getIdentifier(monthLabel, "string", "net.supudo.apps.aNeolog"));
			pDate += " " + cal.get(Calendar.YEAR);
		}
		catch (ParseException e) {
			Log.d("WordComments", "Date parser failed " + comm.CommentDate + " (" + comm.CommentDatestamp + ")");
		}
		
		String commHtml = "<html><head></head>";
		commHtml += "<style>body, div, p, a { color: #ffffff; } a { text-decoration: underline; }</style>";
		commHtml += "<body>";
		commHtml += "<b>" + comm.Author + " @ " + pDate + "</b><br>";
		commHtml += comm.Comment;
		commHtml += "</body></html>";
		holder.content.setBackgroundColor(0);
		holder.content.loadDataWithBaseURL("neolog://localurl", commHtml, "text/html", "utf-8", "");
		holder.cid = comm.CommentID;

		return convertView;
 	}

  	static class ViewHolder {
  		WebView content;
 		Integer cid;
 	}
}
