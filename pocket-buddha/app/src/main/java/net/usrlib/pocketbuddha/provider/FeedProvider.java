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

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	@Override
	public synchronized boolean onCreate() {
		return true;
	}

	@Nullable
	@Override
	public synchronized Cursor query(Uri uri,
									 String[] projection,
									 String selection,
									 String[] selectionArgs,
									 String sortOrder) {
		final int uriType = sUriMatcher.match(uri);
		final DbHelper db = DbHelper.getInstance(getContext());

		if (db == null) {
			return null;
		}

		final SQLiteDatabase sqlLite = db.getWritableDatabase();

		if (sqlLite == null) {
			return null;
		}

		String sqlStr = null;

		switch (uriType) {
			case FeedContract.ItemsEntry.URI_TYPE_ALL_ITEMS:
				sqlStr = DbHelper.SELECT_ALL_BY_TITLE;
				break;

			case FeedContract.ItemsEntry.URI_TYPE_FAVORITES_BY_TITLE_ASC:
				sqlStr = DbHelper.SELECT_FAVORITES_BY_TITLE + DbHelper.ORDER_BY_ASC;
				break;

			case FeedContract.ItemsEntry.URI_TYPE_FAVORITES_BY_TITLE_DESC:
				sqlStr = DbHelper.SELECT_FAVORITES_BY_TITLE + DbHelper.ORDER_BY_DESC;
				break;

			case FeedContract.ItemsEntry.URI_TYPE_FAVORITES_BY_DATE_ASC:
				sqlStr = DbHelper.SELECT_FAVORITES_BY_DATE + DbHelper.ORDER_BY_ASC;
				break;

			case FeedContract.ItemsEntry.URI_TYPE_FAVORITES_BY_DATE_DESC:
				sqlStr = DbHelper.SELECT_FAVORITES_BY_DATE + DbHelper.ORDER_BY_DESC;
				break;

//			case FeedContract.ItemsEntry.URI_TYPE_ITEMS_TITLE_SEARCH:
//				sqlStr = DbHelper.SEARCH_BY_TITLE;
//				break;
		}

//		final SQLiteDatabase sqlLite = db.getWritableDatabase();
//
		if (sqlStr == null) {
			return null;
		}

		return sqlLite.rawQuery(sqlStr, null);
	}

	@Nullable
	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);

		switch (match) {
			case FeedContract.ItemsEntry.URI_TYPE_ALL_ITEMS:
				return FeedContract.ItemsEntry.CONTENT_TYPE;

			case FeedContract.ItemsEntry.URI_TYPE_FAVORITES_BY_TITLE_ASC:
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
			case FeedContract.ItemsEntry.URI_TYPE_ITEM_UPDATE:
				table = FeedContract.ItemsEntry.TABLE_NAME_ITEMS;
				break;
		}

		final DbHelper dbHelper = DbHelper.getInstance(getContext());
		final SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.beginTransaction();

		try {
			rowUpdated = db.update(table, values, where, whereArgs);
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
			case FeedContract.ItemsEntry.URI_TYPE_ITEMS_BULK_INSERT:
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

		matcher.addURI(
				authority,
				FeedContract.PATH_ITEMS,
				FeedContract.ItemsEntry.URI_TYPE_ALL_ITEMS
		);

		matcher.addURI(
				authority,
				FeedContract.PATH_ITEMS_INSERT_BULK,
				FeedContract.ItemsEntry.URI_TYPE_ITEMS_BULK_INSERT
		);

		matcher.addURI(
				authority,
				FeedContract.PATH_ITEM_UPDATE,
				FeedContract.ItemsEntry.URI_TYPE_ITEM_UPDATE
		);

		matcher.addURI(
				authority,
				FeedContract.PATH_FAVORITES_BY_DATE_ASC,
				FeedContract.ItemsEntry.URI_TYPE_FAVORITES_BY_DATE_ASC
		);

		matcher.addURI(
				authority,
				FeedContract.PATH_FAVORITES_BY_DATE_DESC,
				FeedContract.ItemsEntry.URI_TYPE_FAVORITES_BY_DATE_DESC
		);

		matcher.addURI(
				authority,
				FeedContract.PATH_FAVORITES_BY_TITLE_ASC,
				FeedContract.ItemsEntry.URI_TYPE_FAVORITES_BY_TITLE_ASC
		);

		matcher.addURI(
				authority,
				FeedContract.PATH_FAVORITES_BY_TITLE_DESC,
				FeedContract.ItemsEntry.URI_TYPE_FAVORITES_BY_TITLE_DESC
		);

//		matcher.addURI(
//				authority,
//				FeedContract.PATH_ITEMS_TITLE_SEARCH,
//				FeedContract.ItemsEntry.URI_TYPE_ITEMS_TITLE_SEARCH
//		);

		return matcher;
	}
}
