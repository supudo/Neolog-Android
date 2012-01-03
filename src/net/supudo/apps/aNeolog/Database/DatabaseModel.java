package net.supudo.apps.aNeolog.Database;

import net.supudo.apps.aNeolog.Database.DatabaseSchema.AccountDataColumns;
import net.supudo.apps.aNeolog.Database.DatabaseSchema.NestColumns;
import net.supudo.apps.aNeolog.Database.DatabaseSchema.SettingsColumns;
import net.supudo.apps.aNeolog.Database.DatabaseSchema.TextContentColumns;
import net.supudo.apps.aNeolog.Database.DatabaseSchema.WordColumns;
import net.supudo.apps.aNeolog.Database.DatabaseSchema.WordCommentsColumns;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseModel extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Neolog";
	private static final int DATABASE_VERSION = 1;

	private static final String SETTINGS_TABLE_CREATE =
		"CREATE TABLE " + DatabaseSchema.SETTINGS_TABLE_NAME + " (" +
		SettingsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		SettingsColumns.EDITABLE_YN + " TINYINT," +
		SettingsColumns.SNAME + " VARCHAR," +
		SettingsColumns.SVALUE + " VARCHAR);";

	private static final String TEXTCONTENT_TABLE_CREATE =
		"CREATE TABLE " + DatabaseSchema.TEXTCONTENT_TABLE_NAME + " (" +
		TextContentColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		TextContentColumns.TITLE + " VARCHAR," +
		TextContentColumns.CONTENT + " VARCHAR);";

	private static final String ACCOUNTDATA_TABLE_CREATE =
		"CREATE TABLE " + DatabaseSchema.ACCOUNTDATA_TABLE_NAME + " (" +
		AccountDataColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		AccountDataColumns.EMAIL + " VARCHAR," +
		AccountDataColumns.NAME + " VARCHAR," +
		AccountDataColumns.URL + " VARCHAR," +
		AccountDataColumns.NESTID + " INTEGER);";

	private static final String NEST_TABLE_CREATE =
		"CREATE TABLE " + DatabaseSchema.NEST_TABLE_NAME + " (" +
		NestColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		NestColumns.ORDERPOS + " INTEGER, " +
		NestColumns.TITLE + " VARCHAR);";

	private static final String WORDSCOMMENTS_TABLE_CREATE =
		"CREATE TABLE " + DatabaseSchema.WORDSCOMMENTS_TABLE_NAME + " (" +
		WordCommentsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		WordCommentsColumns.WORD_ID + " INTEGER, " +
		WordCommentsColumns.AUTHOR + " VARCHAR, " +
		WordCommentsColumns.COMMENT + " VARCHAR, " +
		WordCommentsColumns.COMMENT_DATE + " VARCHAR," +
		WordCommentsColumns.COMMENT_DATESTAMP + " INTEGER);";

	private static final String WORDS_TABLE_CREATE =
		"CREATE VIRTUAL TABLE " + DatabaseSchema.WORDS_TABLE_NAME + " USING FTS3 (" +
		WordColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		WordColumns.NEST_ID + " INTEGER UNSIGNED REFERENCES " + DatabaseSchema.NEST_TABLE_NAME + "(" + DatabaseSchema.NestColumns._ID + ") ON UPDATE CASCADE ON DELETE CASCADE," +
		WordColumns.WORD + " VARCHAR," +
		WordColumns.WORD_LETTER + " VARCHAR," +
		WordColumns.ORDERPOS + " INTEGER," +
		WordColumns.EXAMPLE + " VARCHAR," +
		WordColumns.ETHIMOLOGY + " VARCHAR," +
		WordColumns.DESCRIPTION + " VARCHAR," +
		WordColumns.DERIVATIVES + " VARCHAR," +
		WordColumns.COMMENT_COUNT + " INTEGER," +
		WordColumns.ADDEDBY_URL + " VARCHAR," +
		WordColumns.ADDEDBY_EMAIL + " VARCHAR," +
		WordColumns.ADDEDBY + " VARCHAR," +
		WordColumns.ADDEDAT_DATE + " VARCHAR," +
		WordColumns.ADDEDAT_DATESTAMP + " INTEGER);";

	public DatabaseModel(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("PRAGMA foreign_keys = ON;");
		db.execSQL(SETTINGS_TABLE_CREATE);
		db.execSQL(TEXTCONTENT_TABLE_CREATE);
		db.execSQL(ACCOUNTDATA_TABLE_CREATE);
		db.execSQL(NEST_TABLE_CREATE);
		db.execSQL(WORDS_TABLE_CREATE);
		db.execSQL(WORDSCOMMENTS_TABLE_CREATE);
		InitSettings(db);
		Log.e("DatabaseModel", "Database created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("DatabaseModel", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseSchema.SETTINGS_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseSchema.TEXTCONTENT_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseSchema.ACCOUNTDATA_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseSchema.NEST_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseSchema.WORDS_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DatabaseSchema.WORDSCOMMENTS_TABLE_NAME);
		onCreate(db);
		InitSettings(db);
	}
	
	private void InitSettings(SQLiteDatabase db) {
		String[] settings = new String[4];
		settings[0] = "StorePrivateData|TRUE|1";
		settings[1] = "OnlineSearch|TRUE|1";
		settings[2] = "InAppEmail|FALSE|1";
		settings[3] = "lastSyncDate| |0";

		ContentValues cv;
		for (int i=0; i<settings.length; i++) {
			cv = new ContentValues();
			String[] starr = settings[i].split("\\|");
			cv.put(DatabaseSchema.SettingsColumns.SNAME, starr[0]);
			cv.put(DatabaseSchema.SettingsColumns.SVALUE, starr[1]);
			cv.put(DatabaseSchema.SettingsColumns.EDITABLE_YN, starr[2]);
			db.insert(DatabaseSchema.SETTINGS_TABLE_NAME, null, cv);
		}
		Log.e("DatabaseModel", "Settings initiated");
	}

}
