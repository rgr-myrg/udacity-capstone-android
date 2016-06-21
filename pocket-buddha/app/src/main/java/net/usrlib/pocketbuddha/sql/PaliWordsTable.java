package net.usrlib.pocketbuddha.sql;

import android.provider.BaseColumns;

import net.usrlib.pocketbuddha.provider.FeedContract;
import net.usrlib.pocketbuddha.provider.WordContract;

/**
 * Created by rgr-myrg on 6/21/16.
 */
public class PaliWordsTable {
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ WordContract.WordEntry.TABLE_NAME
			+ "("
			+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ WordContract.WordEntry.WORD_ITEM_COLUMN + " TEXT NOT NULL,"
			+ WordContract.WordEntry.DESCRIPTION_COLUMN + " TEXT NOT NULL,"
			+ WordContract.WordEntry.FAVORITE_COLUMN  + " INTEGER NOT NULL,"
			+ WordContract.WordEntry.TIMESTAMP_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,"
			+ "UNIQUE (" + FeedContract.ItemsEntry.TITLE_COLUMN + ") ON CONFLICT REPLACE"
			+ ")";

	public static final String DROP_TABLE = "DROP TABLE " + WordContract.WordEntry.TABLE_NAME;
}
