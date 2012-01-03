package net.supudo.apps.aNeolog.Database;

import java.util.ArrayList;

import net.supudo.apps.aNeolog.Database.Models.NestModel;
import net.supudo.apps.aNeolog.Database.Models.SettingModel;
import net.supudo.apps.aNeolog.Database.Models.TextContentModel;
import net.supudo.apps.aNeolog.Database.Models.WordCommentModel;
import net.supudo.apps.aNeolog.Database.Models.WordModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataHelper {

	private Context mCtx;
	private DatabaseModel dbModel;

	public DataHelper(Context ctx) {
		mCtx = ctx;
		dbModel = new DatabaseModel(mCtx);
	}

	/* ------------------------------------------
	 * 
	 * Settings
	 * 
	 * ------------------------------------------
	 */
	public ArrayList<SettingModel> selectAllSettings() {
		return selectSettings(null, null);
	}

	public ArrayList<SettingModel> selectSettings(String selection, String[] args) {
		SQLiteDatabase db = dbModel.getReadableDatabase();
		Cursor c = db.query(DatabaseSchema.SETTINGS_TABLE_NAME, new String[] {
					DatabaseSchema.SettingsColumns._ID,
					DatabaseSchema.SettingsColumns.EDITABLE_YN,
					DatabaseSchema.SettingsColumns.SNAME,
					DatabaseSchema.SettingsColumns.SVALUE},
				selection, args, null, null, null);
		c.moveToFirst();
		ArrayList<SettingModel> resu = new ArrayList<SettingModel>();
		while (c.isAfterLast() == false)
		{
			boolean editableYn = (c.getInt(c.getColumnIndex(DatabaseSchema.SettingsColumns.EDITABLE_YN)) == 1);
			String sname = c.getString(c.getColumnIndex(DatabaseSchema.SettingsColumns.SNAME));
			String svalue = c.getString(c.getColumnIndex(DatabaseSchema.SettingsColumns.SVALUE));
			SettingModel model = new SettingModel(editableYn, sname, svalue);
			resu.add(model);
			c.moveToNext();
		}
		c.close();
		db.close();
		return resu;
	}

	public ArrayList<SettingModel> selectEditableSettings() {
		SQLiteDatabase db = dbModel.getReadableDatabase();
		Cursor c = db.query(DatabaseSchema.SETTINGS_TABLE_NAME, new String[] {
					DatabaseSchema.SettingsColumns._ID,
					DatabaseSchema.SettingsColumns.EDITABLE_YN,
					DatabaseSchema.SettingsColumns.SNAME,
					DatabaseSchema.SettingsColumns.SVALUE},
					DatabaseSchema.SettingsColumns.EDITABLE_YN + " = 1", null, null, null, null);
		c.moveToFirst();
		ArrayList<SettingModel> resu = new ArrayList<SettingModel>();
		while (c.isAfterLast() == false)
		{
			boolean editableYn = (c.getInt(c.getColumnIndex(DatabaseSchema.SettingsColumns.EDITABLE_YN)) == 1);
			String sname = c.getString(c.getColumnIndex(DatabaseSchema.SettingsColumns.SNAME));
			String svalue = c.getString(c.getColumnIndex(DatabaseSchema.SettingsColumns.SVALUE));
			SettingModel model = new SettingModel(editableYn, sname, svalue);
			resu.add(model);
			c.moveToNext();
		}
		c.close();
		db.close();
		return resu;
	}
	
	public SettingModel GetSetting(String sName) {
		ArrayList<SettingModel> ar = selectSettings(DatabaseSchema.SettingsColumns.SNAME + " = ?", new String[]{"" + sName});
		if (ar.size() > 0)
			return ar.get(0);
		return null;
	}
	
	public boolean SetSetting(String sName, String sValue) {
		try {
			SQLiteDatabase db = new DatabaseModel(mCtx).getReadableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(DatabaseSchema.SettingsColumns.SVALUE, sValue);
			db.update(DatabaseSchema.SETTINGS_TABLE_NAME, cv, DatabaseSchema.SettingsColumns.SNAME + " = ?", new String[]{"" + sName});
			db.close();
		}
		catch (Exception ex) {
			Log.d("DataHelper", "Setting save failed - " + sName + " : " + sValue);
			return false;
		}
		return true;
	}

	/* ------------------------------------------
	 * 
	 * Text Content
	 * 
	 * ------------------------------------------
	 */
	public ArrayList<TextContentModel> selectAllTextContent() {
		return selectTextContent(null, null);
	}

	public ArrayList<TextContentModel> selectTextContent(String selection, String[] args) {
		SQLiteDatabase db = dbModel.getReadableDatabase();
		Cursor c = db.query(DatabaseSchema.TEXTCONTENT_TABLE_NAME, new String[] {
					DatabaseSchema.TextContentColumns._ID,
					DatabaseSchema.TextContentColumns.TITLE,
					DatabaseSchema.TextContentColumns.CONTENT},
				selection, args, null, null, null);
		c.moveToFirst();
		ArrayList<TextContentModel> resu = new ArrayList<TextContentModel>();
		while (c.isAfterLast() == false) {
			Integer cid = c.getInt(c.getColumnIndex(DatabaseSchema.TextContentColumns._ID));
			String title = c.getString(c.getColumnIndex(DatabaseSchema.TextContentColumns.TITLE));
			String content = c.getString(c.getColumnIndex(DatabaseSchema.TextContentColumns.CONTENT));
			TextContentModel model = new TextContentModel(cid, title, content);
			resu.add(model);
			c.moveToNext();
		}
		c.close();
		db.close();
		return resu;
	}
	
	public TextContentModel GetTextContent(Integer cid) {
		ArrayList<TextContentModel> ar = selectTextContent(DatabaseSchema.TextContentColumns._ID + " = ?", new String[]{"" + cid});
		if (ar.size() > 0)
			return ar.get(0);
		return null;
	}
	
	public String GetTextContentForSetting(String sName) {
		if (sName.equalsIgnoreCase("StorePrivateData"))
			return GetTextContent(36).Content;
		else if (sName.equalsIgnoreCase("SendGeo"))
			return GetTextContent(37).Content;
		else if (sName.equalsIgnoreCase("InitSync"))
			return GetTextContent(38).Content;
		else if (sName.equalsIgnoreCase("OnlineSearch"))
			return GetTextContent(39).Content;
		else if (sName.equalsIgnoreCase("InAppEmail"))
			return GetTextContent(40).Content;
		else if (sName.equalsIgnoreCase("ShowCategories"))
			return GetTextContent(41).Content;
		else
			return "";
	}

	/* ------------------------------------------
	 * 
	 * Nests
	 * 
	 * ------------------------------------------
	 */
	public ArrayList<NestModel> selectNests() {
		return this.selectNests(null, null, null);
	}
	
	public NestModel GetNestName(int nestID) {
		ArrayList<NestModel> ar = selectNests(DatabaseSchema.NestColumns._ID + " = ?", new String[]{"" + nestID}, null);
		if (ar.size() > 0)
			return ar.get(0);
		return null;
	}

	private ArrayList<NestModel> selectNests(String selection, String[] args, String queryLimit) {
		SQLiteDatabase db = dbModel.getReadableDatabase();
		Cursor c = db.query(DatabaseSchema.NEST_TABLE_NAME, new String[] {
					DatabaseSchema.NestColumns._ID,
					DatabaseSchema.NestColumns.TITLE,
					DatabaseSchema.NestColumns.ORDERPOS},
				selection, args, null, null, DatabaseSchema.NestColumns.ORDERPOS + " DESC", queryLimit);
		c.moveToFirst();
		ArrayList<NestModel> resu = new ArrayList<NestModel>();
		while (c.isAfterLast() == false) {
			Integer wordID = c.getInt(c.getColumnIndex(DatabaseSchema.NestColumns._ID));
			Integer orderPos = c.getInt(c.getColumnIndex(DatabaseSchema.NestColumns.ORDERPOS));
			String name = c.getString(c.getColumnIndex(DatabaseSchema.NestColumns.TITLE));

			NestModel model = new NestModel(wordID, name, orderPos);
			resu.add(model);
			c.moveToNext();
		}
		c.close();
		db.close();
		return resu;
	}

	/* ------------------------------------------
	 * 
	 * Words
	 * 
	 * ------------------------------------------
	 */
	public ArrayList<WordModel> searchWords(String searchQuery) {
		String whereClause = "LOWER(" + DatabaseSchema.WordColumns.WORD + ") LIKE '%" + searchQuery.toLowerCase() + "%' OR LOWER(" + DatabaseSchema.WordColumns.WORD + ") LIKE '%" + searchQuery.toUpperCase() + "%'";
		return selectWords(whereClause, null, null);
	}

	public ArrayList<WordModel> selectWordsForNest(int nestID) {
		String whereClause = DatabaseSchema.WordColumns.NEST_ID + " = " + nestID;
		return selectWords(whereClause, null, null);
	}

	public ArrayList<WordModel> selectWordsForLetter(String letter) {
		String whereClause = "LOWER(" + DatabaseSchema.WordColumns.WORD_LETTER + ") LIKE '%" + letter.toLowerCase() + "%' OR LOWER(" + DatabaseSchema.WordColumns.WORD_LETTER + ") LIKE '%" + letter.toUpperCase() + "%'";
		return selectWords(whereClause, null, null);
	}

	public WordModel GetWord(Integer wordID) {
		ArrayList<WordModel> ar = selectWords(DatabaseSchema.WordColumns._ID + " = " + wordID, null, null);
		if (ar.size() > 0)
			return ar.get(0);
		return null;
	}

	private ArrayList<WordModel> selectWords(String selection, String[] args, String queryLimit) {
		SQLiteDatabase db = dbModel.getReadableDatabase();
		Cursor c = db.query(DatabaseSchema.WORDS_TABLE_NAME, new String[] {
					DatabaseSchema.WordColumns._ID,
					DatabaseSchema.WordColumns.ADDEDAT_DATE,
					DatabaseSchema.WordColumns.ADDEDAT_DATESTAMP,
					DatabaseSchema.WordColumns.ADDEDBY,
					DatabaseSchema.WordColumns.ADDEDBY_EMAIL,
					DatabaseSchema.WordColumns.ADDEDBY_URL,
					DatabaseSchema.WordColumns.COMMENT_COUNT,
					DatabaseSchema.WordColumns.DERIVATIVES,
					DatabaseSchema.WordColumns.DESCRIPTION,
					DatabaseSchema.WordColumns.ETHIMOLOGY,
					DatabaseSchema.WordColumns.EXAMPLE,
					DatabaseSchema.WordColumns.NEST_ID,
					DatabaseSchema.WordColumns.ORDERPOS,
					DatabaseSchema.WordColumns.WORD,
					DatabaseSchema.WordColumns.WORD_LETTER},
				selection, args, null, null, DatabaseSchema.WordColumns.ORDERPOS + " DESC", queryLimit);
		c.moveToFirst();
		ArrayList<WordModel> resu = new ArrayList<WordModel>();
		while (c.isAfterLast() == false) {
			Integer wordID = c.getInt(c.getColumnIndex(DatabaseSchema.WordColumns._ID));
			String word = c.getString(c.getColumnIndex(DatabaseSchema.WordColumns.WORD));
			String wordLetter = c.getString(c.getColumnIndex(DatabaseSchema.WordColumns.WORD_LETTER));
			Integer orderPos = c.getInt(c.getColumnIndex(DatabaseSchema.WordColumns.ORDERPOS));
			Integer nestID = c.getInt(c.getColumnIndex(DatabaseSchema.WordColumns.NEST_ID));
			String example = c.getString(c.getColumnIndex(DatabaseSchema.WordColumns.EXAMPLE));
			String ethimology = c.getString(c.getColumnIndex(DatabaseSchema.WordColumns.ETHIMOLOGY));
			String description = c.getString(c.getColumnIndex(DatabaseSchema.WordColumns.DESCRIPTION));
			String derivatives = c.getString(c.getColumnIndex(DatabaseSchema.WordColumns.DERIVATIVES));
			Integer commentCount = c.getInt(c.getColumnIndex(DatabaseSchema.WordColumns.COMMENT_COUNT));
			String addedBy = c.getString(c.getColumnIndex(DatabaseSchema.WordColumns.ADDEDBY));
			String addedBy_URL = c.getString(c.getColumnIndex(DatabaseSchema.WordColumns.ADDEDBY_URL));
			String addedBy_Email = c.getString(c.getColumnIndex(DatabaseSchema.WordColumns.ADDEDBY_EMAIL));
			String addedAt_Date = c.getString(c.getColumnIndex(DatabaseSchema.WordColumns.ADDEDAT_DATE));
			long addedAt_Datestamp = c.getLong(c.getColumnIndex(DatabaseSchema.WordColumns.ADDEDAT_DATESTAMP));

			WordModel model = new WordModel(wordID, word, wordLetter, orderPos, nestID, example, ethimology, description, derivatives, commentCount, addedBy, addedBy_URL, addedBy_Email, addedAt_Date, addedAt_Datestamp);
			resu.add(model);
			c.moveToNext();
		}
		c.close();
		db.close();
		return resu;
	}

	/* ------------------------------------------
	 * 
	 * Word comments
	 * 
	 * ------------------------------------------
	 */
	public ArrayList<WordCommentModel> selectWordComments(int wordID) {
		String whereClause = DatabaseSchema.WordCommentsColumns.WORD_ID + " = " + wordID;
		return selectWordComments(whereClause, null, null);
	}

	private ArrayList<WordCommentModel> selectWordComments(String selection, String[] args, String queryLimit) {
		SQLiteDatabase db = dbModel.getReadableDatabase();
		Cursor c = db.query(DatabaseSchema.WORDSCOMMENTS_TABLE_NAME, new String[] {
					DatabaseSchema.WordCommentsColumns._ID,
					DatabaseSchema.WordCommentsColumns.AUTHOR,
					DatabaseSchema.WordCommentsColumns.COMMENT,
					DatabaseSchema.WordCommentsColumns.COMMENT_DATE,
					DatabaseSchema.WordCommentsColumns.COMMENT_DATESTAMP,
					DatabaseSchema.WordCommentsColumns.WORD_ID},
				selection, args, null, null, DatabaseSchema.WordCommentsColumns.COMMENT_DATE + " DESC", queryLimit);
		c.moveToFirst();
		ArrayList<WordCommentModel> resu = new ArrayList<WordCommentModel>();
		while (c.isAfterLast() == false) {
			Integer commentID = c.getInt(c.getColumnIndex(DatabaseSchema.WordColumns._ID));
			String author = c.getString(c.getColumnIndex(DatabaseSchema.WordCommentsColumns.AUTHOR));
			String comment = c.getString(c.getColumnIndex(DatabaseSchema.WordCommentsColumns.COMMENT));
			String commentDate = c.getString(c.getColumnIndex(DatabaseSchema.WordCommentsColumns.COMMENT_DATE));
			long commentDatestamp = c.getLong(c.getColumnIndex(DatabaseSchema.WordCommentsColumns.COMMENT_DATESTAMP));
			Integer wordID = c.getInt(c.getColumnIndex(DatabaseSchema.WordCommentsColumns.WORD_ID));

			WordCommentModel model = new WordCommentModel(commentID, wordID, author, comment, commentDate, commentDatestamp);
			resu.add(model);
			c.moveToNext();
		}
		c.close();
		db.close();
		return resu;
	}
}
