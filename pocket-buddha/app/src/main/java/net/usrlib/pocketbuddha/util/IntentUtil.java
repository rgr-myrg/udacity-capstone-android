package net.usrlib.pocketbuddha.util;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;

import net.usrlib.pocketbuddha.R;

/**
 * Created by rgr-myrg on 5/16/16.
 */
public class IntentUtil {
	public static void startWithChooser(
			final AppCompatActivity app,
			final String title,
			final String text) {

		if (app == null || title == null || text == null) {
			return;
		}

		String footer = app.getString(R.string.share_footer_text);

		if (footer == null) {
			footer = "";
		}

		app.startActivity(
				Intent.createChooser(
						ShareCompat.IntentBuilder.from(app)
								.setType("text/plain")
								.setText(title + text + "\n" + footer)
								.getIntent(),
						title
				)
		);
	}
}
