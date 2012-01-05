package net.supudo.apps.aNeolog.Words;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import net.supudo.apps.aNeolog.MainActivity;
import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.Database.DataHelper;
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
import android.widget.Toast;

public class SendComment extends MainActivity implements Runnable, SyncManagerCallbacks {

	private Integer wordID;
	private DataHelper dbHelper;
	private SyncManager syncManager;
	private ProgressDialog loadingDialog;

	private EditText txtName, txtComment;
	private Button btnPost;

	private String valName, valComment;
	
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	loadingDialog.dismiss();
        	
        	if (msg.getData().getString("exception").equals("true"))
        		goBack();
        	else {
        		try {
	    			String errorLabel = msg.getData().getString("exception");
	    			String errorMessage = (String) SendComment.this.getResources().getText(SendComment.this.getResources().getIdentifier(errorLabel, "string", "net.supudo.apps.aNeolog"));
	        		AlertDialog.Builder alertbox = new AlertDialog.Builder(SendComment.this);
	        		alertbox.setMessage(errorMessage);
	        		alertbox.setNeutralButton(R.string.close_alertbox, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface arg0, int arg1) {
	                    }
	                });
	                alertbox.show();
        		}
        		catch (Exception e) {
	        		AlertDialog.Builder alertbox = new AlertDialog.Builder(SendComment.this);
	        		alertbox.setMessage(e.getMessage());
	        		alertbox.setNeutralButton(R.string.close_alertbox, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface arg0, int arg1) {
	                    }
	                });
	                alertbox.show();
        		}
        	}
        }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_comment);

		txtName = (EditText)findViewById(R.id.txt_name);
		txtComment = (EditText)findViewById(R.id.txt_comment);

		btnPost = (Button)findViewById(R.id.btn_post);
		btnPost.setOnClickListener(btnPostListener);
		
		if (dbHelper == null)
			dbHelper = new DataHelper(this);

		if (syncManager == null)
			syncManager = new SyncManager(this, this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		Intent starting_intent = getIntent();
		Bundle extra = starting_intent.getExtras();
		if (extra != null)
			wordID = extra.getInt("wordid");
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

	@Override
	public void syncFinished() {
		handler.handleMessage(handler.obtainMessage());
	}

	@Override
	public void onSyncProgress(int progress) {
	}

	@Override
	public void onSyncError(Exception ex) {
		try {
			JSONObject res = (new JSONObject(ex.getMessage())).getJSONObject("SendComment");
			Log.d("SendComment", res.toString());
			Message msg = handler.obtainMessage(); 
	        Bundle b = new Bundle();
	        b.putString("exception", res.get("result").toString()); 
	        msg.setData(b);
	        handler.handleMessage(msg);
		}
		catch (JSONException e) {
			e.printStackTrace();
			handler.handleMessage(handler.obtainMessage());
		}
	}
	
	public void run() {
		syncManager.SendComment(wordID, valName, valComment);
	}
	
	private OnClickListener btnPostListener = new OnClickListener() {
		public void onClick(View v) {
			valName = txtName.getText().toString().trim();
			valComment = txtComment.getText().toString().trim();
			
			boolean validationError = true;
			if (valName.equals(""))
				txtName.setError(getString(R.string.post_error));
			else if (valComment.equals(""))
				txtComment.setError(getString(R.string.post_error));
			else
				validationError = false;

			if (validationError)
				Toast.makeText(getApplicationContext(), R.string.post_error, Toast.LENGTH_SHORT).show();
			else {
				loadingDialog = ProgressDialog.show(SendComment.this, "", getString(R.string.loading), true);
		     	Thread thread = new Thread(SendComment.this);
		        thread.run();
			}
        }
    };
    
    private void goBack() {
    	Toast.makeText(getApplicationContext(), R.string.post_thanks, Toast.LENGTH_SHORT).show();
    	Timer timer = new Timer();
        timer.schedule( new TimerTask(){
           public void run() { 
       			SendComment.this.onBackPressed();
            }
         }, 2000);
    }

}
