package net.usrlib.pocketbuddha.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.usrlib.pocketbuddha.model.FeedItemDTO;
import net.usrlib.pocketbuddha.provider.FeedContract;

import java.util.List;

/**
 * Created by rgr-myrg on 5/26/16.
 */
public class DbHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "net.usrlib.android.pocketbuddha.data";
	public static final int DB_VERSION = 1;
	public static final String TABLE_NAME = "feed_items";
	public static final String TIMESTAMP_COLUMN = "timestamp";
	public static final String ID_COLUMN = "_id";

	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME
			+ "("
			+ ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ FeedContract.ItemsEntry.TITLE_COLUMN     + " TEXT NOT NULL,"
			+ FeedContract.ItemsEntry.PALI_COLUMN      + " TEXT NOT NULL,"
			+ FeedContract.ItemsEntry.ENGLISH_COLUMN   + " TEXT NOT NULL,"
			+ FeedContract.ItemsEntry.MP3_LINK_COLUMN  + " TEXT NOT NULL,"
			+ FeedContract.ItemsEntry.IMAGE_URL_COLUMN + " TEXT NOT NULL,"
			+ FeedContract.ItemsEntry.AUTHOR_COLUMN    + " TEXT,"
			+ FeedContract.ItemsEntry.SUBJECT_COLUMN   + " TEXT,"
			+ FeedContract.ItemsEntry.FAVORITE_COLUMN  + " INTEGER NOT NULL,"
			+ TIMESTAMP_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,"
			+ "UNIQUE (" + FeedItemDTO.TITLE_KEY + ") ON CONFLICT REPLACE"
			+ ")";

	public static final String DROP_TABLE = "DROP TABLE " + TABLE_NAME;

	public static final String EXISTS = "SELECT name FROM sqlite_master WHERE type='table' AND name = ?";

	public static final String SELECT_ALL_BY_DATE = "SELECT * FROM " + TABLE_NAME
			+ " ORDER BY " + TIMESTAMP_COLUMN;

	public static final String SELECT_ALL_BY_TITLE = "SELECT * FROM " + TABLE_NAME
			+ " ORDER BY " + FeedContract.ItemsEntry.TITLE_COLUMN;

	public static final String SELECT_FAVORITES_BY_TITLE = "SELECT * FROM " + TABLE_NAME
			+ " WHERE " + FeedContract.ItemsEntry.FAVORITE_COLUMN + " = 1"
			+ " ORDER BY " + FeedContract.ItemsEntry.TITLE_COLUMN;

	public static final String SELECT_FAVORITES_BY_DATE = "SELECT * FROM " + TABLE_NAME
			+ " WHERE " + FeedContract.ItemsEntry.FAVORITE_COLUMN + " = 1"
			+ " ORDER BY " + TIMESTAMP_COLUMN;

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

	public Cursor selectAll() {
		final SQLiteDatabase db = getWritableDatabase();

		if (db == null) {
			return null;
		}

		/*
		SELECT *
		FROM MyTable
		WHERE SomeColumn > LastValue
		ORDER BY SomeColumn
		LIMIT 100
		 */
		return db.rawQuery(SELECT_ALL_BY_TITLE, null);
	}

	public Cursor selectFavoritesByTitleAsc() {
		Log.d("DB", "selectFavoritesByTitleAsc");
		final SQLiteDatabase db = getWritableDatabase();

		if (db == null) {
			return null;
		}

		return db.rawQuery(SELECT_FAVORITES_BY_TITLE + ORDER_BY_ASC, null);
	}

	public Cursor selectFavoritesByTitleDesc() {
		Log.d("DB", "selectFavoritesByTitleDesc");
		final SQLiteDatabase db = getWritableDatabase();

		if (db == null) {
			return null;
		}

		return db.rawQuery(SELECT_FAVORITES_BY_TITLE + ORDER_BY_DESC, null);
	}

	public Cursor selectFavoritesByDateAsc() {
		Log.d("DB", "selectFavoritesByDateAsc");
		final SQLiteDatabase db = getWritableDatabase();

		if (db == null) {
			return null;
		}

		return db.rawQuery(SELECT_FAVORITES_BY_DATE + ORDER_BY_ASC, null);
	}

	public Cursor selectFavoritesByDateDesc() {
		Log.d("DB", "selectFavoritesByDateDesc");
		final SQLiteDatabase db = getWritableDatabase();

		if (db == null) {
			return null;
		}

		return db.rawQuery(SELECT_FAVORITES_BY_DATE + ORDER_BY_DESC, null);
	}

//	public void selectFavoritesBK(final OnSelectComplete callback, final boolean orderByTile) {
//		final SQLiteDatabase db = getWritableDatabase();
//
//		if (db == null) {
//			callback.onTransactionError();
//			return;
//		}
//
//		final Cursor cursor = db.rawQuery(
//				orderByTile
//						? SELECT_ALL_BY_TITLE + mTitleSortOrder
//						: SELECT_ALL_BY_DATE + mDateSortOrder,
//				null
//		);
//
//		if (cursor == null) {
//			callback.onTransactionError();
//			return;
//		}
//
//		final List<FeedItemDTO> arrayList = new ArrayList<>();
//
//		if (cursor.moveToFirst()) {
//			do {
//				final FeedItemDTO feedItemDTO = FeedItemDTO.fromDbCursor(cursor);
//				arrayList.add(feedItemDTO);
//			} while (cursor.moveToNext());
//		}
//
//		cursor.close();
//
//		if (arrayList.isEmpty() && arrayList.size() == 0) {
//			callback.onTransactionError();
//			return;
//		}
//
//		// Toggle sort order for the next look up.
//		if (orderByTile) {
//			mTitleSortOrder = mTitleSortOrder.equals(ORDER_BY_ASC) ? ORDER_BY_DESC : ORDER_BY_ASC;
//		} else {
//			mDateSortOrder = mDateSortOrder.equals(ORDER_BY_ASC) ? ORDER_BY_DESC : ORDER_BY_ASC;
//		}
//
//		callback.onTransactionSuccess(arrayList);
//	}

//	public void bulkInsertItems(final List<FeedItemDTO> items,
//	                            final OnTransactionComplete callback) {
//		final SQLiteDatabase db = getWritableDatabase();
//		long newRowId = -1;
//
//		if (db == null || items == null) {
//			callback.onError();
//			return;
//		}
//
//		db.beginTransaction();
//
//		try {
//			for (FeedItemDTO item : items) {
//				newRowId = db.insert(TABLE_NAME, null, item.toContentValues());
//			}
//
//			db.setTransactionSuccessful();
//		} finally {
//			db.endTransaction();
//		}
//
//		final boolean success = newRowId != -1;
//
//		if (success) {
//			callback.onSuccess(newRowId);
//		} else {
//			callback.onError();
//		}
//	}

	public boolean updateFavoriteColumn(final FeedItemDTO feedItemDTO,
	                                    final OnTransactionComplete callback) {
		final SQLiteDatabase db = getWritableDatabase();

		if (db == null || feedItemDTO == null) {
			callback.onError();
			return false;
		}

		db.beginTransaction();

		long rowId = -1;

		final ContentValues contentValues = new ContentValues();
		contentValues.put(
				FeedContract.ItemsEntry.FAVORITE_COLUMN,
				feedItemDTO.isFavorite()
		);

		try {
			rowId = db.update(
					TABLE_NAME,
					contentValues,
					FeedItemDTO.TITLE_KEY + " = ?",
					new String[] {
							feedItemDTO.getTitle()
					}
			);

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		final boolean success = rowId != -1;

		if (success) {
			callback.onSuccess(rowId);
		} else {
			callback.onError();
		}

		return success;
	}

//	public boolean removeFavorite(final FeedItemDTO feedItemDTO, final ViewTransaction callback) {
//		final SQLiteDatabase db = getWritableDatabase();
//
//		if (db == null || feedItemDTO == null) {
//			callback.onTransactionError();
//			return false;
//		}
//
//		int rowId = db.delete(
//				TABLE_NAME,
//				FeedItemDTO.TITLE_KEY + " = ?",
//				new String[]{
//						feedItemDTO.getTitle()
//				}
//		);
//
//		final boolean success = rowId != -1;
//
//		if (success) {
//			callback.onTransactionSuccess(rowId);
//		} else {
//			callback.onTransactionError();
//		}
//
//		return success;
//	}

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
		void onSuccess(List<FeedItemDTO> list);
		void onError();
	}
}
