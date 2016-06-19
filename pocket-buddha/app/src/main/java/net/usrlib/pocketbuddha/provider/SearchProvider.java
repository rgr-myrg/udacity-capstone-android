package net.usrlib.pocketbuddha.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import net.usrlib.pocketbuddha.data.DbHelper;

/**
 * Created by rgr-myrg on 6/8/16.
 */
public class SearchProvider extends ContentProvider {
	private static final UriMatcher sUriMatcher = buildUriMatcher();

	@Override
	public boolean onCreate() {
		return true;
	}

	@Nullable
	@Override
	public Cursor query(Uri uri,
						String[] projection,
						String selection,
						String[] selectionArgs,
						String sortOrder) {
		// content://net.usrlib.pocketbuddha.provider.search/47
		// content://net.usrlib.pocketbuddha.provider.search/search_suggest_query/term?limit=50

		Log.d("MAIN", "SEARCH PROVIDER Uri: " + uri);
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
			case SearchContract.TitleSearchEntry.SEARCH_SUGGEST_URI_TYPE:
				cursor = getTitleSearchListCursor(sqlite, uri);
		}

		return cursor;
//		final Cursor query = sqlite.query(
//				true,
//				FeedContract.ItemsEntry.TABLE_NAME_ITEMS,
//				new String[] {
//						BaseColumns._ID,
//						FeedContract.ItemsEntry.TITLE_COLUMN
//				},
//				FeedContract.ItemsEntry.TITLE_COLUMN + " LIKE ?",
//				new String[] {
//						"%"+ searchText + "%"
//				},
//				null, null, null, null
//		);
//
//		final MatrixCursor cursor = new MatrixCursor(
//				new String[] {
//						BaseColumns._ID,
//						SearchManager.SUGGEST_COLUMN_TEXT_1,
//						SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
//				}
//		);
//
//		if (query.moveToFirst()) {
//			do {
//				final int itemId = query.getInt(
//						query.getColumnIndex(BaseColumns._ID)
//				);
//				final String itemTitle = query.getString(
//						query.getColumnIndex(FeedContract.ItemsEntry.TITLE_COLUMN)
//				);
//				cursor.addRow(new Object[]{ itemId, itemTitle, itemId });
//			} while (query.moveToNext());
//		}
//
//		query.close();
//		return cursor;
	}

	private MatrixCursor getTitleSearchListCursor(final SQLiteDatabase sqlite, final Uri uri) {
		final String searchText = uri.getLastPathSegment().toUpperCase();
		final Cursor query = sqlite.query(
				true,
				SearchContract.TitleSearchEntry.TABLE_NAME_ITEMS,
				new String[] {
						BaseColumns._ID,
						FeedContract.ItemsEntry.TITLE_COLUMN
				},
				SearchContract.TitleSearchEntry.TITLE_COLUMN + " LIKE ?",
				new String[] {
						"%"+ searchText + "%"
				},
				null, null, null, null
		);

		final MatrixCursor cursor = new MatrixCursor(
				new String[] {
						BaseColumns._ID,
						SearchManager.SUGGEST_COLUMN_TEXT_1,
						SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
				}
		);

		if (query.moveToFirst()) {
			do {
				final int itemId = query.getInt(
						query.getColumnIndex(BaseColumns._ID)
				);
				final String itemTitle = query.getString(
						query.getColumnIndex(SearchContract.TitleSearchEntry.TITLE_COLUMN)
				);
				cursor.addRow(new Object[]{ itemId, itemTitle, itemId });
			} while (query.moveToNext());
		}

		query.close();
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

	public static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = SearchContract.CONTENT_AUTHORITY;

		matcher.addURI(
				authority,
				SearchContract.SEARCH_SUGGEST_PATH,
				SearchContract.TitleSearchEntry.SEARCH_SUGGEST_URI_TYPE
		);

		return  matcher;
	}
}
