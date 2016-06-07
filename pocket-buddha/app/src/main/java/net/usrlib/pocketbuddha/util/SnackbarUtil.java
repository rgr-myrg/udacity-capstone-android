package net.usrlib.pocketbuddha.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by rgr-myrg on 5/20/16.
 */
public final class SnackbarUtil {
	public static final void showMessage(final View view, final String message) {
		Snackbar.make(
				view,
				message,
				Snackbar.LENGTH_LONG
		).setAction("Action", null).show();
	}
}
