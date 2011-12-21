package net.supudo.apps.aNeolog.Synchronization;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.supudo.apps.aNeolog.CommonSettings;
import net.supudo.apps.aNeolog.Database.DatabaseSchema;
import net.supudo.apps.aNeolog.Database.DatabaseModel;
import net.supudo.apps.aNeolog.URLHelpers.URLHelper;
import net.supudo.apps.aNeolog.URLHelpers.URLHelperCallbacks;

public class SyncManager implements URLHelperCallbacks {

	private SyncManagerCallbacks mDelegate;
	private Context mCtx;
	private URLHelper urlHelper;
	private DatabaseModel dbModel;
	private SQLiteDatabase db = null;
	private boolean finished = false;
	private String state = ServicesNames.NESTS_SERVICE;

	public static interface SyncManagerCallbacks {
		public void syncFinished();
		public void onSyncProgress(int progress);
		public void onSyncError(Exception ex);
	}

	public static final class ServicesNames {
		private ServicesNames () {}
		public static final String NESTS_SERVICE = "?action=GetNests";
		public static final String TEXTCONTENT_SERVICE = "?action=GetContent";
		public static final String WORDSFORNEST_SERVICE = "?action=FetchWordsForNest";
		public static final String WORDSFORLETTER_SERVICE = "?action=FetchWordsForLetter";
		public static final String WORDCOMMENTS_SERVICE = "?action=FetchWordComments";
		public static final String SENDWORD_SERVICE = "?action=SendWord";
		public static final String SENDCOMMENT_SERVICE = "?action=SendComment";
	}

	public static final class WebServiceID {
		private WebServiceID () {}
		public static final Integer NESTS_SERVICE = 0;
		public static final Integer TEXTCONTENT_SERVICE = 1;
		public static final Integer WORDSFORNEST_SERVICE = 2;
		public static final Integer WORDSFORLETTER_SERVICE = 3;
		public static final Integer WORDCOMMENTS_SERVICE = 4;
		public static final Integer SENDWORD_SERVICE = 5;
		public static final Integer SENDCOMMENT_SERVICE = 6;
	}

	public String getState() {
		return state;
	}

	public boolean isFinished() {
		return finished;
	}

	public SyncManager(Context context, SyncManagerCallbacks delegate) {
		mDelegate = delegate;
		mCtx = context;
		urlHelper = new URLHelper(this);
		dbModel = new DatabaseModel(mCtx);
	}

	public void clearDatabase() {
		SQLiteDatabase db_tmp = dbModel.getWritableDatabase();
		db_tmp.execSQL("PRAGMA foreign_keys = OFF;");
		db_tmp.execSQL("DELETE FROM " + DatabaseSchema.SETTINGS_TABLE_NAME);
		db_tmp.execSQL("DELETE FROM " + DatabaseSchema.TEXTCONTENT_TABLE_NAME);
		db_tmp.execSQL("DELETE FROM " + DatabaseSchema.ACCOUNTDATA_TABLE_NAME);
		db_tmp.execSQL("DELETE FROM " + DatabaseSchema.NEST_TABLE_NAME);
		db_tmp.execSQL("DELETE FROM " + DatabaseSchema.WORDS_TABLE_NAME);
		db_tmp.execSQL("DELETE FROM " + DatabaseSchema.WORDSCOMMENTS_TABLE_NAME);
		db_tmp.close();
		Log.d("Sync", "Database cleared");
	}

	public void cancel() {
		if (urlHelper != null) {
			urlHelper.cancel();
			urlHelper = null;
		}
		if (db != null) {
			if (db.isOpen())
				db.close();
			db = null;
		}
		mCtx = null;
	}
	
	/* ------------------------------------------
	 * 
	 * Public services
	 * 
	 * ------------------------------------------
	 */
	public void synchronize() {
		synchronize(ServicesNames.NESTS_SERVICE, 0, "");
	}

	public void synchronize(String serviceName, Integer nestID, String firstLetter) {
		try {
			db = dbModel.getWritableDatabase();
			db.execSQL("PRAGMA foreign_keys = ON;");
			Log.d("Sync", "Synchronizing ... " + serviceName);
			if (serviceName.equals(ServicesNames.NESTS_SERVICE))
				this.loadNestsUrl();
			else if (serviceName.equals(ServicesNames.TEXTCONTENT_SERVICE))
				this.loadTextContentUrl();
			else if (serviceName.equals(ServicesNames.WORDSFORNEST_SERVICE))
				this.loadWordForNestUrl(nestID);
			else if (serviceName.equals(ServicesNames.WORDSFORLETTER_SERVICE))
				this.loadWordForLetterUrl(firstLetter);
		}
		catch (NotFoundException e) {
			e.printStackTrace();
			Log.d("Sync", "Synchronizing error - " + e.getMessage());
			mDelegate.onSyncError(e);
		}
		catch (MalformedURLException e) {
			Log.d("Sync", "Synchronizing error - " + e.getMessage());
			e.printStackTrace();
			mDelegate.onSyncError(e);
		}
		catch (UnsupportedEncodingException e) {
			Log.d("Sync", "Synchronizing error - " + e.getMessage());
			e.printStackTrace();
			mDelegate.onSyncError(e);
		}		
	}

	public void GetWordsForLetter(String letter) {
		db = dbModel.getWritableDatabase();
		db.execSQL("PRAGMA foreign_keys = ON;");
		Log.d("Sync", "GetWordsForLetter ... ");
		synchronize(ServicesNames.WORDSFORLETTER_SERVICE, 0, letter);
	}

	public void GetWordsForNest(Integer nestID) {
		db = dbModel.getWritableDatabase();
		db.execSQL("PRAGMA foreign_keys = ON;");
		Log.d("Sync", "GetWordsForNest ... ");
		synchronize(ServicesNames.WORDSFORNEST_SERVICE, nestID, "");
	}

	/* ------------------------------------------
	 * 
	 * Workers
	 * 
	 * ------------------------------------------
	 */
	@Override
	public void updateModelWithJSONObject(JSONObject object, Integer serviceId) {
		try {
			if (serviceId == WebServiceID.NESTS_SERVICE) {
				Log.d("Sync", "updateModelWithJSONObject ... nests");
				handleNests(object);
				this.loadTextContentUrl();
			}
			else if (serviceId == WebServiceID.TEXTCONTENT_SERVICE) {
				Log.d("Sync", "updateModelWithJSONObject ... text content");
				handleTextContent(object);
			}
			else if (serviceId == WebServiceID.WORDSFORNEST_SERVICE) {
				Log.d("Sync", "updateModelWithJSONObject ... words for nest");
				handleWords(object, "FetchWordsForNest");
			}
			else if (serviceId == WebServiceID.WORDSFORLETTER_SERVICE) {
				Log.d("Sync", "updateModelWithJSONObject ... words for letter");
				handleWords(object, "FetchWordsForLetter");
			}
		}
		catch (NotFoundException e) {
			e.printStackTrace();
			mDelegate.onSyncError(e);
			this.connectionFailed(serviceId);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			mDelegate.onSyncError(e);
			this.connectionFailed(serviceId);
		}
	}

	@Override
	public void connectionFailed(Integer serviceId) {
		mDelegate.syncFinished();
		if (db != null) {
			db.close();
			db = null;
		}
	}

	private void finishSync() {
		db.close();
		finished = true;
		mDelegate.syncFinished();
	}
	
	/* ------------------------------------------
	 * 
	 * Synchronization URLs
	 * 
	 * ------------------------------------------
	 */
	private void loadNestsUrl() throws MalformedURLException, NotFoundException {
		this.state = ServicesNames.NESTS_SERVICE;
		Log.d("Sync", CommonSettings.BASE_SERVICES_URL + ServicesNames.NESTS_SERVICE);
		urlHelper.loadURLString(CommonSettings.BASE_SERVICES_URL + ServicesNames.NESTS_SERVICE, WebServiceID.NESTS_SERVICE);
	}

	private void loadTextContentUrl() throws MalformedURLException, NotFoundException {
		this.state = ServicesNames.TEXTCONTENT_SERVICE;
		Log.d("Sync", CommonSettings.BASE_SERVICES_URL + ServicesNames.TEXTCONTENT_SERVICE);
		urlHelper.loadURLString(CommonSettings.BASE_SERVICES_URL + ServicesNames.TEXTCONTENT_SERVICE, WebServiceID.TEXTCONTENT_SERVICE);
	}

	private void loadWordForNestUrl(Integer nestID) throws MalformedURLException, NotFoundException {
		this.state = ServicesNames.WORDSFORNEST_SERVICE;
		Log.d("Sync", CommonSettings.BASE_SERVICES_URL + ServicesNames.WORDSFORNEST_SERVICE + "&nid=" + nestID);
		urlHelper.loadURLString(CommonSettings.BASE_SERVICES_URL + ServicesNames.WORDSFORNEST_SERVICE + "&nid=" + nestID, WebServiceID.WORDSFORNEST_SERVICE);
	}

	private void loadWordForLetterUrl(String letter) throws MalformedURLException, NotFoundException, UnsupportedEncodingException {
		this.state = ServicesNames.WORDSFORLETTER_SERVICE;
		Log.d("Sync", CommonSettings.BASE_SERVICES_URL + ServicesNames.WORDSFORLETTER_SERVICE + "&letter=" + URLEncoder.encode(letter, HTTP.UTF_8));
		urlHelper.loadURLString(CommonSettings.BASE_SERVICES_URL + ServicesNames.WORDSFORLETTER_SERVICE + "&letter=" + URLEncoder.encode(letter, HTTP.UTF_8), WebServiceID.WORDSFORLETTER_SERVICE);
	}

	/* ------------------------------------------
	 * 
	 * Synchronization Data
	 * 
	 * ------------------------------------------
	 */
	private void handleNests(JSONObject object) {
		try {
			JSONArray nests = object.getJSONArray("GetNests");
			for (int i=0; i<nests.length(); ++i) {
				JSONObject ent = nests.getJSONObject(i);

				ContentValues cv = new ContentValues();
				cv.put(DatabaseSchema.NestColumns._ID, Integer.parseInt(ent.getString("nid")));
				cv.put(DatabaseSchema.NestColumns.ORDERPOS, Integer.parseInt(ent.getString("orderpos")));
				cv.put(DatabaseSchema.NestColumns.TITLE, ent.getString("nest"));

				String q = "SELECT * FROM " + DatabaseSchema.NEST_TABLE_NAME + " WHERE _id = " + ent.getString("nid");
				Cursor c = db.rawQuery(q, null);
				if (c.getCount() == 0)
					db.insert(DatabaseSchema.NEST_TABLE_NAME, null, cv);
				else
					db.update(DatabaseSchema.NEST_TABLE_NAME, cv, DatabaseSchema.NestColumns._ID + " = ?", new String[]{ent.getString("nid")});
				c.close();
			}
			Log.d("Sync", "GetNests ... done");
		}
		catch (Exception e) {
			e.printStackTrace();
			this.connectionFailed(WebServiceID.NESTS_SERVICE);
			mDelegate.onSyncError(e);
		}
	}

	private void handleTextContent(JSONObject object) {
		try {
			JSONArray tc = object.getJSONArray("GetContent");
			for (int i=0; i<tc.length(); ++i) {
				JSONObject ent = tc.getJSONObject(i);

				ContentValues cv = new ContentValues();
				cv.put(DatabaseSchema.TextContentColumns._ID, Integer.parseInt(ent.getString("id")));
				cv.put(DatabaseSchema.TextContentColumns.TITLE, ent.getString("title"));
				cv.put(DatabaseSchema.TextContentColumns.CONTENT, ent.getString("content"));

				Cursor c = db.rawQuery("SELECT * FROM " + DatabaseSchema.TEXTCONTENT_TABLE_NAME + " WHERE " + DatabaseSchema.TextContentColumns._ID + " = ?;", new String[] {ent.getString("id")});
				if (c.getCount() == 0)
					db.insert(DatabaseSchema.TEXTCONTENT_TABLE_NAME, null, cv);
				else
					db.update(DatabaseSchema.TEXTCONTENT_TABLE_NAME, cv, DatabaseSchema.TextContentColumns._ID + " = ?", new String[]{ent.getString("id")});
				c.close();
			}
			Log.d("Sync", "GetContent ... done");
			finishSync();
		}
		catch (Exception e) {
			e.printStackTrace();
			this.connectionFailed(WebServiceID.TEXTCONTENT_SERVICE);
			mDelegate.onSyncError(e);
		}
	}

	private void handleWords(JSONObject object, String action) {
		try {
			JSONArray tc = object.getJSONArray(action);
			for (int i=0; i<tc.length(); ++i) {
				JSONObject ent = tc.getJSONObject(i);

				ContentValues cv = new ContentValues();
				cv.put(DatabaseSchema.WordColumns._ID, Integer.parseInt(ent.getString("wid")));
				cv.put(DatabaseSchema.WordColumns.WORD, ent.getString("word"));
				cv.put(DatabaseSchema.WordColumns.NEST_ID, Integer.parseInt(ent.getString("nestid")));
				cv.put(DatabaseSchema.WordColumns.COMMENT_COUNT, Integer.parseInt(ent.getString("commcount")));
				cv.put(DatabaseSchema.WordColumns.ADDEDBY, ent.getString("addedby"));
				cv.put(DatabaseSchema.WordColumns.ADDEDBY_EMAIL, ent.getString("addedby_email"));
				cv.put(DatabaseSchema.WordColumns.ADDEDBY_URL, ent.getString("addedby_url"));
				cv.put(DatabaseSchema.WordColumns.DESCRIPTION, ent.getString("desc"));
				cv.put(DatabaseSchema.WordColumns.EXAMPLE, ent.getString("ex"));
				cv.put(DatabaseSchema.WordColumns.ETHIMOLOGY, ent.getString("eth"));
				cv.put(DatabaseSchema.WordColumns.ADDEDAT_DATE, ent.getString("createddate"));

				Cursor c = db.rawQuery("SELECT * FROM " + DatabaseSchema.WORDS_TABLE_NAME + " WHERE " + DatabaseSchema.WordColumns._ID + " = ?;", new String[] {ent.getString("wid")});
				if (c.getCount() == 0)
					db.insert(DatabaseSchema.WORDS_TABLE_NAME, null, cv);
				else
					db.update(DatabaseSchema.WORDS_TABLE_NAME, cv, DatabaseSchema.WordColumns._ID + " = ?", new String[]{ent.getString("wid")});
				c.close();
			}
			Log.d("Sync", "FetchWordsForNest ... done");
			finishSync();
		}
		catch (Exception e) {
			e.printStackTrace();
			this.connectionFailed(WebServiceID.WORDSFORNEST_SERVICE);
			mDelegate.onSyncError(e);
		}
	}

}
