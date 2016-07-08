package net.usrlib.pocketbuddha.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;

/**
 * Created by rgr-myrg on 6/7/16.
 */
public class HomeActivity extends BaseActivity implements MvpView {
	public static final String NAME = HomeActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initContentView(R.layout.home_activity);

		if (savedInstanceState == null) {
			MvpPresenter.getInstance().requestItemsFromDb(this);
		}
	}

	@Override
	public void onTransactionCursorReady(Cursor cursor) {
		final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_fragment);
		mCursor = cursor;

		// Notify the fragment the cursor is ready.
		if (fragment != null) {
			((BaseFragment) fragment).onCursorReady(cursor);
		}
	}

	@Override
	public void requestTransaction(Bundle bundle) {
	}

	@Override
	public void onTransactionProgress(MvpPresenter.TransactionType type) {
	}

	@Override
	public void onTransactionSuccess(MvpPresenter.TransactionType type, Bundle data) {
		// Special case for table master layout only.
		// Otherwise, this is handled in DetailActivity
		if (!isTablet()) {
			return;
		}

		if (type == MvpPresenter.TransactionType.DB_UPDATE) {
			final int msgId = mData.isFavorite()
					? R.string.msg_favorite_success
					: R.string.msg_favorite_removed;

			displayMessage(msgId);

			// Refresh Home Cursor
			MvpPresenter.getInstance().requestItemsFromDb(this);
		}
	}

	@Override
	public void onTransactionError(MvpPresenter.TransactionType type) {
		displayMessage(R.string.msg_db_error);
	}
}