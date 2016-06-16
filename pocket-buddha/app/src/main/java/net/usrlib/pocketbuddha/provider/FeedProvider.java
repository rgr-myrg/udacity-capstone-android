package net.usrlib.pocketbuddha.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import net.usrlib.pocketbuddha.data.DbHelper;

/**
 * Created by home on 6/6/16.
 */
public class FeedProvider extends ContentProvider {
	public static final String AUTHORITY = "net.usrlib.pocketbuddha.provider.feed";
	public static final String BASE_PATH = "items";
	public static final Uri CONTENT_URI  = Uri.parse("content://" + AUTHORITY + BASE_PATH);
	// CONTENT_URI = content://net.usrlib.pocketbuddha.provider/items

	public static final int ITEMS = 100;
	public static final int ITEMS_BULK_INSERT = 101;
	public static final int ITEMS_WITH_FAVORITES_BY_TITLE_ASC  = 102;
	public static final int ITEMS_WITH_FAVORITES_BY_TITLE_DESC = 103;
	public static final int ITEMS_WITH_FAVORITES_BY_DATE_ASC   = 104;
	public static final int ITEMS_WITH_FAVORITES_BY_DATE_DESC  = 105;

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private DbHelper dbHelper = null;

	@Override
	public synchronized boolean onCreate() {
		dbHelper = DbHelper.getInstance(getContext());
		return dbHelper != null;
	}

	@Nullable
	@Override
	public synchronized Cursor query(Uri uri,
	                                 String[] projection,
	                                 String selection,
	                                 String[] selectionArgs,
	                                 String sortOrder) {
		Cursor cursor = null;

		switch (sUriMatcher.match(uri)) {
			case ITEMS:
				cursor = dbHelper.selectAll();
				break;
			case ITEMS_WITH_FAVORITES_BY_TITLE_ASC:
				cursor = dbHelper.selectFavoritesByTitleAsc();
				break;
			case ITEMS_WITH_FAVORITES_BY_TITLE_DESC:
				cursor = dbHelper.selectFavoritesByTitleDesc();
				break;
			case ITEMS_WITH_FAVORITES_BY_DATE_ASC:
				cursor = dbHelper.selectFavoritesByDateAsc();
				break;
			case ITEMS_WITH_FAVORITES_BY_DATE_DESC:
				cursor = dbHelper.selectFavoritesByDateDesc();
				break;

		}

		return cursor;
	}

	@Nullable
	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
			case ITEMS:
				return FeedContract.ItemsEntry.CONTENT_TYPE;

			case ITEMS_WITH_FAVORITES_BY_TITLE_ASC:
				return FeedContract.ItemsEntry.CONTENT_TYPE;

			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Nullable
	@Override
	public synchronized Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		final int match = sUriMatcher.match(uri);
		return 0;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) throws SQLException {
		int newRowsInserted = 0;
		int uriType = sUriMatcher.match(uri);

		String table = null;

		switch (uriType) {
			case ITEMS_BULK_INSERT:
				table = FeedContract.ItemsEntry.TABLE_NAME_ITEMS;
				break;
		}

		DbHelper dbHelper = DbHelper.getInstance(getContext());
		SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();

		sqlDB.beginTransaction();

		try {
			for (ContentValues item : values) {
				long newID = sqlDB.insertOrThrow(table, null, item);

				if (newID <= 0) {
					throw new SQLException("Failed to insert row into " + uri);
				}
			}

			sqlDB.setTransactionSuccessful();
			newRowsInserted = values.length;

		} finally {
			sqlDB.endTransaction();
		}

		return newRowsInserted;
	}

	public static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = FeedContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, FeedContract.PATH_ITEMS, ITEMS);
		matcher.addURI(authority, FeedContract.PATH_BULK_INSERT, ITEMS_BULK_INSERT);
		matcher.addURI(authority, FeedContract.PATH_ITEMS_WITH_FAVORITES + "/*", ITEMS_WITH_FAVORITES_BY_TITLE_ASC);

		return matcher;
	}
}
