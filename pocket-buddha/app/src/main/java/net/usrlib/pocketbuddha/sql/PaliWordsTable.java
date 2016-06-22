package net.usrlib.pocketbuddha.sql;

import android.provider.BaseColumns;

import net.usrlib.pocketbuddha.provider.WordContract;

/**
 * Created by rgr-myrg on 6/21/16.
 */
public class PaliWordsTable {
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ WordContract.DictionaryEntry.TABLE_NAME
			+ "("
			+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ WordContract.DictionaryEntry.PALI_NAME_COLUMN  + " TEXT NOT NULL, "
			+ WordContract.DictionaryEntry.PALI_TERM_COLUMN  + " TEXT NOT NULL, "
			+ WordContract.DictionaryEntry.DEFINITION_COLUMN + " TEXT NOT NULL, "
			+ WordContract.DictionaryEntry.FAVORITE_COLUMN   + " INTEGER NOT NULL,"
			+ WordContract.DictionaryEntry.TIMESTAMP_COLUMN  + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, "
			+ "UNIQUE (" + WordContract.DictionaryEntry.PALI_NAME_COLUMN + ") ON CONFLICT REPLACE"
			+ ")";

	public static final String DROP_TABLE = "DROP TABLE " + WordContract.DictionaryEntry.TABLE_NAME;
}
