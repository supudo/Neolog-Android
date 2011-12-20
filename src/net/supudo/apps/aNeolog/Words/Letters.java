package net.supudo.apps.aNeolog.Words;

import net.supudo.apps.aNeolog.CommonSettings;
import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.TableActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class Letters extends TableActivity {
	
	private AdView adView;
	private TextView txtEmpty;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letters);
		setTitle(R.string.title_LettersAndNests);

		txtEmpty = (TextView)this.getListView().getEmptyView();
		txtEmpty.setText("");

		adView = new AdView(this, AdSize.BANNER, CommonSettings.GoogleAddsAppID);
		LinearLayout layout = (LinearLayout)findViewById(R.id.letters_layout);
		layout.addView(adView);
		AdRequest request = new AdRequest();
		request.addTestDevice(AdRequest.TEST_EMULATOR);
		adView.loadAd(request);

		ArrayAdapter<String> _adapter = new ArrayAdapter<String>(Letters.this, R.layout.list_item, CommonSettings.Letters);

		listView = getListView();
		listView.setTextFilterEnabled(true);
		listView.setAdapter(_adapter);

		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ViewNest(position);
			}
		});
	}
    
    @Override
    public void onStart() {
    	super.onStart();
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
	
	private void ViewNest(Integer pos) {
		String letter = CommonSettings.Letters[pos];
		Toast.makeText(getApplicationContext(), letter, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent().setClass(Letters.this, Words.class);
		intent.putExtra("nestid", 0);
		intent.putExtra("lettrPos", pos);
		startActivity(intent);
	}
}
