package net.usrlib.pocketbuddha.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;

public class FavoritesActivity extends BaseActivity implements MvpView {
	public static final String ACTION = FavoritesActivity.class.getSimpleName();

	public static final String SORT_BY_KEY = "sortBy";
	public static final String TITLE_SORT_KEY = "titleSortDirection";
	public static final String DATE_SORT_KEY  = "dateSortDirection";

	public static final int SORT_BY_DATE  = 1;
	public static final int SORT_BY_TITLE = 2;

	private FavoritesAdapter mRecyclerAdapter = null;
	private RecyclerView mRecyclerView = null;

	private boolean hasSortTitleAsc = true;
	private boolean hasSortDateAsc  = true;

	private int currentSortType = SORT_BY_DATE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initContentView(R.layout.favorites_activity);

		if (savedInstanceState == null) {
			MvpPresenter.getInstance().requestFavoritesSortByDateDesc(this);
		} else {
			restoreFromBundle(savedInstanceState);
		}
	}

	@Override
	protected void onRestart() {
		Log.i("FavoritesActivity", "onRestart: " + MvpPresenter.getInstance().getLastDbQueryUri());
		super.onRestart();

		// Request the previous query on restart when navigating back from detail view
		MvpPresenter.getInstance().requestLoaderManagerForDbQuery(
				this,
				MvpPresenter.getInstance().getLastDbQueryUri()
		);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i("FavoritesActivity", "onSaveInstanceState");
		super.onSaveInstanceState(outState);

		outState.putInt(SORT_BY_KEY, currentSortType);
		outState.putBoolean(TITLE_SORT_KEY, !hasSortTitleAsc);
		outState.putBoolean(DATE_SORT_KEY, !hasSortDateAsc);
	}

	@Override
	public void onTransactionCursorReady(Cursor cursor) {
		initRecyclerViewAndAdapter(cursor);
	}

	@Override
	public void onTransactionProgress(MvpPresenter.TransactionType type) {

	}

	@Override
	public void onTransactionSuccess(MvpPresenter.TransactionType type, Bundle data) {
		switch (type) {
			case DB_UPDATE:
				final Uri previousUri = MvpPresenter.getInstance().getLastDbQueryUri();

				// Retain previous query if available
				if (previousUri != null) {
					MvpPresenter.getInstance().requestLoaderManagerForDbQuery(this, previousUri);
				} else {
					MvpPresenter.getInstance().requestFavoritesSortByDateDesc(this);
				}

				break;
		}
	}

	@Override
	public void onTransactionError(MvpPresenter.TransactionType type) {
		Log.w(ACTION, "onTransactionError for type: " + type.toString());
	}

	@Override
	public void requestTransaction(Bundle data) {
		if (data.containsKey(MvpPresenter.TRANSACTION_TYPE_KEY)) {
			final String transactionType = data.getString(MvpPresenter.TRANSACTION_TYPE_KEY);

			if (transactionType.equals(MvpPresenter.TransactionType.DB_UPDATE.toString())) {
				MvpPresenter.getInstance().requestItemUpdate(
						this,
						(MvpModel) data.getParcelable(MvpModel.NAME)
				);
			}
		}
	}

	public void onSortByTitleClicked(View view) {
		closeFloatingActionMenu(view);

		hasSortTitleAsc = !hasSortTitleAsc;
		currentSortType = SORT_BY_TITLE;

		if (hasSortTitleAsc) {
			MvpPresenter.getInstance().requestFavoritesSortByTitleDesc(this);
		} else {
			MvpPresenter.getInstance().requestFavoritesSortByTitleAsc(this);
		}
	}

	public void onSortByDateClicked(View view) {
		closeFloatingActionMenu(view);

		hasSortDateAsc = !hasSortDateAsc;
		currentSortType = SORT_BY_DATE;

		if (hasSortDateAsc) {
			MvpPresenter.getInstance().requestFavoritesSortByDateDesc(this);
		} else {
			MvpPresenter.getInstance().requestFavoritesSortByDateAsc(this);
		}
	}

	private void initRecyclerViewAndAdapter(final Cursor cursor) {
		if (mRecyclerAdapter != null) {
			mRecyclerAdapter.changeCursor(cursor);
			return;
		}

		mRecyclerAdapter = new FavoritesAdapter(this, cursor);
		mRecyclerView = (RecyclerView) findViewById(R.id.favorites_list_recycler_view);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setAdapter(mRecyclerAdapter);
	}

	private void restoreFromBundle(final Bundle bundle) {
		currentSortType = bundle.getInt(SORT_BY_KEY, SORT_BY_DATE);
		hasSortTitleAsc = bundle.getBoolean(TITLE_SORT_KEY, hasSortTitleAsc);
		hasSortDateAsc  = bundle.getBoolean(DATE_SORT_KEY, hasSortDateAsc);

		switch (currentSortType) {
			case SORT_BY_DATE:
				onSortByDateClicked(null);
				break;
			case SORT_BY_TITLE:
				onSortByTitleClicked(null);
				break;
		}
	}
}
