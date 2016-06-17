package net.usrlib.pocketbuddha.view;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;

/**
 * Created by rgr-myrg on 6/7/16.
 */
public class HomeActivity extends BaseActivity implements MvpView {
	private RecyclerView mRecyclerView = null;
	private HomeAdapter mRecyclerAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initContentView(R.layout.home_activity);
		MvpPresenter.getInstance().requestItemsFromDb(this);
	}

	protected void initRecyclerViewAndAdapter(final Cursor cursor) {
		mRecyclerAdapter = new HomeAdapter(this, cursor);
		mRecyclerView = (RecyclerView) findViewById(R.id.home_recycler_view_list);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(mRecyclerAdapter);
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
	}

	@Override
	public void onTransactionError(MvpPresenter.TransactionType type) {
	}
}