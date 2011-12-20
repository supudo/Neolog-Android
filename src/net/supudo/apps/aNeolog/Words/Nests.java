package net.supudo.apps.aNeolog.Words;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import net.supudo.apps.aNeolog.DataAdapters.NestsAdapter;
import net.supudo.apps.aNeolog.Database.DataHelper;
import net.supudo.apps.aNeolog.Database.Models.NestModel;
import net.supudo.apps.aNeolog.CommonSettings;
import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.TableActivity;

public class Nests extends TableActivity {
	
	private ArrayList<NestModel> listItems;
	private AdView adView;
	private TextView txtEmpty;
	private DataHelper dbHelper;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nests);
		setTitle(R.string.title_LettersAndNests);

		txtEmpty = (TextView)this.getListView().getEmptyView();
		txtEmpty.setText("");

		adView = new AdView(this, AdSize.BANNER, CommonSettings.GoogleAddsAppID);
		LinearLayout layout = (LinearLayout)findViewById(R.id.nests_layout);
		layout.addView(adView);
		AdRequest request = new AdRequest();
		request.addTestDevice(AdRequest.TEST_EMULATOR);
		adView.loadAd(request);
		
		if (dbHelper == null)
			dbHelper = new DataHelper(this);

		reloadItems();

		if (listItems.size() == 0)
			txtEmpty.setText(getString(R.string.no_data));
		else {
			setListAdapter(new NestsAdapter(Nests.this, R.layout.list_item, listItems));

			listView = getListView();
			listView.setTextFilterEnabled(true);

    		listView.setTextFilterEnabled(true);
    		listView.setOnItemClickListener(new OnItemClickListener() {
    			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    				Integer nid = (Integer)((TextView)view.findViewById(R.id.title)).getTag();
    				String title = ((TextView)view.findViewById(R.id.title)).getText().toString(); 
    				ViewNest(nid, title);
    			}
    		});
		}
	}
    
    @Override
    public void onStart() {
    	super.onStart();
    	reloadItems();
    	setListAdapter(new NestsAdapter(Nests.this, R.layout.list_item, listItems));
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
	
	private void reloadItems() {
		listItems = dbHelper.selectNests();
	}
	
	private void ViewNest(Integer nid, String title) {
		Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent().setClass(Nests.this, Words.class);
		intent.putExtra("nestid", nid);
		intent.putExtra("lettrPos", 0);
		startActivity(intent);
	}
}
