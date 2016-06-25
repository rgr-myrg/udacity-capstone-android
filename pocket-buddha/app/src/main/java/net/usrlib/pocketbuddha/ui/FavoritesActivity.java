package net.usrlib.pocketbuddha.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;

public class FavoritesActivity extends BaseActivity implements MvpView {
	public static final String ACTION = FavoritesActivity.class.getSimpleName();

	private FavoritesAdapter mRecyclerAdapter = null;
	private RecyclerView mRecyclerView = null;

	private boolean hasSortTitleAsc = true;
	private boolean hasSortDateAsc  = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initContentView(R.layout.favorites_activity);

		MvpPresenter.getInstance().requestFavoritesSortByDateDesc(this);
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

		if (hasSortTitleAsc) {
			MvpPresenter.getInstance().requestFavoritesSortByTitleDesc(this);
		} else {
			MvpPresenter.getInstance().requestFavoritesSortByTitleAsc(this);
		}
	}

	public void onSortByDateClicked(View view) {
		closeFloatingActionMenu(view);
		hasSortDateAsc = !hasSortDateAsc;

		if (hasSortDateAsc) {
			MvpPresenter.getInstance().requestFavoritesSortByDateDesc(this);
		} else {
			MvpPresenter.getInstance().requestFavoritesSortByDateAsc(this);
		}
	}

	private void closeFloatingActionMenu(View view) {
		if (view == null) {
			return;
		}

		final FloatingActionMenu menu = (FloatingActionMenu) view.getParent();

		if (menu == null) {
			return;
		}

		menu.close(true);
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
}
