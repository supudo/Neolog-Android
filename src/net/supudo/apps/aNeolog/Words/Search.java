package net.supudo.apps.aNeolog.Words;

import net.supudo.apps.aNeolog.CommonSettings;
import net.supudo.apps.aNeolog.MainActivity;
import net.supudo.apps.aNeolog.R;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Search extends MainActivity implements Runnable, SyncManagerCallbacks {

	private EditText txtSearch;
	private Button btnSearch;
	private SyncManager syncManager;
	private String searchQuery;
	private ProgressDialog loadingDialog;
	
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	loadingDialog.dismiss();
        	
        	if (msg.getData().isEmpty())
        		OpenResults();
        	else {
        		AlertDialog.Builder alertbox = new AlertDialog.Builder(Search.this);
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
		setContentView(R.layout.search);
		setTitle(R.string.search);
		txtSearch = (EditText)findViewById(R.id.search_box);

		btnSearch = (Button)findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(btnSearchListener);

		if (syncManager == null)
			syncManager = new SyncManager(this, this);
	}
    
    @Override
    public void onStart() {
    	super.onStart();
    	
    	if (loadingDialog != null && loadingDialog.isShowing())
    		loadingDialog.dismiss();
    }
	
	private OnClickListener btnSearchListener = new OnClickListener() {
		public void onClick(View v) {
			searchQuery = txtSearch.getText().toString();
        	
        	if (CommonSettings.stSearchOnline) {
    	     	loadingDialog = ProgressDialog.show(Search.this, "", getString(R.string.loading), true);
    	    	Thread thread = new Thread(Search.this);
    	        thread.run();
        	}
        	else
        		OpenResults();
        }
    };
	
	public void run() {
		if (syncManager == null)
			syncManager = new SyncManager(this, this);
		syncManager.SearchWords(searchQuery);
	}

	@Override
	public void syncFinished() {
		OpenResults();
	}

	@Override
	public void onSyncProgress(int progress) {
	}

	@Override
	public void onSyncError(Exception ex) {
		Log.d("Search", ex.getMessage());
		Message msg = handler.obtainMessage(); 
        Bundle b = new Bundle();
        b.putString("exception", ex.getMessage()); 
        msg.setData(b); 
        handler.handleMessage(msg);
	}

	@Override
	protected void onPause() {
		if (syncManager != null) {
			syncManager.cancel();
			syncManager = null;
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (syncManager != null) {
			syncManager.cancel();
			syncManager = null;
		}
		super.onDestroy();
	}
	
	private void OpenResults() {
    	Intent myIntent = new Intent().setClass(Search.this, SearchResults.class);
    	myIntent.putExtra("searchQuery", searchQuery);
    	startActivityForResult(myIntent, 0);
	}

}