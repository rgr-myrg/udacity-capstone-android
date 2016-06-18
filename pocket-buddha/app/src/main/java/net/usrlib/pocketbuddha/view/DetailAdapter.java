package net.usrlib.pocketbuddha.view;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.usrlib.pocketbuddha.mvp.MvpModel;

/**
 * Created by rgr-myrg on 6/8/16.
 */
public class DetailAdapter extends FragmentStatePagerAdapter {
	private Cursor mCursor = null;

	public DetailAdapter(final FragmentManager fragmentManager, final Cursor cursor) {
		super(fragmentManager);
		this.mCursor = cursor;
	}

	@Override
	public Fragment getItem(int position) {
		mCursor.moveToPosition(position);
		return DetailFragment.newInstance(MvpModel.fromDbCursor(mCursor));
	}

	@Override
	public int getCount() {
		return mCursor == null ? 0 : mCursor.getCount();
	}

	public void changeCursor(Cursor cursor) {
		Cursor swappedCursor = swapCursor(cursor);

		if (swappedCursor != null) {
			swappedCursor.close();
		}
	}

	private Cursor swapCursor(Cursor cursor) {
		if (mCursor == cursor) {
			return null;
		}

		final Cursor previousCursor = mCursor;

		mCursor = cursor;

		if (cursor != null) {
			this.notifyDataSetChanged();
		}

		return previousCursor;
	}
}
