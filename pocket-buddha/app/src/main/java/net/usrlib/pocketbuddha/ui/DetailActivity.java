package net.usrlib.pocketbuddha.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;
import net.usrlib.pocketbuddha.util.TrackerUtil;

/**
 * Created by rgr-myrg on 6/7/16.
 */
public class DetailActivity extends BaseActivity implements MvpView {
	public static final String NAME = DetailActivity.class.getSimpleName();

	protected DetailAdapter mPagerAdapter = null;
	protected ViewPager mViewPager = null;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_detail_activity);

		final Intent intent = getIntent();

		if (intent == null) {
			return;
		}

		mRootView = findViewById(android.R.id.content);

		mAdapterPosition = savedInstanceState == null
				? intent.getIntExtra(MvpModel.POSITION_KEY, 0)
				: savedInstanceState.getInt(MvpModel.POSITION_KEY);

		if (savedInstanceState == null) {
			Log.d("DETAIL", "is null");
		} else {
			Log.d("DETAIL", "NOT null");
		}

		Log.d("DETAIL", "mAdapterPosition: " + mAdapterPosition);
		final String action = intent.getAction();

		if (action == null) {
			MvpPresenter.getInstance().requestItemsFromDb(this);
			TrackerUtil.trackScreen(getApplication(), NAME, HomeActivity.NAME);
			return;
		}

		// Determine which cursor results to request based on Intent Action
		if (action.equals(Intent.ACTION_SEARCH)) {
			startSearchResultsActivity(intent);
			TrackerUtil.trackScreen(getApplication(), NAME, Intent.ACTION_SEARCH);

		} else if (action.equals(Intent.ACTION_VIEW)) {
			requestTitleSearchResults(intent);
			TrackerUtil.trackScreen(getApplication(), NAME, Intent.ACTION_VIEW);

		} else if (action.equals(SearchResultActivity.ACTION)) {
			requestTextSearchResults(intent);
			TrackerUtil.trackScreen(getApplication(), NAME, SearchResultActivity.ACTION);

		} else if (action.equals(FavoritesActivity.ACTION)) {
			requestFavorites();
			TrackerUtil.trackScreen(getApplication(), NAME, FavoritesActivity.ACTION);

		} else {
			MvpPresenter.getInstance().requestItemsFromDb(this);
			TrackerUtil.trackScreen(getApplication(), NAME, HomeActivity.NAME);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState == null || mViewPager == null) {
			return;
		}
		outState.putInt(MvpModel.POSITION_KEY, mViewPager.getCurrentItem());
	}

	// Loader Helper Callbacks
	@Override
	public void onTransactionProgress(MvpPresenter.TransactionType type) {
	}

	@Override
	public void onTransactionSuccess(MvpPresenter.TransactionType type, Bundle data) {
		if (type != MvpPresenter.TransactionType.DB_UPDATE) {
			return;
		}

		// Keep track of current position
		if (mViewPager != null) {
			mAdapterPosition = mViewPager.getCurrentItem();
		}

		final int msgId = mData.isFavorite()
				? R.string.msg_favorite_success
				: R.string.msg_favorite_removed;

		displayMessage(msgId);

		//TODO: Get last transaction ??? Figure out if view should be updated. Ex: Came from search result
		// Refresh this view's data for DB_UPDATE transactions
		Log.d("DETAIL", "invking requestItemsFromDb");
		MvpPresenter.getInstance().requestItemsFromDb(this);

		// Data has changed. Restart the loader to do a new query?
		// getLoaderManager().restartLoader(MvpModel.DB_QUERY_LOADER_ID, null, null);
	}

	@Override
	public void onTransactionError(MvpPresenter.TransactionType type) {
		displayMessage(R.string.msg_db_error);
	}

	@Override
	public void onTransactionCursorReady(Cursor cursor) {
		Log.d("DETAIL", "onTransactionCursorReady");
		mCursor = cursor;
		mData = getItem(mAdapterPosition);

		initViewPager(mAdapterPosition, cursor);
	}

	@Override
	public void requestTransaction(Bundle data) {
	}

	private void initViewPager(final int position, final Cursor cursor) {
		Log.d("DETAIL", "initViewPager");

		if (mPagerAdapter != null && mViewPager != null) {
			Log.d("DETAIL", "initViewPager invoking changeCursor");
			mPagerAdapter.changeCursor(cursor);
			mViewPager.setCurrentItem(position);

			return;
		}

		mPagerAdapter = new DetailAdapter(getSupportFragmentManager(), cursor);

		mViewPager = (ViewPager) findViewById(R.id.home_detail_viewpager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setCurrentItem(position);

		mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mData = getItem(position);
			}
		});
	}

	private void requestTitleSearchResults(final Intent intent) {
		final Uri uri = Uri.parse(intent.getDataString());

		MvpPresenter.getInstance().requestTitleSearch(this, uri);
	}

	private void requestTextSearchResults(final Intent intent) {
		final Bundle bundle = intent.getExtras();

		if (bundle == null) {
			onTransactionError(MvpPresenter.TransactionType.DB_QUERY);
		}

		final String query = bundle.getString(SearchManager.QUERY);
		mAdapterPosition = bundle.getInt(SearchResultAdapter.ITEM_POSITION_KEY, 0);

		MvpPresenter.getInstance().requestTextSearch(this, query);
	}

	private void requestFavorites() {
		final Uri previousUri = MvpPresenter.getInstance().getLastDbQueryUri();

		// Retain previous query if available
		if (previousUri != null) {
			MvpPresenter.getInstance().requestLoaderManagerForDbQuery(this, previousUri);
		} else {
			MvpPresenter.getInstance().requestFavoritesSortByDateDesc(this);
		}
	}

	private void startSearchResultsActivity(final Intent intent) {
		intent.setClass(getApplicationContext(), SearchResultActivity.class);
		startActivity(intent);
	}
}
