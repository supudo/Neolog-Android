package net.supudo.apps.aNeolog;

import java.util.Date;

public enum CommonSettings {
	INSTANCE;
	
	public static final String BASE_SERVICES_URL = "http://www.neolog.bg/service_json";
	public static final String HOST_NAME = "www.neolog.bg";
	public static final String PREFS_FILE_NAME = "NelogPreferences";
	public static String AppCallbackURI = "neologandroid";
	
	public static String AppVersion = "";
	public static boolean ShowBanners = false;
	public static String DefaultDateFormat = "yyyy-MM-dd HH:mm:ss";
	
	public static String GoogleAddsAppID = "a14ef08a8f6cc59";
	public static String FacebookAppID = "269046193111640";
	public static String TwitterConsumerKey = "bpPhvYVczLnd2ZntLNbcQ", TwitterConsumerSecret = "HbAvScLfdkAR9ZhW9j2LWQmWNk60J4lYHrxLOeXgQ";
	public static String TwitterCallbackURI = AppCallbackURI + "://twitter";
	
	public static final String IEXTRA_AUTH_URL = "auth_url";
	public static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
	public static final String IEXTRA_OAUTH_TOKEN = "oauth_token";
	public static String PREFERENCE_NAME = "twitter_oauth";
	public static final String PREF_KEY_SECRET = "oauth_token_secret";
	public static final String PREF_KEY_TOKEN = "oauth_token";
	public static final String PREF_KEY_CONNECTED = "connected";

	public static boolean stSearchOnline = true, stStorePrivateData = true, stInAppEmail = true;
	
	public static String[] Letters = {"А", "Б", "В", "Г", "Д", "Е", "Ж", "З", "И", "Й", "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ю", "Я"}; 
	
	public static Date lastSyncDate;
}