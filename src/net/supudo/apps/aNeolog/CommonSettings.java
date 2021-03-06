package net.supudo.apps.aNeolog;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public enum CommonSettings {
	INSTANCE;
	
	public static final String BASE_SERVICES_URL = "http://neolog.eenk.com/service_json.php";
	public static final String HOST_NAME = "neolog.eenk.com";
	public static final String PREFS_FILE_NAME = "NelogPreferences";
	public static String AppCallbackURI = "neologandroid";
	
	public static String AppVersion = "";
	public static boolean ShowBanners = false;
	public static String DefaultDateFormat = "yyyy-MM-dd HH:mm:ss";
	
	public static String GoogleAddsAppID = "a14ef08a8f6cc59";
	public static String FacebookAppID = "269046193111640";
	public static String TwitterConsumerKey = "KvIUb7SS9i4FJhHeLzo4IQ", TwitterConsumerSecret = "hq7L3zkKJTY8IJ9HyesJ0t4fd8cbkIHdAC3iyvoJB70";
	public static String TwitterCallbackURI = AppCallbackURI + "://twitter";
	
	public static final String IEXTRA_AUTH_URL = "auth_url";
	public static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
	public static final String IEXTRA_OAUTH_TOKEN = "oauth_token";
	public static String PREFERENCE_NAME = "twitter_oauth";
	public static final String PREF_KEY_SECRET = "oauth_token_secret";
	public static final String PREF_KEY_TOKEN = "oauth_token";
	public static final String PREF_KEY_CONNECTED = "connected";

	public static boolean stSearchOnline = true, stStorePrivateData = true, stInAppEmail = true;
	public static String[] Letters = {"А", "Б", "В", "Г", "Д", "Е", "Ж", "З", "И", "Й", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ю", "Я", "Други..."}; 
	public static Date lastSyncDate;
	public static HashMap<String, String> cacheObject = new HashMap<String, String>();
	
	public static int syncTimeintervalForWords = 3600, syncTimeintervalForComments = 72000;

	public static boolean ShouldSyncComments(Context ctx, int wordID) {
		boolean shouldSync = true;
		if (cacheObject.size() == 0)
			LoadCache(ctx);

		String syncDate = (String)cacheObject.get("word-" + String.valueOf(wordID));

		if (syncDate != null)
			syncDate = syncDate.replace("word-", "");

		if (syncDate != null && !syncDate.equals("")) {
			long syncDateMiliSeconds = Long.parseLong(syncDate);
			long diffInSeconds = (Calendar.getInstance().getTime().getTime() - syncDateMiliSeconds) / 1000;
			Log.d("CommonSettings", "Skipping sync for word comments " + wordID + " - last @ " + (diffInSeconds * 60));
			if (diffInSeconds < syncTimeintervalForComments)
				shouldSync = false;
		}

		return shouldSync;
	}

	public static boolean ShouldSyncWords(Context ctx, int letterPos, int nestID) {
		boolean shouldSync = true;
		if (cacheObject.size() == 0)
			LoadCache(ctx);

		String syncDate = "";
		if (nestID > 0)
			syncDate = (String)cacheObject.get("nest-" + String.valueOf(nestID));
		else
			syncDate = (String)cacheObject.get("letter-" + String.valueOf(letterPos));

		if (syncDate != null) {
			if (nestID > 0)
				syncDate = syncDate.replace("nest-", "");
			else
				syncDate = syncDate.replace("letter-", "");
		}

		if (syncDate != null && !syncDate.equals("")) {
			long syncDateMiliSeconds = Long.parseLong(syncDate);
			long diffInSeconds = (Calendar.getInstance().getTime().getTime() - syncDateMiliSeconds) / 1000;
			Log.d("CommonSettings", "Skipping sync for " + ((nestID > 0) ? nestID : letterPos) + " - last @ " + (diffInSeconds * 60));
			if (diffInSeconds < syncTimeintervalForWords)
				shouldSync = false;
		}

		return shouldSync;
	}
	
	public static void AddToCache(Context ctx, int letterPos, int nestID, int wordID) {
		String syncDate = String.valueOf((new Date()).getTime());
		if (nestID > 0)
			cacheObject.put("nest-" + String.valueOf(nestID), syncDate);
		else if (wordID > 0)
			cacheObject.put("word-" + String.valueOf(wordID), syncDate);
		else
			cacheObject.put("letter-" + String.valueOf(letterPos), syncDate);
		SaveCache(ctx);
	}

	public static void SaveCache(Context ctx) {
		SharedPreferences cachePrefs = ctx.getSharedPreferences(CommonSettings.PREFS_FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor cachePrefsEditor = cachePrefs.edit();
		for (String s : cacheObject.keySet())
			cachePrefsEditor.putString(s, cacheObject.get(s));
		cachePrefsEditor.commit();
	}

	@SuppressWarnings("unchecked")
	public static void LoadCache(Context ctx) {
		cacheObject.clear();

        SharedPreferences settings = ctx.getSharedPreferences(CommonSettings.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        HashMap<String, String> _temp = (HashMap<String, String>)settings.getAll();
        for (String s : _temp.keySet())
            cacheObject.put(s, _temp.get(s).toString());
	}
	
	public static void ClearCache(Context ctx) {
        Editor editor = ctx.getSharedPreferences(CommonSettings.PREFS_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
	}
}