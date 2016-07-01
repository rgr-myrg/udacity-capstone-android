package net.usrlib.pocketbuddha.ui;

import android.database.Cursor;
import android.support.v4.app.FragmentActivity;

/**
 * Created by rgr-myrg on 6/30/16.
 */
public interface BaseFragment {
	public FragmentActivity getFragmentActivity();
	public void onCursorReady(Cursor cursor);
	public boolean isTablet();
}
