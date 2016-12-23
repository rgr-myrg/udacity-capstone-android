package net.usrlib.pocketbuddha.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rgr-myrg on 12/23/16.
 */

public class JsonLoader {
	public static final String PALI_CANNON_JS = "pali_cannon.js";
	public static final String PALI_TERMS_JS  = "pali_terms.js";

	public static final String loadJSONFromAsset(final Context context, final String jsonFileName) {
		String jsonString = null;

		try {
			final AssetManager manager = context.getAssets();
			final InputStream inputStream = manager.open(jsonFileName);

			final int size = inputStream.available();
			final byte[] buffer = new byte[size];

			inputStream.read(buffer);
			inputStream.close();

			jsonString = new String(buffer, "UTF-8");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}

		return jsonString;
	}
}
