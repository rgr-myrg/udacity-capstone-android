package net.usrlib.pocketbuddha.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.usrlib.pocketbuddha.provider.FeedContract;

/**
 * Created by rgr-myrg on 5/26/16.
 */
public class DbHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "net.usrlib.android.pocketbuddha.data";
	public static final int DB_VERSION = 1;

	public static final String SELECT_CLAUSE = "SELECT * FROM "
			+ FeedContract.ItemsEntry.TABLE_NAME_ITEMS;

	public static final String SELECT_ALL_BY_TITLE = SELECT_CLAUSE
			+ " ORDER BY " + FeedContract.ItemsEntry.TITLE_COLUMN;

	public static final String SELECT_FAVORITES_BY_TITLE = SELECT_CLAUSE
			+ " WHERE " + FeedContract.ItemsEntry.FAVORITE_COLUMN + " = 1"
			+ " ORDER BY " + FeedContract.ItemsEntry.TITLE_COLUMN;

	public static final String SELECT_FAVORITES_BY_DATE = SELECT_CLAUSE
			+ " WHERE " + FeedContract.ItemsEntry.FAVORITE_COLUMN + " = 1"
			+ " ORDER BY " + FeedContract.ItemsEntry.TIMESTAMP_COLUMN;

	public static final String ORDER_BY_ASC  = " ASC";
	public static final String ORDER_BY_DESC = " DESC";

	private static DbHelper sInstance = null;

	private DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(FeedItemsTable.CREATE_TABLE);
		db.execSQL(PaliWordsTable.CREATE_TABLE);
		db.execSQL(NotesTable.CREATE_TABLE);
		db.execSQL(FeedItemsTrigger.CREATE_TRIGGER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(FeedItemsTable.DROP_TABLE);
		db.execSQL(PaliWordsTable.DROP_TABLE);
		db.execSQL(NotesTable.DROP_TABLE);
		db.execSQL(FeedItemsTrigger.DROP_TRIGGER);
		onCreate(db);
	}

	public static synchronized DbHelper getInstance(final Context context) {
		// Use application context, to prevent accidentally leaking an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null && context != null) {
			sInstance = new DbHelper(context.getApplicationContext());
		}

		return sInstance;
	}
}
