package net.usrlib.pocketbuddha.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;
import net.usrlib.pocketbuddha.player.SoundPlayer;
import net.usrlib.pocketbuddha.util.IntentUtil;
import net.usrlib.pocketbuddha.util.SnackbarUtil;

/**
 * Created by rgr-myrg on 6/7/16.
 */
public class DetailActivity extends AppCompatActivity implements MvpView {
	protected View mRootView = null;
	protected DetailAdapter mPagerAdapter = null;
	protected ViewPager mViewPager = null;
	protected Cursor mCursor = null;
	protected MvpModel mData = null;

	protected SoundPlayer mSoundPlayer = new SoundPlayer();
	protected int mAdapterPosition = 0;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_detail_activity);

		final Intent intent = getIntent();

		if (intent == null) {
			return;
		}

		mRootView = findViewById(android.R.id.content);

		mAdapterPosition = intent.getIntExtra(MvpModel.POSITION_KEY, 0);

//		mData = getItem(position);
//
//		if (mData == null) {
//			// Sum Ting Wong
//			return;
//		}

//		int startingPosition = 0;
//		if (intent.hasExtra(FavoritesAdapter.ITEM_POSITION_KEY)) {
//			mHasFavoriteItems = true;
//			startingPosition = intent.getIntExtra(FavoritesAdapter.ITEM_POSITION_KEY, 0);
//			mData = AwsFeedLoader.getFeedItemsFromDb().get(startingPosition);
//			mDataSet = AwsFeedLoader.getFeedItemsFromDb();
//
//		} else if(intent.hasExtra(HomeAdapter.ITEM_POSITION_KEY)) {
//			mHasFavoriteItems = false;
//			startingPosition = intent.getIntExtra(HomeAdapter.ITEM_POSITION_KEY, 0);
//			mData = AwsFeedLoader.getFeedItemsFromServer().get(startingPosition);
//			mDataSet = AwsFeedLoader.getFeedItemsFromServer();
//
//		} else if(intent.hasExtra(SearchResultAdapter.ITEM_POSITION_KEY)) {
//			mHasFavoriteItems = false;
//			startingPosition  = intent.getIntExtra(SearchResultAdapter.ITEM_POSITION_KEY, 0);
//			mData = AwsFeedLoader.getItemsFromSearchResult().get(startingPosition);
//			mDataSet = AwsFeedLoader.getItemsFromSearchResult();
//		}

//		MvpPresenter.getInstance().requestItemsFromDb(this, new MvpPresenter.DatabaseEvent() {
//			@Override
//			public void onDbCursorReady(Cursor cursor) {
//				mCursor = cursor;
//				mData = getItem(position);
//				initViewPager(position, cursor);
//			}
//
//			@Override
//			public void onDbError() {
//
//			}
//		});

		if (intent.hasExtra(FavoritesActivity.NAME)
				&& intent.getBooleanExtra(FavoritesActivity.NAME, false)) {
			//TODO: Specify sort order from intent
			MvpPresenter.getInstance().requestFavoritesSortByDateDesc(this);
		} else {
			MvpPresenter.getInstance().requestItemsFromDb(this);
		}
	}

	public void bindDataToView(final View view, final MvpModel data) {
		if (data == null) {
			return;
		}

		final ImageView image  = (ImageView) view.findViewById(R.id.detail_item_image);
		final ImageView icon   = (ImageView) view.findViewById(R.id.detail_item_favorite_icon);
		final TextView header  = (TextView) view.findViewById(R.id.detail_item_header);
		final TextView pali    = (TextView) view.findViewById(R.id.detail_item_pali);
		final TextView english = (TextView) view.findViewById(R.id.detail_item_english);

		setTitle(data.getTitle());

		Glide.with(this).load(data.getImageUrl()).into(image);

		final int iconResource = data.isFavorite()
				? R.drawable.ic_star_black_36dp
				: R.drawable.ic_star_border_black_36dp;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			icon.setImageDrawable(getDrawable(iconResource));
		} else {
			icon.setImageBitmap(
					BitmapFactory.decodeResource(getResources(), iconResource)
			);
		}

		header.setText(data.getTitle());
		pali.setText(Html.fromHtml(data.getPali()));
		english.setText(Html.fromHtml(data.getEnglish()));
	}

	public void onShareItClicked(View view) {
		closeFloatingActionMenu(view);

		if (mData == null) {
			return;
		}

		final String body = mData.getPali() + "\n\n" + mData.getEnglish();

		IntentUtil.startWithChooser(
				this,
				formatValueForIntent(mData.getTitle()),
				formatValueForIntent(body)
		);
	}

	public void onFavoriteItClicked(View view) {
		if (mData == null) {
			return;
		}

		mData.setFavorite(!mData.isFavorite());

		toggleFavoriteIcon(view, mData);

		MvpPresenter.getInstance().requestItemUpdate(this, mData);
	}

	public void onPlayItClicked(final View view) {
		Log.d("onPlayItClicked", mData.getMp3Link());
		if (mData == null) {
			return;
		}

		final FloatingActionButton button = (FloatingActionButton) view;

		if (button == null) {
			return;
		}

		new AsyncTask<Void, Void, Void>() {
			private boolean hasLoaded = false;
			@Override
			protected Void doInBackground(Void... params) {
				mSoundPlayer.loadMp3StreamWithUrl(
						mData.getMp3Link(),
						new SoundPlayer.OnPrepared() {
							@Override
							public void onReady() {
								onSoundPlayerReady();
							}
							@Override
							public void onComplete() {
							}
							@Override
							public void onError() {
								onMp3StreamError(view, button);
							}
						}
				);

				while (!hasLoaded) {
					try {
						Thread.sleep(1000);
						publishProgress();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				return null;
			}

			@Override
			protected void onProgressUpdate(Void... values) {
				super.onProgressUpdate(values);
				button.setProgress(100, true);
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				mSoundPlayer.play();
				onMp3StreamReady(view, button);
			}
			private void onSoundPlayerReady() {
				hasLoaded = true;
			}
		}.execute();

		button.setProgress(100, true);
	}

	@Override
	public void onTransactionProgress(MvpPresenter.TransactionType type) {

	}

	@Override
	public void onTransactionSuccess(MvpPresenter.TransactionType type, Bundle data) {
		if (type != MvpPresenter.TransactionType.DB_UPDATE) {
			return;
		}

		final int msgId = mData.isFavorite()
				? R.string.msg_favorite_success
				: R.string.msg_favorite_removed;

		displayMessage(msgId);

		// Refresh this view's data for DB_UPDATE transactions
		MvpPresenter.getInstance().requestItemsFromDb(this);
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
//		if (mPagerAdapter == null) {
//			initViewPager(mAdapterPosition, cursor);
//		} else {
//			// TODO: Investigate issue where the view does not retain the latest update
//			//mPagerAdapter.updateCursor(cursor);
//		}
	}

	protected void initViewPager(final int position, final Cursor cursor) {
		if (mPagerAdapter != null) {
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

	protected void onMp3StreamReady(final View view, final FloatingActionButton button) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				button.hideProgress();
				closeFloatingActionMenu(view);
			}
		});
	}

	protected void onMp3StreamError(final View view, final FloatingActionButton button) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				button.hideProgress();
				closeFloatingActionMenu(view);
				SnackbarUtil.showMessage(
						mRootView,
						getString(R.string.msg_play_error)
				);
			}
		});
	}

	protected void toggleFavoriteIcon(final View view, final MvpModel data) {
		final ImageView icon = (ImageView) view;
		final int iconResource = data.isFavorite()
				? R.drawable.ic_star_black_36dp
				: R.drawable.ic_star_border_black_36dp;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			icon.setImageDrawable(getDrawable(iconResource));
		} else {
			icon.setImageBitmap(
					BitmapFactory.decodeResource(getResources(), iconResource)
			);
		}
	}

	protected void closeFloatingActionMenu(View view) {
		if (view == null) {
			return;
		}

		final FloatingActionMenu menu = (FloatingActionMenu) view.getParent();

		if (menu == null) {
			return;
		}

		menu.close(true);
	}

//	protected void onUpdateFavoriteComplete(final boolean success) {
//		final int msgId = mData.isFavorite()
//				? R.string.msg_favorite_success
//				: R.string.msg_favorite_removed;
//
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				SnackbarUtil.showMessage(
//						mRootView,
//						getString(success ? msgId : R.string.msg_db_error)
//				);
//			}
//		});
//	}

//	protected void updateAdapterDataSet() {
//		if (!mHasFavoriteItems) {
//			return;
//		}
//
//		// If viewing favorite items it means FavoritesActivity is the parent activity
//		// and needs it's adapter's data updated to pick up AwsFeeLoader items.
////		final FavoritesActivity activity = FavoritesActivity.getInstance();
////
////		if (activity != null) {
////			activity.updateAdapterDataSet();
////		}
//	}

	protected String formatValueForIntent(final String value) {
		return Html.fromHtml(value + "<br/>" + "<br/>").toString();
	}

	protected MvpModel getItem(final int position) {
		mCursor.moveToPosition(position);
		return MvpModel.fromDbCursor(mCursor);
	}

	protected void displayMessage(final int msgId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				SnackbarUtil.showMessage(
						mRootView,
						getString(msgId)
				);
			}
		});
	}
}
