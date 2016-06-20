package net.usrlib.pocketbuddha.data;

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
	public static final String TABLE_NAME = "feed_items";
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
			+ FeedContract.ItemsEntry.TIMESTAMP_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,"
			+ "UNIQUE (" + FeedContract.ItemsEntry.TITLE_COLUMN + ") ON CONFLICT REPLACE"
			+ ")";

	private static final String CREATE_TRIGGER = "CREATE TRIGGER IF NOT EXISTS timestamp_trigger "
			+ "AFTER UPDATE ON " + TABLE_NAME + " FOR EACH ROW "
			+ "BEGIN "
				+ "UPDATE " + TABLE_NAME
				+ " SET " + FeedContract.ItemsEntry.TIMESTAMP_COLUMN + " = CURRENT_TIMESTAMP"
				+ " WHERE " + ID_COLUMN + " = old." + ID_COLUMN + ";"
			+ " END";

	public static final String DROP_TABLE = "DROP TABLE " + TABLE_NAME;

	public static final String EXISTS = "SELECT name FROM sqlite_master WHERE type='table' AND name = ?";

	public static final String SELECT_CLAUSE = "SELECT * FROM " + TABLE_NAME;

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

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
		db.execSQL(CREATE_TRIGGER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}

	public static DbHelper getInstance(final Context context) {
		// Use application context, to prevent accidentally leaking an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null && context != null) {
			sInstance = new DbHelper(context.getApplicationContext());
		}

		return sInstance;
	}
}
