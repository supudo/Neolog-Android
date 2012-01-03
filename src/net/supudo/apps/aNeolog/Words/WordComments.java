package net.supudo.apps.aNeolog.Words;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import net.supudo.apps.aNeolog.CommonSettings;
import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.TableActivity;
import net.supudo.apps.aNeolog.DataAdapters.WordCommentsAdapter;
import net.supudo.apps.aNeolog.Database.DataHelper;
import net.supudo.apps.aNeolog.Database.Models.WordCommentModel;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class WordComments extends TableActivity implements Runnable, SyncManagerCallbacks {

	private ArrayList<WordCommentModel> listItems;
	private AdView adView;
	private ProgressDialog loadingDialog;
	private SyncManager syncManager;
	private TextView txtEmpty;
	private DataHelper dbHelper;
	private int wordID;
	private WordModel wordEnt;
	
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	loadingDialog.dismiss();
        	
        	if (msg.getData().isEmpty())
        		LoadWordComments();
        	else {
        		AlertDialog.Builder alertbox = new AlertDialog.Builder(WordComments.this);
        		if (msg.getData().getString("exception").equals("no-data")) {
        			alertbox.setMessage(R.string.no_comments);
        			alertbox.setNeutralButton(R.string.close_alertbox, new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface arg0, int arg1) {
        					goBack();
        				}
        			});
        		}
        		else {
        			alertbox.setMessage(msg.getData().getString("exception"));
        			alertbox.setNeutralButton(R.string.close_alertbox, new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface arg0, int arg1) {
        				}
        			});
        		}
                alertbox.show();
        	}
        }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_comments);

		txtEmpty = (TextView)this.getListView().getEmptyView();
		txtEmpty.setText("");

		adView = new AdView(this, AdSize.BANNER, CommonSettings.GoogleAddsAppID);
		LinearLayout layout = (LinearLayout)findViewById(R.id.wordcomments_layout);
		layout.addView(adView);
		AdRequest request = new AdRequest();
		request.addTestDevice(AdRequest.TEST_EMULATOR);
		adView.loadAd(request);

		wordID = 0;
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
		if (extra != null)
			wordID = extra.getInt("wordid"); 

		if (wordID > 0) {
			wordEnt = dbHelper.GetWord(wordID);
			setTitle(wordEnt.Word);
		}

		if (CommonSettings.ShouldSyncComments(WordComments.this, wordID)) {
			loadingDialog = ProgressDialog.show(this, "", getString(R.string.loading), true);
			Thread thread = new Thread(this);
			thread.run();
		}
		else
			LoadWordComments();
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
		CommonSettings.AddToCache(WordComments.this, 0, 0, wordID);
        handler.sendEmptyMessage(0);
	}

	@Override
	public void onSyncProgress(int progress) {
	}

	@Override
	public void onSyncError(Exception ex) {
		Log.d("WordsComments", ex.getMessage());
		Message msg = handler.obtainMessage(); 
        Bundle b = new Bundle();
        b.putString("exception", ex.getMessage()); 
        msg.setData(b);
        handler.handleMessage(msg);
	}

	public void run() {
		syncManager.GetWordComments(wordID);
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

		listItems = dbHelper.selectWordComments(wordID);
	}
	
	private void LoadWordComments() {
		reloadItems();

		if (listItems.size() == 0)
			txtEmpty.setText(getString(R.string.no_data));
		else {
    		setListAdapter(new WordCommentsAdapter(WordComments.this, R.layout.list_item, listItems));

    		ListView lv = getListView();
    		lv.setTextFilterEnabled(true);
    		registerForContextMenu(lv);
		}
	}
    
    private void goBack() {
    	Timer timer = new Timer();
        timer.schedule( new TimerTask(){
           public void run() { 
        	   WordComments.this.onBackPressed();
            }
         }, 2000);
    }

}
