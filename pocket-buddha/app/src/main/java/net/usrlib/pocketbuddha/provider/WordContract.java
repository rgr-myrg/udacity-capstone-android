package net.usrlib.pocketbuddha.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import net.usrlib.pocketbuddha.mvp.MvpModel;

/**
 * Created by rgr-myrg on 6/21/16.
 */
public class WordContract {
	public static final String CONTENT_AUTHORITY = "net.usrlib.pocketbuddha.provider.word";
	public static final String DAILY_WORD_PATH   = "#";
	public static final String BULK_INSERT_PATH  = "insert_bulk";
	public static final Uri BASE_CONTENT_URI     = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final class DictionaryEntry implements BaseColumns {
		public static final String TABLE_NAME = "pali_dictionary";
		public static final String PALI_NAME_COLUMN  = MvpModel.Dictionary.PALI_NAME_COLUMN;
		public static final String PALI_TERM_COLUMN  = MvpModel.Dictionary.PALI_TERM_COLUMN;
		public static final String DEFINITION_COLUMN = MvpModel.Dictionary.DEFINITION_COLUMN;
		public static final String FAVORITE_COLUMN   = MvpModel.Dictionary.FAVORITE_COLUMN;
		public static final String TIMESTAMP_COLUMN  = MvpModel.Dictionary.TIMESTAMP_COLUMN;

		public static final Uri DAILY_WORD_CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				//.appendPath(DAILY_WORD_PATH)
				.build();

		public static final Uri BULK_INSERT_CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(BULK_INSERT_PATH)
				.build();

		public static final int DAILY_WORD_URI_TYPE  = 300;
		public static final int BULK_INSERT_URI_TYPE = 301;
	}
}
