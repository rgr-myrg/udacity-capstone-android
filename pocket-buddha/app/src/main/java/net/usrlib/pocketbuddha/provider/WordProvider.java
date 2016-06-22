package net.usrlib.pocketbuddha.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import net.usrlib.pocketbuddha.sql.DbHelper;

/**
 * Created by rgr-myrg on 6/21/16.
 */
public class WordProvider extends ContentProvider {
	private static final UriMatcher sUriMatcher = buildUriMatcher();

	@Override
	public boolean onCreate() {
		return false;
	}

	@Nullable
	@Override
	public Cursor query(Uri uri,
	                    String[] projection,
	                    String selection,
	                    String[] selectionArgs,
	                    String sortOrder) {
		final int uriType = sUriMatcher.match(uri);
		final DbHelper db = DbHelper.getInstance(getContext());

		if (db == null) {
			return null;
		}

		final SQLiteDatabase sqlite = db.getReadableDatabase();

		if (sqlite == null) {
			return null;
		}

		Cursor cursor = null;

		switch (uriType) {
			case WordContract.DictionaryEntry.DAILY_WORD_URI_TYPE:
				cursor = getNextDailyWord(sqlite, uri);
				break;
		}

		return cursor;
	}

	@Nullable
	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Nullable
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int rowsInserted  = 0;
		final int uriType = sUriMatcher.match(uri);

		String table = null;

		switch (uriType) {
			case WordContract.DictionaryEntry.BULK_INSERT_URI_TYPE:
				table = WordContract.DictionaryEntry.TABLE_NAME;
				break;
		}

		if (table == null) {
			return -1;
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

	public Cursor getNextDailyWord(final SQLiteDatabase sqlite, final Uri uri) {
		final int itemId = Integer.valueOf(uri.getLastPathSegment());

		Log.d("WORD", "getNextDailyWord: " + uri);
		Log.d("WORD", "getNextDailyWord itemId: " + itemId);

		// SELECT * FROM table WHERE _id > itemId ORDER BY _id LIMIT 1
		return sqlite.query(
				true,
				WordContract.DictionaryEntry.TABLE_NAME,
				null,
				BaseColumns._ID + " > ?",
				new String[] {
						String.valueOf(itemId)
				},
				null, null,
				BaseColumns._ID + " ASC",
				"1"
		);
	}

	public static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = WordContract.CONTENT_AUTHORITY;

		matcher.addURI(
				authority,
				WordContract.DAILY_WORD_PATH,
				WordContract.DictionaryEntry.DAILY_WORD_URI_TYPE
		);

		matcher.addURI(
				authority,
				WordContract.BULK_INSERT_PATH,
				WordContract.DictionaryEntry.BULK_INSERT_URI_TYPE
		);

		return  matcher;
	}
}
