package net.usrlib.pocketbuddha.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by rgr-myrg on 6/17/16.
 */
public class Preferences {
	public static final String DATA_INSTALL_KEY = "dataInstall";
	public static final String DICTIONARY_INSTALL_KEY = "dictionaryInstall";
	public static final String CURRENT_WORD_ID_KEY = "currentWordItemId";


	public static final void setHasDataInstall(final Context context, final boolean value) {
		setBoolean(context, DATA_INSTALL_KEY, value);
	}

	public static final void setHasDictionaryInstall(final Context context, final boolean value) {
		setBoolean(context, DICTIONARY_INSTALL_KEY, value);
	}

	public static final void setCurrentWordItemId(final Context context, final int value) {
		setInt(context, CURRENT_WORD_ID_KEY, value);
	}

	public static final boolean hasDataInstall(final Context context) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		return preferences.getBoolean(DATA_INSTALL_KEY, false);
	}

	public static final boolean hasDictionaryInstall(final Context context) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		return preferences.getBoolean(DICTIONARY_INSTALL_KEY, false);
	}

	public static final int getCurrentWordItemId(final Context context) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		return preferences.getInt(CURRENT_WORD_ID_KEY, 0);
	}

	public static final void setBoolean(final Context context,
										 final String key,
										 final boolean value) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = preferences.edit();

		editor.putBoolean(key, value);
		editor.commit();
	}

	public static final void setInt(final Context context,
	                                final String key,
	                                final int value) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		final SharedPreferences.Editor editor = preferences.edit();

		editor.putInt(key, value);
		editor.commit();
	}
}
