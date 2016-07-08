package net.usrlib.pocketbuddha.sql;

import android.provider.BaseColumns;

import net.usrlib.pocketbuddha.provider.FeedContract;

/**
 * Created by rgr-myrg on 7/7/16.
 */
public class NotesTable {
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ FeedContract.NotesEntry.TABLE_NAME
			+ "("
			+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ FeedContract.NotesEntry.ITEM_ID_COLUMN + " INTEGER NOT NULL,"
			+ FeedContract.NotesEntry.COMMENT_COLUMN + " TEXT NOT NULL,"
			+ FeedContract.NotesEntry.LIKES_COLUMN   + " INTEGER DEFAULT 0,"
			+ FeedContract.NotesEntry.DELETED_COLUMN + " INTEGER DEFAULT 0,"
			+ FeedContract.NotesEntry.TIMESTAMP_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,"
			+ "FOREIGN KEY(" + FeedContract.NotesEntry.ITEM_ID_COLUMN + ") "
			+ "REFERENCES " + FeedContract.ItemsEntry.TABLE_NAME_ITEMS + "(id),"
			+ "UNIQUE (" + FeedContract.NotesEntry.COMMENT_COLUMN + ") ON CONFLICT REPLACE"
			+ ")";

	public static final String DROP_TABLE = "DROP TABLE " + FeedContract.NotesEntry.TABLE_NAME;
}
