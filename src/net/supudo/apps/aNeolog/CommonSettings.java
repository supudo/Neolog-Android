package net.supudo.apps.aNeolog;

import java.util.Date;

public enum CommonSettings {
	INSTANCE;
	
	public static final String BASE_SERVICES_URL = "http://www.neolog.bg/_mob_service_json.php";
	public static final String HOST_NAME = "www.neolog.bg";
	public static final String PREFS_FILE_NAME = "NelogPreferences";
	public static String AppCallbackURI = "neologandroid";
	
	public static String AppVersion = "";
	public static boolean ShowBanners = false;
	public static String DefaultDateFormat = "yyyy-MM-dd HH:mm:ss";
	
	public static String GoogleAddsAppID = "a14eb7e357a4b8c";
	public static String FacebookAppID = "162884250446512";
	public static String TwitterConsumerKey = "uSlctvG45nI6JawDFzGHw", TwitterConsumerSecret = "RGLWAZDxDBfqnw4i0LHTEEnfRPaYzAZQXZriSI1cI";
	public static String TwitterCallbackURI = AppCallbackURI + "://twitter";
	
	public static final String IEXTRA_AUTH_URL = "auth_url";
	public static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
	public static final String IEXTRA_OAUTH_TOKEN = "oauth_token";
	public static String PREFERENCE_NAME = "twitter_oauth";
	public static final String PREF_KEY_SECRET = "oauth_token_secret";
	public static final String PREF_KEY_TOKEN = "oauth_token";
	public static final String PREF_KEY_CONNECTED = "connected";
	
	public static boolean reloadNewestOffers = true, reloadSearchJobs = true;
	public static boolean reloadSearchPeople = true, reloadSearch = true;

	public static boolean stSearchOnline = true, stStorePrivateData = true, stSendGeo = true;
	public static boolean stInitSync = true, stInAppEmail = true, stShowCategories = true;
	
	public static Date lastSyncDate;
}