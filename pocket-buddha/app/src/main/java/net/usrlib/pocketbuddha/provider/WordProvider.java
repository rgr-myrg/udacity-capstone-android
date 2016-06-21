package net.usrlib.pocketbuddha.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

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
			case WordContract.WordEntry.DAILY_WORD_URI_TYPE:
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

	public Cursor getNextDailyWord(final SQLiteDatabase sqlite, final Uri uri) {
		final int itemId = Integer.valueOf(uri.getLastPathSegment());

		// SELECT * FROM word_items WHERE _id > itemId ORDER BY _id LIMIT 1
		return sqlite.query(
				true,
				WordContract.WordEntry.TABLE_NAME,
				null,
				BaseColumns._ID + " > ?",
				new String[] {
						String.valueOf(itemId)
				},
				null, null,
				BaseColumns._ID + "ASC",
				"1"
		);
	}

	public static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = WordContract.CONTENT_AUTHORITY;

		matcher.addURI(
				authority,
				WordContract.DAILY_WORD_PATH,
				WordContract.WordEntry.DAILY_WORD_URI_TYPE
		);

		return  matcher;
	}
}
