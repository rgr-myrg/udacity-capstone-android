package net.usrlib.pocketbuddha.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import net.usrlib.pocketbuddha.mvp.MvpModel;

/**
 * Created by rgr-myrg on 6/19/16.
 */
public class SearchContract {
	public static final String CONTENT_AUTHORITY = "net.usrlib.pocketbuddha.provider.search";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final String SEARCH_SUGGEST_PATH = "search_suggest_query/*";
	public static final String SEARCH_TERM_PATH    = "#";
	public static final String SEARCH_TEXT_PATH    = "search_text_query";

	public static final class SearchEntry implements BaseColumns {

		public static final String TABLE_NAME_ITEMS = "feed_items";
		public static final String TITLE_COLUMN     = MvpModel.TITLE_COLUMN;
		public static final String ENGLISH_COLUMN   = MvpModel.ENGLISH_COLUMN;

		public static final Uri SEARCH_SUGGEST_CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(SEARCH_SUGGEST_PATH)
				.build();

		public static final Uri SEARCH_TERM_CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(SEARCH_TERM_PATH)
				.build();

		public static final Uri SEARCH_TEXT_CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(SEARCH_TEXT_PATH)
				.build();

		public static final int SEARCH_SUGGEST_URI_TYPE = 200;
		public static final int SEARCH_TERM_URI_TYPE    = 201;
		public static final int SEARCH_TEXT_URI_TYPE    = 202;
	}
}
