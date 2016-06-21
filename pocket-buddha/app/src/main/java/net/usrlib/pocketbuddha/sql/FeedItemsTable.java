package net.usrlib.pocketbuddha.sql;

import android.provider.BaseColumns;

import net.usrlib.pocketbuddha.provider.FeedContract;

/**
 * Created by rgr-myrg on 6/21/16.
 */
public class FeedItemsTable {
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ FeedContract.ItemsEntry.TABLE_NAME_ITEMS
			+ "("
			+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
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

	public static final String DROP_TABLE = "DROP TABLE " + FeedContract.ItemsEntry.TABLE_NAME_ITEMS;
}
