package net.supudo.apps.aNeolog.Database;

import android.provider.BaseColumns;

public class DatabaseSchema {

	public static final String SETTINGS_TABLE_NAME = "settings";
	public static final String TEXTCONTENT_TABLE_NAME = "staticcontent";
	public static final String ACCOUNTDATA_TABLE_NAME = "accountdata";
	public static final String NEST_TABLE_NAME = "nests";
	public static final String WORDS_TABLE_NAME = "words";
	public static final String WORDSCOMMENTS_TABLE_NAME = "wordscomments";

	public static final class SettingsColumns implements BaseColumns {
		private SettingsColumns(){};
		public static final String EDITABLE_YN = "editableyn";
		public static final String SNAME = "sname";
		public static final String SVALUE = "svalue";
	}

	public static final class TextContentColumns implements BaseColumns {
		private TextContentColumns(){};
		public static final String TITLE = "content";
		public static final String CONTENT = "title";
	}

	public static final class AccountDataColumns implements BaseColumns {
		private AccountDataColumns(){};
		public static final String EMAIL = "email";
		public static final String NAME = "name";
		public static final String NESTID = "nestid";
		public static final String URL = "url";
	}

	public static final class NestColumns implements BaseColumns {
		private NestColumns(){};
		public static final String ORDERPOS = "orderpos";
		public static final String TITLE = "title";
	}

	public static final class WordCommentsColumns implements BaseColumns {
		private WordCommentsColumns(){};
		public static final String WORD_ID = "wordid";
		public static final String AUTHOR = "author";
		public static final String COMMENT = "comment";
		public static final String COMMENT_DATE = "commentdate";
		public static final String COMMENT_DATESTAMP = "commentdatestamp";
	}

	public static final class WordColumns implements BaseColumns {
		private WordColumns(){};
		public static final String WORD = "word";
		public static final String WORD_LETTER = "wordletter";
		public static final String ORDERPOS = "orderpos";
		public static final String NEST_ID = "nestid";
		public static final String EXAMPLE = "example";
		public static final String ETHIMOLOGY = "ethimology";
		public static final String DESCRIPTION = "description";
		public static final String DERIVATIVES = "derivatives";
		public static final String COMMENT_COUNT = "commentcount";
		public static final String ADDEDBY_URL = "addedbyurl";
		public static final String ADDEDBY_EMAIL = "addedbyemail";
		public static final String ADDEDBY = "addedby";
		public static final String ADDEDAT_DATE = "addedatdate";
		public static final String ADDEDAT_DATESTAMP = "addedatdatestamp";
	}
	
}
