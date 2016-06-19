package net.usrlib.pocketbuddha.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import net.usrlib.pocketbuddha.data.DbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgr-myrg on 6/8/16.
 */
public class SearchProvider extends ContentProvider {
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
		// content://net.usrlib.pocketbuddha.provider.search/search_suggest_query/test?limit=50:null

		final String searchText = uri.getLastPathSegment().toUpperCase();
		final int searchLimit = Integer.parseInt(
				uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT)
		);

		final DbHelper db = DbHelper.getInstance(getContext());

		if (db == null) {
			return null;
		}

		final SQLiteDatabase sqlite = db.getReadableDatabase();

		if (sqlite == null) {
			return null;
		}

		final Cursor query = sqlite.query(
				true,
				FeedContract.ItemsEntry.TABLE_NAME_ITEMS,
				new String[] {FeedContract.ItemsEntry.TITLE_COLUMN},
				FeedContract.ItemsEntry.TITLE_COLUMN + " LIKE ?",
				new String[] {"%"+ searchText + "%" },
				null, null, null, null
		);

		final List<String> results = new ArrayList<>();

		if (query.moveToFirst()) {
			do {
				results.add(
						query.getString(
								query.getColumnIndex(FeedContract.ItemsEntry.TITLE_COLUMN)
						)
				);
			} while (query.moveToNext());
		}

		query.close();

		final MatrixCursor cursor = new MatrixCursor(
				new String[] {
						BaseColumns._ID,
						SearchManager.SUGGEST_COLUMN_TEXT_1,
						SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
				}
		);

		final int length = results.size();

		for (int i = 0; i < length && cursor.getCount() < searchLimit; i++) {
			String item = results.get(i);

			//if (item.toUpperCase().contains(query)) {
				cursor.addRow(new Object[]{ i, item, i });
			//}
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
}
