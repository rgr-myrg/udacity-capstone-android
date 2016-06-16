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
	public static final int ITEM_UPDATE = 102;
	public static final int FAVORITES_BY_TITLE_ASC = 103;
	public static final int FAVORITES_BY_TITLE_DESC = 104;
	public static final int FAVORITES_BY_DATE_ASC = 105;
	public static final int FAVORITES_BY_DATE_DESC = 106;

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
			case FAVORITES_BY_TITLE_ASC:
				cursor = dbHelper.selectFavoritesByTitleAsc();
				break;
			case FAVORITES_BY_TITLE_DESC:
				cursor = dbHelper.selectFavoritesByTitleDesc();
				break;
			case FAVORITES_BY_DATE_ASC:
				cursor = dbHelper.selectFavoritesByDateAsc();
				break;
			case FAVORITES_BY_DATE_DESC:
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

			case FAVORITES_BY_TITLE_ASC:
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
	public synchronized int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		int rowUpdated = 0;
		final int uriType = sUriMatcher.match(uri);

		String table = null;

		switch (uriType) {
			case ITEM_UPDATE:
				table = FeedContract.ItemsEntry.TABLE_NAME_ITEMS;
				break;
		}

		final DbHelper dbHelper = DbHelper.getInstance(getContext());
		final SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.beginTransaction();

		try {
			rowUpdated = db.update(table, values, where, whereArgs);

//			if (rowUpdated <= 0) {
//				throw new SQLException("Failed to update row into " + table);
//			}

			db.setTransactionSuccessful();

		} finally {
			db.endTransaction();
		}

		return rowUpdated;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) throws SQLException {
		int rowsInserted = 0;
		final int uriType = sUriMatcher.match(uri);

		String table = null;

		switch (uriType) {
			case ITEMS_BULK_INSERT:
				table = FeedContract.ItemsEntry.TABLE_NAME_ITEMS;
				break;
		}

		final DbHelper dbHelper = DbHelper.getInstance(getContext());
		final SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.beginTransaction();

		try {
			for (ContentValues item : values) {
				long newID = db.insertOrThrow(table, null, item);

				if (newID <= 0) {
					throw new SQLException("Failed to insert row into " + table);
				}
			}

			db.setTransactionSuccessful();
			rowsInserted = values.length;

		} finally {
			db.endTransaction();
		}

		return rowsInserted;
	}

	public static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = FeedContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, FeedContract.PATH_ITEMS, ITEMS);
		matcher.addURI(authority, FeedContract.PATH_BULK_INSERT, ITEMS_BULK_INSERT);
		matcher.addURI(authority, FeedContract.PATH_ITEM_UPDATE, ITEM_UPDATE);
		matcher.addURI(authority, FeedContract.PATH_FAVORITES, FAVORITES_BY_DATE_DESC);

		//matcher.addURI(authority, FeedContract.PATH_FAVORITES + "/*", FAVORITES_BY_TITLE_ASC);

		return matcher;
	}
}
