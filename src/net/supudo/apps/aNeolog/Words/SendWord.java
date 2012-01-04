package net.supudo.apps.aNeolog.Words;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import net.supudo.apps.aNeolog.CommonSettings;
import net.supudo.apps.aNeolog.MainActivity;
import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.DataAdapters.SpinnerNestAdapter;
import net.supudo.apps.aNeolog.Database.DataHelper;
import net.supudo.apps.aNeolog.Database.Models.NestModel;
import net.supudo.apps.aNeolog.Synchronization.SyncManager;
import net.supudo.apps.aNeolog.Synchronization.SyncManager.SyncManagerCallbacks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SendWord extends MainActivity implements Runnable, SyncManagerCallbacks {

	private DataHelper dbHelper;
	private SyncManager syncManager;
	private ArrayList<NestModel> itemsNests;
	private ProgressDialog loadingDialog;

	private Spinner cmbNests;
	private EditText txtName, txtEmail, txtURL, txtWord, txtDescription, txtExamples, txtEthimology;
	private Button btnPost;

	private int pNestID;
	private String valName, valEmail, valURL, valWord, valDescription, valExamples, valEthimology;
	
	private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	loadingDialog.dismiss();
        	
        	if (msg.getData().getString("exception").equals("true"))
        		goBack();
        	else {
        		try {
	    			String errorLabel = msg.getData().getString("exception");
	    			String errorMessage = (String) SendWord.this.getResources().getText(SendWord.this.getResources().getIdentifier(errorLabel, "string", "net.supudo.apps.aNeolog"));
	        		AlertDialog.Builder alertbox = new AlertDialog.Builder(SendWord.this);
	        		alertbox.setMessage(errorMessage);
	        		alertbox.setNeutralButton(R.string.close_alertbox, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface arg0, int arg1) {
	                    }
	                });
	                alertbox.show();
        		}
        		catch (Exception e) {
	        		AlertDialog.Builder alertbox = new AlertDialog.Builder(SendWord.this);
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
		setContentView(R.layout.send_word);
		
		cmbNests = (Spinner)findViewById(R.id.nest);
		txtName = (EditText)findViewById(R.id.txt_name);
		txtEmail = (EditText)findViewById(R.id.txt_email);
		txtURL = (EditText)findViewById(R.id.txt_url);
		txtWord = (EditText)findViewById(R.id.txt_word);
		txtDescription = (EditText)findViewById(R.id.txt_description);
		txtExamples = (EditText)findViewById(R.id.txt_examples);
		txtEthimology = (EditText)findViewById(R.id.txt_ethimology);

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

		itemsNests = dbHelper.selectNests();

    	if (CommonSettings.stStorePrivateData)
    		txtEmail.setText((dbHelper.GetSetting("PrivateData_Email")).SValue);

		SpinnerNestAdapter _nestsAdapter = new SpinnerNestAdapter(SendWord.this, itemsNests);
		cmbNests.setAdapter(_nestsAdapter);
		cmbNests.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                NestModel nest = (NestModel)parent.getItemAtPosition(pos);
                pNestID = nest.NestID;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
			JSONObject res = (new JSONObject(ex.getMessage())).getJSONObject("SendWord");
			Log.d("SendWord", res.toString());
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
		syncManager.SendWord(pNestID, valName, valEmail, valURL, valWord, valDescription, valExamples, valEthimology);
	}
	
	private OnClickListener btnPostListener = new OnClickListener() {
		public void onClick(View v) {
			valName = txtName.getText().toString().trim();
			valEmail = txtEmail.getText().toString().trim();
			valURL = txtURL.getText().toString().trim();
			valWord = txtWord.getText().toString().trim();
			valDescription = txtDescription.getText().toString().trim();
			valExamples = txtExamples.getText().toString().trim();
			valEthimology = txtEthimology.getText().toString().trim();
			
			boolean validationError = true;
			if (valName.equals(""))
				txtName.setError(getString(R.string.post_error));
			else if (valWord.equals(""))
				txtWord.setError(getString(R.string.post_error));
			else if (valDescription.equals(""))
				txtDescription.setError(getString(R.string.post_error));
			else if (valExamples.equals(""))
				txtExamples.setError(getString(R.string.post_error));
			else
				validationError = false;
			
			if (pNestID == 0)
				validationError = true;

			if (validationError)
				Toast.makeText(getApplicationContext(), R.string.post_error, Toast.LENGTH_SHORT).show();
			else {
				loadingDialog = ProgressDialog.show(SendWord.this, "", getString(R.string.loading), true);
		     	Thread thread = new Thread(SendWord.this);
		        thread.run();
			}
        }
    };
    
    private void goBack() {
    	if (CommonSettings.stStorePrivateData)
    		dbHelper.SetSetting("PrivateData_Email", valEmail);
    	Toast.makeText(getApplicationContext(), R.string.post_thanks, Toast.LENGTH_SHORT).show();
    	Timer timer = new Timer();
        timer.schedule( new TimerTask(){
           public void run() { 
       			SendWord.this.onBackPressed();
            }
         }, 2000);
    }

}