package net.supudo.apps.aNeolog.Words;

import java.util.ArrayList;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import net.supudo.apps.aNeolog.CommonSettings;
import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.TableActivity;
import net.supudo.apps.aNeolog.DataAdapters.WordsAdapter;
import net.supudo.apps.aNeolog.Database.DataHelper;
import net.supudo.apps.aNeolog.Database.Models.WordModel;

public class SearchResults extends TableActivity {

	private ArrayList<WordModel> listItems;
	private AdView adView;
	private TextView txtEmpty;
	private DataHelper dbHelper;
	private String searchQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_results);

		txtEmpty = (TextView)this.getListView().getEmptyView();
		txtEmpty.setText("");

		adView = new AdView(this, AdSize.BANNER, CommonSettings.GoogleAddsAppID);
		LinearLayout layout = (LinearLayout)findViewById(R.id.search_results_layout);
		layout.addView(adView);
		AdRequest request = new AdRequest();
		request.addTestDevice(AdRequest.TEST_EMULATOR);
		adView.loadAd(request);
	}
    
    @Override
    public void onStart() {
    	super.onStart();
		
		if (dbHelper == null)
			dbHelper = new DataHelper(this);

		Intent starting_intent = getIntent();
		Bundle extra = starting_intent.getExtras();
		if (extra != null)
			searchQuery = extra.getString("searchQuery");
		setTitle(searchQuery);

		LoadWords();
    }

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		if (adView != null)
			adView.destroy();
		super.onDestroy();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.word_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		WordModel w = (WordModel)listItems.get((int)info.id);
		switch (item.getItemId()) {
			case R.id.view:
				ViewWord(w.WordID, w.Word);
				return true;
			case R.id.viewcomments:
				ViewComments(w.WordID, w.Word);
				return true;
			case R.id.sendcomment:
				SendComment(w.WordID, w.Word);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	private void reloadItems() {
		if (listItems != null)
			listItems.clear();

		listItems = dbHelper.searchWords(searchQuery);
	}
	
	private void LoadWords() {
		reloadItems();

		if (listItems.size() == 0)
			txtEmpty.setText(getString(R.string.no_data));
		else {
    		setListAdapter(new WordsAdapter(SearchResults.this, R.layout.list_item, listItems));

    		ListView lv = getListView();
    		lv.setTextFilterEnabled(true);
    		registerForContextMenu(lv);

    		lv.setOnItemClickListener(new OnItemClickListener() {
    			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    				Integer wid = (Integer)((TextView)view.findViewById(R.id.title)).getTag();
    				String word = ((TextView)view.findViewById(R.id.title)).getText().toString(); 
    				ViewWord(wid, word);
    			}
    		});
		}
	}
	
	private void ViewWord(Integer wid, String word) {
		Toast.makeText(getApplicationContext(), word, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent().setClass(SearchResults.this, WordDetails.class);
		intent.putExtra("wordid", wid);
		startActivity(intent);
	}
	
	private void ViewComments(Integer wid, String word) {
		Toast.makeText(getApplicationContext(), word, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent().setClass(SearchResults.this, WordComments.class);
		intent.putExtra("wordid", wid);
		startActivity(intent);
	}
	
	private void SendComment(Integer wid, String word) {
		Toast.makeText(getApplicationContext(), word, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent().setClass(SearchResults.this, SendComment.class);
		intent.putExtra("wordid", wid);
		startActivity(intent);
	}

}
