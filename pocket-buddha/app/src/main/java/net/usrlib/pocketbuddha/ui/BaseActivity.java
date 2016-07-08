package net.usrlib.pocketbuddha.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.usrlib.pocketbuddha.util.TrackerUtil;
import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.player.SoundPlayer;
import net.usrlib.pocketbuddha.util.IntentUtil;
import net.usrlib.pocketbuddha.util.SnackbarUtil;

/**
 * Created by rgr-myrg on 6/11/16.
 */
public class BaseActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private boolean mIsTablet = false;
	protected View mRootView  = null;
	protected MvpModel mData  = null;
	protected Cursor mCursor  = null;
	protected int mAdapterPosition = 0;
	protected SoundPlayer mSoundPlayer = new SoundPlayer();

	protected void initContentView(final int resource) {
		setContentView(resource);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		initDrawerLayout(toolbar);
		initNavigationView();

		mRootView = findViewById(android.R.id.content);
		mIsTablet = findViewById(R.id.tablet_detail_container) != null;
	}

	protected void initDrawerLayout(Toolbar toolbar) {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_drawer_layout);

		if (drawer == null) {
			return;
		}

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this,
				drawer,
				toolbar,
				R.string.navigation_drawer_open,
				R.string.navigation_drawer_close
		);

		drawer.addDrawerListener(toggle);
		toggle.syncState();
	}

	protected void initNavigationView() {
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		if (navigationView != null) {
			navigationView.setNavigationItemSelectedListener(this);
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_drawer_layout);

		if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);

		final MenuItem searchItem = menu.findItem(R.id.action_search);
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(
						new ComponentName(this, DetailActivity.class)
				)
		);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();

		switch (itemId) {
			case R.id.action_home:
				startActivity(new Intent(this, HomeActivity.class));
				break;
			case R.id.action_favorites:
				startActivity(new Intent(this, FavoritesActivity.class));
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		final int itemId = item.getItemId();

		switch (itemId) {
			case R.id.nav_home:
				startActivity(new Intent(this, HomeActivity.class));
				break;
			case R.id.nav_favorites:
				startActivity(new Intent(this, FavoritesActivity.class));
				break;
			case R.id.nav_about:
				startActivity(new Intent(this, AboutActivity.class));
				break;
			case R.id.nav_credits:
				startActivity(new Intent(this, CreditsActivity.class));
				break;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_drawer_layout);
		drawer.closeDrawer(GravityCompat.START);

		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mSoundPlayer != null && mSoundPlayer.isPlaying()) {
			mSoundPlayer.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mSoundPlayer != null && mSoundPlayer.isLoaded()) {
			mSoundPlayer.unPause();
		}
	}

	public MvpModel getItem(final int position) {
		if (mCursor == null) {
			return null;
		}
		Log.d("BaseActivity", "getItem position: " + position);

		mCursor.moveToPosition(position);
		mAdapterPosition = position;

		return MvpModel.fromDbCursor(mCursor);
	}

	public boolean isTablet() {
		return mIsTablet;
	}

	public void setAdapterPosition(int position) {
		Log.d("BaseActivity", "setAdapterPosition position: " + position);
		mAdapterPosition = position;
	}

	public String formatValueForIntent(final String value) {
		return Html.fromHtml(value + "<br/>" + "<br/>").toString();
	}

	public void onShareItClicked(View view) {
		closeFloatingActionMenu(view);

		mData = getItem(mAdapterPosition);

		final String body = mData.getPali() + "\n\n" + mData.getEnglish();

		IntentUtil.startWithChooser(
				this,
				formatValueForIntent(mData.getTitle()),
				formatValueForIntent(body)
		);

		TrackerUtil.trackShareItClicked(getApplication(), mData.getTitle());
	}

	public void closeFloatingActionMenu(View view) {
		if (view == null) {
			return;
		}

		final FloatingActionMenu menu = (FloatingActionMenu) view.getParent();

		if (menu == null) {
			return;
		}

		menu.close(true);
	}

	public void toggleFavoriteIcon(final View view, final MvpModel data) {
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

	public void onFavoriteItClicked(View view) {
		mData = getItem(mAdapterPosition);
		mData.setFavorite(!mData.isFavorite());

		toggleFavoriteIcon(view, mData);

		MvpPresenter.getInstance().requestItemUpdate(this, mData);

		if(mData.isFavorite()) {
			TrackerUtil.trackFavoriteItClicked(getApplication(), mData.getTitle());
		}
	}

	public void displayMessage(final int msgId) {
		if (mRootView == null) {
			return;
		}

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

	public void onPlayItClicked(final View view) {
		mData = getItem(mAdapterPosition);

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

		TrackerUtil.trackPlayItClicked(getApplication(), mData.getTitle());
	}

	public void onMp3StreamReady(final View view, final FloatingActionButton button) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				button.hideProgress();
				closeFloatingActionMenu(view);
			}
		});
	}

	public void onMp3StreamError(final View view, final FloatingActionButton button) {
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

	public void setAdView() {
		final AdView adView = (AdView) findViewById(R.id.adView);

		if (adView == null) {
			return;
		}

		// Create an ad request. Check logcat output for the hashed device ID to
		// get test ads on a physical device. e.g.
		// "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
		final AdRequest adRequest = new AdRequest.Builder()
		//		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.build();

		adView.loadAd(adRequest);
	}

	public class BaseWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
}
