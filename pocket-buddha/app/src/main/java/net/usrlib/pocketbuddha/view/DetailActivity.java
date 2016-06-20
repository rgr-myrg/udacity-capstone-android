package net.usrlib.pocketbuddha.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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

		final String action = intent.getAction();

		if (action == null) {
			MvpPresenter.getInstance().requestItemsFromDb(this);
			return;
		}

		// Determine which cursor results to request based on Intent Action
		if (action.equals(Intent.ACTION_SEARCH)) {
			startSearchResultsActivity(intent);

		} else if (action.equals(Intent.ACTION_VIEW)) {
			requestTitleSearchResults(intent);

		} else if (action.equals(FavoritesActivity.ACTION)) {
			requestFavorites();

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

	// Loader Helper Callbacks
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

		//TODO: Get last transaction ??? Figure out if view should be updated. Ex: Came from search result
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
	}

	@Override
	public void requestTransaction(Bundle data) {
	}

	private void initViewPager(final int position, final Cursor cursor) {
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

	private void requestTitleSearchResults(final Intent intent) {
		final Uri uri = Uri.parse(intent.getDataString());

		MvpPresenter.getInstance().requestTitleSearch(this, uri);
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

	private void onMp3StreamReady(final View view, final FloatingActionButton button) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				button.hideProgress();
				closeFloatingActionMenu(view);
			}
		});
	}

	private void onMp3StreamError(final View view, final FloatingActionButton button) {
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

	private void toggleFavoriteIcon(final View view, final MvpModel data) {
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

	private String formatValueForIntent(final String value) {
		return Html.fromHtml(value + "<br/>" + "<br/>").toString();
	}

	private MvpModel getItem(final int position) {
		mCursor.moveToPosition(position);
		return MvpModel.fromDbCursor(mCursor);
	}

	private void displayMessage(final int msgId) {
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
