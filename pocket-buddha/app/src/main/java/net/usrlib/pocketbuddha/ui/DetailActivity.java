package net.usrlib.pocketbuddha.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

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
	public static final String LAST_INTENT_KEY = "lastIntent";

	private DetailAdapter mPagerAdapter = null;
	private ViewPager mViewPager = null;
	private Intent mLastIntent = null;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_detail_activity);

		mRootView = findViewById(android.R.id.content);

		if (savedInstanceState == null) {
			mLastIntent = getIntent();
			mAdapterPosition = mLastIntent.getIntExtra(MvpModel.POSITION_KEY, 0);
		} else {
			mLastIntent = savedInstanceState.getParcelable(LAST_INTENT_KEY);
			mAdapterPosition = savedInstanceState.getInt(MvpModel.POSITION_KEY);
		}

		requestResults(mLastIntent);
	}

	private void requestResults(final Intent intent) {
		String action = intent.getAction();
		if (action == null) {
			action = NAME;
		}

		Log.d(NAME, "action: " + action);

		if (action.equals(Intent.ACTION_SEARCH)) {
			startSearchResultsActivity(intent);
		} else if (action.equals(Intent.ACTION_VIEW)) {
			requestTitleSearchResults(intent);
		} else if (action.equals(SearchResultActivity.ACTION)) {
			requestTextSearchResults(intent);
		} else if (action.equals(FavoritesActivity.ACTION)) {
			requestFavorites();
		} else {
			MvpPresenter.getInstance().requestItemsFromDb(this);
		}

		TrackerUtil.trackScreen(getApplication(), NAME, action);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (outState == null || mViewPager == null) {
			return;
		}

		outState.putInt(MvpModel.POSITION_KEY, mViewPager.getCurrentItem());
		outState.putParcelable(LAST_INTENT_KEY, mLastIntent);
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

		MvpPresenter.getInstance().OnRequestItemUpdateComplete.notifySuccess(mData);

		if (mLastIntent != null) {
			final String action = mLastIntent.getAction();
			if (action != null && mLastIntent.getAction().equals(FavoritesActivity.ACTION)) {
				return;
			}
		}

		Log.d("DETAIL", "invoking requestResults");
		requestResults(mLastIntent);
	}

	@Override
	public void onTransactionError(MvpPresenter.TransactionType type) {
		displayMessage(R.string.msg_db_error);
	}

	@Override
	public void onTransactionCursorReady(Cursor cursor) {
		mCursor = cursor;
		mData = getItem(mAdapterPosition);

		initViewPager(mAdapterPosition, cursor);
	}

	@Override
	public void requestTransaction(Bundle data) {
	}

	private void initViewPager(final int position, final Cursor cursor) {
		if (mPagerAdapter != null && mViewPager != null) {
			mViewPager.setCurrentItem(position);
			mPagerAdapter.changeCursor(cursor);

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
