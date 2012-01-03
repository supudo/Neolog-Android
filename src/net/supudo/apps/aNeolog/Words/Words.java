package net.supudo.apps.aNeolog.Words;

import java.util.ArrayList;

import net.supudo.apps.aNeolog.CommonSettings;
import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.TableActivity;
import net.supudo.apps.aNeolog.DataAdapters.WordsAdapter;
import net.supudo.apps.aNeolog.Database.DataHelper;
import net.supudo.apps.aNeolog.Database.Models.WordModel;
import net.supudo.apps.aNeolog.Synchronization.SyncManager;
import net.supudo.apps.aNeolog.Synchronization.SyncManager.SyncManagerCallbacks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class Words extends TableActivity implements Runnable, SyncManagerCallbacks {
	
	private ArrayList<WordModel> listItems;
	private AdView adView;
	private ProgressDialog loadingDialog;
	private SyncManager syncManager;
	private TextView txtEmpty;
	private DataHelper dbHelper;
	private int nestID, letterPos;
	
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	loadingDialog.dismiss();
        	
        	if (msg.getData().isEmpty())
        		LoadWords();
        	else {
        		AlertDialog.Builder alertbox = new AlertDialog.Builder(Words.this);
        		alertbox.setMessage(msg.getData().getString("exception"));
        		alertbox.setNeutralButton(R.string.close_alertbox, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                alertbox.show();
        	}
        }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.words);

		txtEmpty = (TextView)this.getListView().getEmptyView();
		txtEmpty.setText("");

		adView = new AdView(this, AdSize.BANNER, CommonSettings.GoogleAddsAppID);
		LinearLayout layout = (LinearLayout)findViewById(R.id.words_layout);
		layout.addView(adView);
		AdRequest request = new AdRequest();
		request.addTestDevice(AdRequest.TEST_EMULATOR);
		adView.loadAd(request);

    	nestID = 0;
    	letterPos = 0;
	}
    
    @Override
    public void onStart() {
    	super.onStart();
		
		if (dbHelper == null)
			dbHelper = new DataHelper(this);

		if (syncManager == null)
			syncManager = new SyncManager(this, this);

		Intent starting_intent = getIntent();
		Bundle extra = starting_intent.getExtras();
		if (extra != null) {
			nestID = extra.getInt("nestid");
			letterPos = extra.getInt("lettrPos"); 
		}

		if (nestID > 0)
			setTitle(dbHelper.GetNestName(nestID).Title);
		else
			setTitle(CommonSettings.Letters[letterPos]);

		if (CommonSettings.ShouldSyncWords(Words.this, letterPos, nestID)) {
			loadingDialog = ProgressDialog.show(this, "", getString(R.string.loading), true);
			Thread thread = new Thread(this);
			thread.run();
		}
		else
			LoadWords();
    }

	@Override
	protected void onPause() {
		DestroySyncManager();
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		if (adView != null)
			adView.destroy();
		DestroySyncManager();
		super.onDestroy();
	}

	@Override
	public void syncFinished() {
		CommonSettings.AddToCache(Words.this, letterPos, nestID, 0);
        handler.sendEmptyMessage(0);
	}

	@Override
	public void onSyncProgress(int progress) {
	}

	@Override
	public void onSyncError(Exception ex) {
		Log.d("Words", ex.getMessage());
		Message msg = handler.obtainMessage(); 
        Bundle b = new Bundle();
        b.putString("exception", ex.getMessage()); 
        msg.setData(b); 
        handler.handleMessage(msg);
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

	public void run() {
		if (nestID > 0)
			syncManager.GetWordsForNest(nestID);
		else
			syncManager.GetWordsForLetter(((letterPos == CommonSettings.Letters.length - 1) ? "x" : CommonSettings.Letters[letterPos]));
	}
	
	private void DestroySyncManager() {
		if (syncManager != null) {
			syncManager.cancel();
			syncManager = null;
		}
	}
	
	private void reloadItems() {
		if (listItems != null)
			listItems.clear();

		if (nestID > 0)
			listItems = dbHelper.selectWordsForNest(nestID);
		else
			listItems = dbHelper.selectWordsForLetter(((letterPos == CommonSettings.Letters.length - 1) ? "x" : CommonSettings.Letters[letterPos]));
	}
	
	private void LoadWords() {
		reloadItems();

		if (listItems.size() == 0)
			txtEmpty.setText(getString(R.string.no_data));
		else {
    		setListAdapter(new WordsAdapter(Words.this, R.layout.list_item, listItems));

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
		Intent intent = new Intent().setClass(Words.this, WordDetails.class);
		intent.putExtra("wordid", wid);
		startActivity(intent);
	}
	
	private void ViewComments(Integer wid, String word) {
		Toast.makeText(getApplicationContext(), word, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent().setClass(Words.this, WordComments.class);
		intent.putExtra("wordid", wid);
		startActivity(intent);
	}
	
	private void SendComment(Integer wid, String word) {
		Toast.makeText(getApplicationContext(), word, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent().setClass(Words.this, SendComment.class);
		intent.putExtra("wordid", wid);
		startActivity(intent);
	}
}