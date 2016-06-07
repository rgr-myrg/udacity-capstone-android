package net.usrlib.pocketbuddha.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.usrlib.pocketbuddha.model.FeedItemDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgr-myrg on 5/26/16.
 */
public class DbHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "net.usrlib.android.pocketbuddha.data";
	public static final int DB_VERSION = 1;
	public static final String TABLE_NAME = "feed_items";
	public static final String TIMESTAMP_COLUMN = "timestamp";
	public static final String PRIMARY_ID_KEY = "_id";
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ "("
			+ PRIMARY_ID_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ FeedItemDTO.TITLE_KEY + " TEXT NOT NULL,"
			+ FeedItemDTO.PALI_KEY + " TEXT NOT NULL,"
			+ FeedItemDTO.ENGLISH_KEY + " TEXT NOT NULL,"
			+ FeedItemDTO.MP3_LINK_KEY + " TEXT NOT NULL,"
			+ FeedItemDTO.IMAGE_URL_KEY + " TEXT NOT NULL,"
			+ FeedItemDTO.AUTHOR_KEY + " TEXT,"
			+ FeedItemDTO.SUBJECT_KEY + " TEXT,"
			+ FeedItemDTO.FAVORITE_KEY + " INTEGER NOT NULL,"
			+ TIMESTAMP_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,"
			+ "UNIQUE (" + FeedItemDTO.TITLE_KEY + ") ON CONFLICT REPLACE"
			+ ")";
	public static final String DROP_TABLE = "DROP TABLE " + TABLE_NAME;
	public static final String SELECT_ALL_BY_DATE = "SELECT * FROM " + TABLE_NAME
			+ " ORDER BY " + TIMESTAMP_COLUMN;

	public static final String SELECT_ALL_BY_TITLE = "SELECT * FROM " + TABLE_NAME
			+ " ORDER BY " + FeedItemDTO.TITLE_KEY;

	public static final String ORDER_BY_ASC  = " ASC";
	public static final String ORDER_BY_DESC = " DESC";

	private static DbHelper sInstance = null;

	private String mTitleSortOrder = ORDER_BY_ASC;
	private String mDateSortOrder  = ORDER_BY_DESC;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}

	public void selectFavorites(final OnSelectComplete callback, final boolean orderByTile) {
		final SQLiteDatabase db = getWritableDatabase();

		if (db == null) {
			callback.onError();
			return;
		}

		final Cursor cursor = db.rawQuery(
				orderByTile
						? SELECT_ALL_BY_TITLE + mTitleSortOrder
						: SELECT_ALL_BY_DATE + mDateSortOrder,
				null
		);

		if (cursor == null) {
			callback.onError();
			return;
		}

		final List<FeedItemDTO> arrayList = new ArrayList<>();

		if (cursor.moveToFirst()) {
			do {
				final FeedItemDTO feedItemDTO = FeedItemDTO.fromDbCursor(cursor);
				arrayList.add(feedItemDTO);
			} while (cursor.moveToNext());
		}

		cursor.close();

		if (arrayList.isEmpty() && arrayList.size() == 0) {
			callback.onError();
			return;
		}

		// Toggle sort order for the next look up.
		if (orderByTile) {
			mTitleSortOrder = mTitleSortOrder.equals(ORDER_BY_ASC) ? ORDER_BY_DESC : ORDER_BY_ASC;
		} else {
			mDateSortOrder = mDateSortOrder.equals(ORDER_BY_ASC) ? ORDER_BY_DESC : ORDER_BY_ASC;
		}

		callback.onSuccess(arrayList);
	}

	public void bulkInsertItems(final List<FeedItemDTO> items, final OnTransactionComplete callback) {
		final SQLiteDatabase db = getWritableDatabase();
		long newRowId = -1;

		if (db == null || items == null) {
			callback.onError();
			return;
		}

		db.beginTransaction();

		try {
			for (FeedItemDTO item : items) {
				newRowId = db.insert(TABLE_NAME, null, item.toContentValues());
			}

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		final boolean success = newRowId != -1;

		if (success) {
			callback.onSuccess(newRowId);
		} else {
			callback.onError();
		}
	}

	public boolean addFavorite(final FeedItemDTO feedItemDTO, final OnTransactionComplete callback) {
		final SQLiteDatabase db = getWritableDatabase();

		if (db == null || feedItemDTO == null) {
			callback.onError();
			return false;
		}

		db.beginTransaction();

		long newRowId = -1;

		try {
			newRowId = db.insert(TABLE_NAME, null, feedItemDTO.toContentValues());
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		final boolean success = newRowId != -1;

		if (success) {
			callback.onSuccess(newRowId);
		} else {
			callback.onError();
		}

		return success;
	}

	public boolean removeFavorite(final FeedItemDTO feedItemDTO, final OnTransactionComplete callback) {
		final SQLiteDatabase db = getWritableDatabase();

		if (db == null || feedItemDTO == null) {
			callback.onError();
			return false;
		}

		int rowId = db.delete(
				TABLE_NAME,
				FeedItemDTO.TITLE_KEY + " = ?",
				new String[]{
						feedItemDTO.getTitle()
				}
		);

		final boolean success = rowId != -1;

		if (success) {
			callback.onSuccess(rowId);
		} else {
			callback.onError();
		}

		return success;
	}

	public static DbHelper getInstance(final Context context) {
		// Use application context, to prevent accidentally leaking an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null && context != null) {
			sInstance = new DbHelper(context.getApplicationContext());
		}

		return sInstance;
	}

	public interface OnTransactionComplete {
		void onSuccess(Object data);
		void onError();
	}

	public interface OnSelectComplete {
		void onSuccess(List<
				FeedItemDTO> list);
		void onError();
	}
}
