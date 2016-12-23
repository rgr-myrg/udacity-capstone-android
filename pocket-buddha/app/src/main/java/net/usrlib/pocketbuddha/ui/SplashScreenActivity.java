package net.usrlib.pocketbuddha.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;
import net.usrlib.pocketbuddha.util.JsonLoader;
import net.usrlib.pocketbuddha.util.Preferences;

import static net.usrlib.pocketbuddha.service.FeedService.DATA_RESULT;

/**
 * Created by rgr-myrg on 6/6/16.
 */
public class SplashScreenActivity extends AppCompatActivity implements MvpView {
	public static final String TAG = SplashScreenActivity.class.getSimpleName();
	private TextView mMessageTextView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		mMessageTextView = (TextView) findViewById(R.id.splash_screen_message_textview);

		final Context context = getApplicationContext();

		if (Preferences.hasDataInstall(context) && Preferences.hasDictionaryInstall(context)) {
			Log.d(TAG, "hasDataInstall & hasDictionaryInstall = true. Invoke startHomeActivity().");
			startHomeActivity();
		} else {
			Log.d(TAG, "Loading feeds for the first time.");
			loadPaliCannon();
		}
	}

	protected void loadPaliCannon() {
		Log.d(TAG, "loadPaliCannon starts");
		final String jsonString = JsonLoader.loadJSONFromAsset(
				getApplicationContext(), JsonLoader.PALI_CANNON_JS
		);

		final Bundle bundle = new Bundle();
		bundle.putString(DATA_RESULT, (String) jsonString);

		MvpPresenter.getInstance().requestDbBulkInsert(
				this,
				bundle,
				MvpPresenter.TransactionType.DB_FEED_ITEMS_BULK_INSERT
		);
	}

	protected void loadPaliTerms() {
		Log.d(TAG, "loadPaliTerms starts");
		final String jsonString = JsonLoader.loadJSONFromAsset(
				getApplicationContext(), JsonLoader.PALI_TERMS_JS
		);

		final Bundle bundle = new Bundle();
		bundle.putString(DATA_RESULT, (String) jsonString);

		MvpPresenter.getInstance().requestDbBulkInsert(
				this,
				bundle,
				MvpPresenter.TransactionType.DB_DICTIONARY_BULK_INSERT
		);
	}

	@Override
	public void onTransactionSuccess(MvpPresenter.TransactionType type, Bundle bundle) {
		switch (type) {
			case DB_FEED_ITEMS_BULK_INSERT:
				Log.d(TAG, "onTransactionSuccess invoking setHasDataInstall");
				Preferences.setHasDataInstall(getApplicationContext(), true);
				loadPaliTerms();
				break;
			case DB_DICTIONARY_BULK_INSERT:
				Log.d(TAG, "onTransactionSuccess invoking setHasDictionaryInstall");
				Preferences.setHasDictionaryInstall(getApplicationContext(), true);
				startHomeActivity();
				break;
		}
	}

//	@Override
//	public void onTransactionSuccess(MvpPresenter.TransactionType type, Bundle bundle) {
//		switch (type) {
//			case DOWNLOAD_FEED_ITEMS_SERVICE:
//				MvpPresenter.getInstance().requestBulkInsertWithFeedItems(this, bundle);
//				break;
//
//			case DOWNLOAD_DICTIONARY_SERVICE:
//				MvpPresenter.getInstance().requestBulkInsertWithDictionaryItems(this, bundle);
//				break;
//
//			case DB_FEED_ITEMS_BULK_INSERT:
//				Preferences.setHasDataInstall(getApplicationContext(), true);
//				// TODO: Add a request queue in Presenter. Chain request here for now:
//				MvpPresenter.getInstance().requestDictionaryDownloadService(this);
//				break;
//
//			case DB_DICTIONARY_BULK_INSERT:
//				Preferences.setHasDictionaryInstall(getApplicationContext(), true);
//				startHomeActivity();
//				break;
//		}
//	}

	@Override
	public void onTransactionProgress(MvpPresenter.TransactionType type) {
		if (type == MvpPresenter.TransactionType.DB_FEED_ITEMS_BULK_INSERT) {
			displayMessage(R.string.splash_screen_progress_msg);
		}
	}

	@Override
	public void onTransactionError(MvpPresenter.TransactionType type) {
		displayMessage(R.string.splash_screen_error_msg);
	}

	@Override
	public void onTransactionCursorReady(Cursor cursor) {
	}

	@Override
	public void requestTransaction(Bundle data) {
	}

	private void startHomeActivity() {
		displayMessage(R.string.splash_screen_finished_msg);

		runOnUiThread(() -> {
			startActivity(
					new Intent(getBaseContext(), HomeActivity.class)
			);

			finish();
		});
	}

	private void displayMessage(final int msgId) {
		runOnUiThread(() -> {
			if (mMessageTextView != null) {
				mMessageTextView.setText(getString(msgId));
			}
		});
	}
}
