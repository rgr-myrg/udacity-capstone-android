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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initContentView(R.layout.home_activity);
		MvpPresenter.getInstance().requestItemsFromDb(this);
	}

	@Override
	public void onTransactionCursorReady(Cursor cursor) {
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.home_fragment);

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
	}

	@Override
	public void onTransactionError(MvpPresenter.TransactionType type) {
	}
}