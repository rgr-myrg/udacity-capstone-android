package net.usrlib.pocketbuddha.view;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;

/**
 * Created by rgr-myrg on 6/2/16.
 */
public class SearchResultActivity extends BaseActivity implements MvpView {
	private RecyclerView mRecyclerView = null;
	private SearchResultAdapter mRecyclerAdapter = null;

	private String mSearchQuery = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initContentView(R.layout.search_results_activity);

		final Intent intent = getIntent();

		if (intent == null) {
			return;
		}

		mSearchQuery = intent.getStringExtra(SearchManager.QUERY);

		if (mSearchQuery == null) {
			return;
		}

		MvpPresenter.getInstance().requestTextSearch(this, mSearchQuery);
	}

	protected void initRecyclerViewAndAdapter(final Cursor cursor) {
		mRecyclerAdapter = new SearchResultAdapter(this, cursor, mSearchQuery);
		mRecyclerView = (RecyclerView) findViewById(R.id.search_results_recycler_view);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setAdapter(mRecyclerAdapter);
	}

	@Override
	public void onTransactionProgress(MvpPresenter.TransactionType type) {
	}

	@Override
	public void onTransactionSuccess(MvpPresenter.TransactionType type, Bundle data) {
		Log.d("MAIN", "SEARCH ACTIVITY onTransactionSuccess");
	}

	@Override
	public void onTransactionError(MvpPresenter.TransactionType type) {
		Log.d("MAIN", "SEARCH ACTIVITY onTransactionError");
	}

	@Override
	public void onTransactionCursorReady(Cursor cursor) {
		initRecyclerViewAndAdapter(cursor);
	}

	@Override
	public void requestTransaction(Bundle data) {
	}
}
