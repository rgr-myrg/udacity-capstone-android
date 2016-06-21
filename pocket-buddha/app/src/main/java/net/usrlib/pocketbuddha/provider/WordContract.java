package net.usrlib.pocketbuddha.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rgr-myrg on 6/21/16.
 */
public class WordContract {
	public static final String CONTENT_AUTHORITY = "net.usrlib.pocketbuddha.provider.word";
	public static final String DAILY_WORD_PATH   = "#";
	public static final Uri BASE_CONTENT_URI     = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final class WordEntry implements BaseColumns {
		public static final String TABLE_NAME = "pali_words";
		public static final String WORD_ITEM_COLUMN   = "word_item";
		public static final String DESCRIPTION_COLUMN = "description";
		public static final String FAVORITE_COLUMN    = "favorite";
		public static final String TIMESTAMP_COLUMN   = "timestamp";

		public static final Uri DAILY_WORD_CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(DAILY_WORD_PATH)
				.build();

		public static final int DAILY_WORD_URI_TYPE = 300;
	}
}
