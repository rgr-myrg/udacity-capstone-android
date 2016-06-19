package net.usrlib.pocketbuddha.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import net.usrlib.pocketbuddha.mvp.MvpModel;

/**
 * Created by rgr-myrg on 6/7/16.
 */
public final class FeedContract {
	public static final String CONTENT_AUTHORITY = "net.usrlib.pocketbuddha.provider";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final String PATH_ITEMS = "items";
	public static final String PATH_ITEMS_INSERT_BULK  = "items_insert_bulk";
//	public static final String PATH_ITEMS_TITLE_SEARCH = "items_title_search";
	public static final String PATH_ITEM_UPDATE = "item_update";

	public static final String PATH_FAVORITES_BY_DATE_ASC   = "favorites_date_asc";
	public static final String PATH_FAVORITES_BY_DATE_DESC  = "favorites_date_desc";
	public static final String PATH_FAVORITES_BY_TITLE_ASC  = "favorites_title_asc";
	public static final String PATH_FAVORITES_BY_TITLE_DESC = "favorites_title_desc";

	public static final class ItemsEntry implements BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH_ITEMS)
				.build();

		public static final Uri CONTENT_BULK_INSERT_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH_ITEMS_INSERT_BULK)
				.build();

		public static final Uri CONTENT_ITEM_UPDATE_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH_ITEM_UPDATE)
				.build();

		public static final Uri CONTENT_FAVORITES_BY_DATE_ASC_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH_FAVORITES_BY_DATE_ASC)
				.build();

		public static final Uri CONTENT_FAVORITES_BY_DATE_DESC_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH_FAVORITES_BY_DATE_DESC)
				.build();

		public static final Uri CONTENT_FAVORITES_BY_TITLE_ASC_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH_FAVORITES_BY_TITLE_ASC)
				.build();

		public static final Uri CONTENT_FAVORITES_BY_TITLE_DESC_URI = BASE_CONTENT_URI
				.buildUpon()
				.appendPath(PATH_FAVORITES_BY_TITLE_DESC)
				.build();

		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + CONTENT_AUTHORITY
				+ "/" + PATH_ITEMS;

//		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
//				+ "/" + CONTENT_AUTHORITY
//				+ "/" + PATH_ITEMS;

//		public static final Uri CONTENT_TITLE_SEARCH_URI = BASE_CONTENT_URI
//				.buildUpon()
//				.appendPath(PATH_ITEMS_TITLE_SEARCH)
//				.build();

		public static final String TABLE_NAME_ITEMS = "feed_items";
		public static final String TITLE_COLUMN     = MvpModel.TITLE_COLUMN;
		public static final String PALI_COLUMN      = MvpModel.PALI_COLUMN;
		public static final String ENGLISH_COLUMN   = MvpModel.ENGLISH_COLUMN;
		public static final String MP3_LINK_COLUMN  = MvpModel.MP3_LINK_COLUMN;
		public static final String IMAGE_URL_COLUMN = MvpModel.IMAGE_URL_COLUMN;
		public static final String AUTHOR_COLUMN    = MvpModel.AUTHOR_COLUMN;
		public static final String SUBJECT_COLUMN   = MvpModel.SUBJECT_COLUMN;
		public static final String FAVORITE_COLUMN  = MvpModel.FAVORITE_COLUMN;

		public static final int URI_TYPE_ALL_ITEMS = 100;
		public static final int URI_TYPE_ITEMS_BULK_INSERT = 101;
		public static final int URI_TYPE_ITEM_UPDATE = 102;
		public static final int URI_TYPE_FAVORITES_BY_TITLE_ASC  = 103;
		public static final int URI_TYPE_FAVORITES_BY_TITLE_DESC = 104;
		public static final int URI_TYPE_FAVORITES_BY_DATE_ASC   = 105;
		public static final int URI_TYPE_FAVORITES_BY_DATE_DESC  = 106;

//		public static final int URI_TYPE_ITEMS_TITLE_SEARCH = 107;

		public static Uri buildItemsUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
}
