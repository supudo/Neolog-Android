package net.supudo.apps.aNeolog.Words;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import net.supudo.apps.aNeolog.CommonSettings;
import net.supudo.apps.aNeolog.MainActivity;
import net.supudo.apps.aNeolog.R;
import net.supudo.apps.aNeolog.Database.DataHelper;
import net.supudo.apps.aNeolog.Database.Models.WordModel;
import net.supudo.apps.aNeolog.SocNet.Facebook.BaseDialogListener;
import net.supudo.apps.aNeolog.SocNet.Facebook.BaseRequestListener;
import net.supudo.apps.aNeolog.SocNet.Facebook.SessionEvents;
import net.supudo.apps.aNeolog.SocNet.Facebook.SessionEvents.AuthListener;
import net.supudo.apps.aNeolog.SocNet.Facebook.SessionStore;
import net.supudo.apps.aNeolog.SocNet.Twitter.TwitterApp;

public class WordDetails extends MainActivity {

	private Integer wordID;
	private DataHelper dbHelper;
	private WordModel wordEnt;

    private Button fbPostButton, twitterPostButton, viewCommentButton, sendCommentButton;
	private TextView txtWord, txtDescription, txtExamples, txtEthimology, txtAuthorDate, txtAuthorURLEmail;
	private TextView txtExamplesLabel, txtEthimologyLabel;

    private Facebook engineFacebook;
    private AsyncFacebookRunner fbAsyncRunner;
    
	private Twitter engineTwitter;
	private RequestToken twitterRequestToken = null;
	private Context twitterContext;
    
    private enum SocNetOp {
    	SocNetOpTwitter,
    	SocNetOpFacebook
    }
    private SocNetOp selectedOp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_details);
		
		if (dbHelper == null)
			dbHelper = new DataHelper(this);

		fbPostButton = (Button)findViewById(R.id.facebook_post);
		twitterPostButton = (Button)findViewById(R.id.twitter_post);
		viewCommentButton = (Button)findViewById(R.id.btn_comments);
		sendCommentButton = (Button)findViewById(R.id.btn_send_comment);

		txtWord = (TextView)findViewById(R.id.word);
		txtDescription = (TextView)findViewById(R.id.description);
		txtExamples = (TextView)findViewById(R.id.examples);
		txtEthimology = (TextView)findViewById(R.id.ethimology);
		txtAuthorDate = (TextView)findViewById(R.id.authorDate);
		txtAuthorURLEmail = (TextView)findViewById(R.id.authorURLEmail);

		txtExamplesLabel = (TextView)findViewById(R.id.examples_title);
		txtEthimologyLabel = (TextView)findViewById(R.id.ethimology_title);

		engineFacebook = new Facebook(CommonSettings.FacebookAppID);
		fbAsyncRunner = new AsyncFacebookRunner(engineFacebook);
        SessionStore.restore(engineFacebook, this);
        SessionEvents.addAuthListener(new FBAuthListener());

        twitterPostButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	PostToTwitter();
            }
        });

        fbPostButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	PostOnFacebook();
            }
        });

        viewCommentButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	ViewComments();
            }
        });

        sendCommentButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	SendComment();
            }
        });
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		Intent starting_intent = getIntent();
		Bundle extra = starting_intent.getExtras();
		if (extra != null) {
			wordID = extra.getInt("wordid");
			wordEnt = dbHelper.GetWord(wordID);
			setTitle(wordEnt.Word);
			txtWord.setText(wordEnt.Word);
			txtDescription.setText(wordEnt.Description);
			if (!wordEnt.Example.equals(""))
				txtExamples.setText(wordEnt.Example);
			else {
				txtExamples.setText("");
				txtExamplesLabel.setHeight(0);
				txtExamples.setHeight(0);
			}
			if (!wordEnt.Ethimology.equals(""))
				txtEthimology.setText(wordEnt.Ethimology);
			else {
				txtEthimology.setText("");
				txtEthimologyLabel.setHeight(0);
				txtEthimology.setHeight(0);
			}

			String pDate = "";
			try {
				DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				Date date = (Date)formatter.parse(wordEnt.AddedAt_Date);
				int month = date.getMonth() + 1;
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				pDate += cal.get(Calendar.DAY_OF_MONTH);

				String monthLabel = "monthsShort_" + month;
				pDate += " " + (String) this.getResources().getText(this.getResources().getIdentifier(monthLabel, "string", "net.supudo.apps.aNeolog"));
				pDate += " " + cal.get(Calendar.YEAR);
			}
			catch (ParseException e) {
				Log.d("OfferDetails", "Date parser failed " + wordEnt.AddedAt_Date + " (" + wordEnt.AddedAt_Datestamp + ")");
			}

			txtAuthorDate.setText(wordEnt.AddedBy + " @ " + pDate);

			String authorDetails = "";
			if (!wordEnt.AddedBy_Email.equals(""))
				authorDetails += wordEnt.AddedBy_Email;
			if (!wordEnt.AddedBy_URL.equals(""))
				authorDetails += ((authorDetails.equals("")) ? "" : " // ") + wordEnt.AddedBy_URL;
			txtAuthorURLEmail.setText(authorDetails);
		}
		else
			goBack();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (this.selectedOp == SocNetOp.SocNetOpFacebook)
    		engineFacebook.authorizeCallback(requestCode, resultCode, data);
    	else if (this.selectedOp == SocNetOp.SocNetOpTwitter) {
    		if (resultCode == RESULT_OK) {
    			AccessToken accessToken = null;
    			try {
    				accessToken = engineTwitter.getOAuthAccessToken(twitterRequestToken, data.getExtras().getString(CommonSettings.IEXTRA_OAUTH_VERIFIER));

    				SharedPreferences pref = getSharedPreferences(CommonSettings.PREFERENCE_NAME, MODE_PRIVATE);
    				SharedPreferences.Editor editor = pref.edit();
    				editor.putString(CommonSettings.PREF_KEY_TOKEN, accessToken.getToken());
    				editor.putString(CommonSettings.PREF_KEY_SECRET, accessToken.getTokenSecret());
    				editor.putBoolean(CommonSettings.PREF_KEY_CONNECTED, true);
    				editor.commit();

    				String tweet = "Neolog.bg - " + wordEnt.Word;
    				tweet += " http://www.neolog.bg/word/" + wordEnt.WordID;
    				tweet += " #neolog";

    				engineTwitter.setOAuthAccessToken(accessToken);
    				engineTwitter.updateStatus(tweet);
    				Toast.makeText(WordDetails.this, R.string.twitter_publishok, Toast.LENGTH_LONG).show();
    			}
    			catch (TwitterException e) {
    				if (e.getMessage().toString().contains("duplicate"))
    					Toast.makeText(WordDetails.this, R.string.twitter_err_duplicate, Toast.LENGTH_LONG).show();
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    private void goBack() {
    	Timer timer = new Timer();
        timer.schedule( new TimerTask(){
           public void run() { 
        	   WordDetails.this.onBackPressed();
            }
         }, 2000);
    }
    
    private void ViewComments() {
		Toast.makeText(getApplicationContext(), wordEnt.Word, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent().setClass(WordDetails.this, WordComments.class);
		intent.putExtra("wordid", wordID);
		startActivity(intent);
    }
	
	private void SendComment() {
		Intent intent = new Intent().setClass(WordDetails.this, SendComment.class);
		intent.putExtra("wordid", wordID);
		startActivity(intent);
	}

	/* ------------------------------------------
	 * 
	 * Twitter
	 * 
	 * ------------------------------------------
	 */
	private void PostToTwitter() {
    	this.selectedOp = SocNetOp.SocNetOpTwitter;
		PostToTwitterApp();
	}

	private void PostToTwitterApp() {
		try {
			twitterContext = this;
			new TwitterConnectTask().execute();
		}
		catch (Exception e) {
			Log.e("OfferDetails", e.getMessage());
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void connectTwitter() {
		ConfigurationBuilder confbuilder = new ConfigurationBuilder();
		Configuration conf = confbuilder.setOAuthConsumerKey(CommonSettings.TwitterConsumerKey).setOAuthConsumerSecret(CommonSettings.TwitterConsumerSecret).build();

		engineTwitter = new TwitterFactory(conf).getInstance();
		engineTwitter.setOAuthAccessToken(null);

		try {
			twitterRequestToken = engineTwitter.getOAuthRequestToken(CommonSettings.TwitterCallbackURI);
			Intent intent = new Intent(this, TwitterApp.class);
			intent.putExtra(CommonSettings.IEXTRA_AUTH_URL, twitterRequestToken.getAuthorizationURL());
			this.startActivityForResult(intent, 0);
		}
		catch (TwitterException e) {
			Toast.makeText(WordDetails.this, "Errror : " + e.getStatusCode(), Toast.LENGTH_LONG).show();
		}
	}

	private class TwitterConnectTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(twitterContext);
			progressDialog.setMessage(twitterContext.getString(R.string.twitter_initializing));
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(false);
				}
			});
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... v) {
			connectTwitter();
			return (Void)null;
		}

		@Override
		protected void onProgressUpdate(Void... v) {
		}

		@Override
		protected void onPostExecute(Void v) {
			progressDialog.dismiss();
		}
	}
    
	/* ------------------------------------------
	 * 
	 * Facebook
	 * 
	 * ------------------------------------------
	 */
	private void PostOnFacebook() {
    	this.selectedOp = SocNetOp.SocNetOpFacebook;
         try {
        	 String response = engineFacebook.request("me");
        	 
        	 JSONObject attachment = new JSONObject();
        	 attachment.put("name", wordEnt.Word);
        	 attachment.put("href", "http://www.neolog.bg/word/" + wordEnt.WordID);
        	 attachment.put("caption", wordEnt.Example);
        	 attachment.put("description", wordEnt.Description);

        	 Bundle params = new Bundle();
        	 params.putString("attachment", attachment.toString());

        	 JSONObject actionLink = new JSONObject();
        	 actionLink.put("text", "Neolog.bg");
        	 actionLink.put("href", "http://www.neolog.bg/");
        	 
        	 JSONArray jasonarray = new JSONArray().put(actionLink);
        	 params.putString("action_links", jasonarray.toString());
        	 
        	 engineFacebook.dialog(WordDetails.this, "stream.publish", params, new FBDialogListener());
        	 if (response == null || response.equals("") || response.equals("false"))
        		 Log.v("WordDetails", "Blank response");
        	 else
        		 Log.v("WordDetails", "got response: " + response);
         }
         catch(Exception e) {
             e.printStackTrace();
         }
    }
	
	public class FBAuthListener implements AuthListener {
        public void onAuthSucceed() {
        	SessionStore.save(engineFacebook, WordDetails.this);
        }
        public void onAuthFail(String error) {
        }
    }

    public class FBDialogListener extends BaseDialogListener {
        public void onComplete(Bundle values) {
            final String postId = values.getString("post_id");
            if (postId != null)
            	fbAsyncRunner.request(postId, new FBWallPostRequestListener());
        }
    }

    public class FBWallPostRequestListener extends BaseRequestListener {
        public void onComplete(final String response, final Object state) {
            try {
            	Log.d("WordDetails", "Facebook response - " + response);
                JSONObject json = Util.parseJson(response);
                String message = json.getString("message");
                Log.d("WordDetails", "Facebook post success - " + message);
            }
            catch (JSONException e) {
            	e.printStackTrace();
            	WordDetails.this.runOnUiThread(
        			new Runnable() {
        				public void run() {
        	            	Toast.makeText(WordDetails.this, R.string.facebook_publisherror, Toast.LENGTH_LONG).show();
        				}
        			}
            	);
            }
            catch (FacebookError e) {
            	e.printStackTrace();
            	WordDetails.this.runOnUiThread(
        			new Runnable() {
        				public void run() {
        					Toast.makeText(WordDetails.this, R.string.facebook_responseerror, Toast.LENGTH_LONG).show();
        				}
        			}
            	);
            }
            WordDetails.this.runOnUiThread(
    			new Runnable() {
    				public void run() {
    		        	Toast.makeText(WordDetails.this, R.string.facebook_publishok, Toast.LENGTH_LONG).show();
    				}
    			}
        	);
        }
    }

}
