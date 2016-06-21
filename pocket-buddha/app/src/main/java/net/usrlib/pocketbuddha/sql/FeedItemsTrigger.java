package net.usrlib.pocketbuddha.sql;

import android.provider.BaseColumns;

import net.usrlib.pocketbuddha.provider.FeedContract;

/**
 * Created by rgr-myrg on 6/21/16.
 */
public class FeedItemsTrigger {
	public static final String TRIGGER_NAME = "timestamp_trigger";
	public static final String CREATE_TRIGGER = "CREATE TRIGGER IF NOT EXISTS " + TRIGGER_NAME
			+ " AFTER UPDATE ON " + FeedContract.ItemsEntry.TABLE_NAME_ITEMS + " FOR EACH ROW"
			+ " BEGIN"
			+ " UPDATE " + FeedContract.ItemsEntry.TABLE_NAME_ITEMS
			+ " SET " + FeedContract.ItemsEntry.TIMESTAMP_COLUMN + " = CURRENT_TIMESTAMP"
			+ " WHERE " + BaseColumns._ID + " = old." + BaseColumns._ID + ";"
			+ " END";

	public static final String DROP_TRIGGER = "DROP TRIGGER " + TRIGGER_NAME;
}
