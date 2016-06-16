package net.usrlib.pocketbuddha.mvp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.usrlib.pocketbuddha.provider.FeedContract;
import net.usrlib.pocketbuddha.service.FeedReceiver;
import net.usrlib.pocketbuddha.service.FeedService;

import org.json.JSONException;

/**
 * Created by rgr-myrg on 6/7/16.
 */
public class MvpPresenter {
	private static MvpPresenter sInstance = null;
	private FeedReceiver mFeedReceiver = null;
	private Cursor mSelectAllDbCursor  = null;

	public static final MvpPresenter getInstance() {
		if (sInstance == null) {
			sInstance = new MvpPresenter();
		}

		return sInstance;
	}

	public void requestFeedService(final AppCompatActivity app) {
		final MvpView mvpView = (MvpView) app;

		mFeedReceiver = new FeedReceiver(new Handler());

		mFeedReceiver.setCallback(new FeedReceiver.OnReceiveResult() {
			@Override
			public void onComplete(int resultCode, Bundle resultData) {
				switch (resultCode) {
					case FeedService.FINISHED:
						mvpView.onTransactionSuccess(
								TransactionType.FEED_SERVICE,
								resultData
						);

						break;

					case FeedService.ERROR:
						mvpView.onTransactionError(TransactionType.FEED_SERVICE);

						break;
				}
			}
		});

		final Intent intent = new Intent(Intent.ACTION_SYNC, null, app, FeedService.class);

		intent.putExtra(FeedReceiver.NAME, mFeedReceiver);
		intent.putExtra(FeedService.COMMAND_KEY, FeedService.FETCH_COMMAND);

		app.startService(intent);

		mvpView.onTransactionProgress(TransactionType.FEED_SERVICE);
	}

	public void requestDbBulkInsert(final AppCompatActivity app, final Bundle data) {
		final MvpView mvpView = (MvpView) app;
		final ContentResolver contentResolver = app.getContentResolver();

		if (data == null || data == Bundle.EMPTY || contentResolver == null) {
			mvpView.onTransactionError(TransactionType.DB_BULK_INSERT);
			return;
		}

		final String jsonDataString = data.getString(FeedService.DATA_RESULT);

		if (jsonDataString == null) {
			mvpView.onTransactionError(TransactionType.DB_BULK_INSERT);
			return;
		}

		// Notify the View of progress
		mvpView.onTransactionProgress(TransactionType.DB_BULK_INSERT);

		try {
			final int rowCount = contentResolver.bulkInsert(
					FeedContract.ItemsEntry.CONTENT_BULK_INSERT_URI,
					MvpModel.fromJsonStringAsContentValues(jsonDataString)
			);

			if (rowCount > 0) {
				mvpView.onTransactionSuccess(
						TransactionType.DB_BULK_INSERT,
						Bundle.EMPTY
				);

				contentResolver.notifyChange(FeedContract.ItemsEntry.CONTENT_BULK_INSERT_URI, null);
			} else {
				mvpView.onTransactionError(TransactionType.DB_BULK_INSERT);
			}
		} catch (JSONException e) {
			mvpView.onTransactionError(TransactionType.DB_BULK_INSERT);
			e.printStackTrace();
		}
	}

	public void requestItemsFromDb(final AppCompatActivity app) {
//		final MvpView mvpView = (MvpView) app;
//		final ContentResolver contentResolver = app.getContentResolver();
//
//		if (contentResolver == null) {
//			mvpView.onTransactionError(TransactionType.DB_QUERY);
//			return;
//		}
//
//		final LoaderManager loaderManager = app.getSupportLoaderManager();
//
//		if (loaderManager == null) {
//			mvpView.onTransactionError(TransactionType.DB_QUERY);
//			return;
//		}
//
//		final MvpModel.LoaderHelper loaderHelper = new MvpModel.LoaderHelper(
//				app.getApplicationContext(),
//				FeedContract.ItemsEntry.CONTENT_URI,
//				mvpView
//		);
//
//		loaderManager.initLoader(1, null, loaderHelper);
		requestLoaderManagerForDbQuery(app, FeedContract.ItemsEntry.CONTENT_URI);
	}

	public void requestFavoritesFromDb(final AppCompatActivity app) {
		requestLoaderManagerForDbQuery(app, FeedContract.ItemsEntry.CONTENT_FAVORITES_URI);
	}

	public void requestItemUpdate(final AppCompatActivity app, final MvpModel data) {
		final MvpView mvpView = (MvpView) app;
		final ContentResolver contentResolver = app.getContentResolver();

		if (contentResolver == null) {
			mvpView.onTransactionError(TransactionType.DB_UPDATE);
			return;
		}

		final int rowCount = contentResolver.update(
				FeedContract.ItemsEntry.CONTENT_ITEM_UPDATE_URI,
				data.toContentValues(),
				FeedContract.ItemsEntry.TITLE_COLUMN + "=?",
				new String[] {
						data.getTitle()
				}
		);

		Log.d("PRESENTER", "rowCount: " + rowCount);
		if (rowCount > 0) {
			mvpView.onTransactionSuccess(
					TransactionType.DB_UPDATE,
					Bundle.EMPTY
			);

			contentResolver.notifyChange(FeedContract.ItemsEntry.CONTENT_ITEM_UPDATE_URI, null);
		} else {
			mvpView.onTransactionError(TransactionType.DB_UPDATE);
		}
	}

	public static enum TransactionType {
		FEED_SERVICE,
		DB_BULK_INSERT,
		DB_QUERY,
		DB_UPDATE
	}

	private void requestLoaderManagerForDbQuery(final AppCompatActivity app, final Uri uri) {
		final MvpView mvpView = (MvpView) app;
		final ContentResolver contentResolver = app.getContentResolver();

		if (contentResolver == null) {
			mvpView.onTransactionError(TransactionType.DB_QUERY);
			return;
		}

		final LoaderManager loaderManager = app.getSupportLoaderManager();

		if (loaderManager == null) {
			mvpView.onTransactionError(TransactionType.DB_QUERY);
			return;
		}

		final MvpModel.LoaderHelper loaderHelper = new MvpModel.LoaderHelper(
				app.getApplicationContext(),
				uri,
				mvpView
		);

		loaderManager.initLoader(1, null, loaderHelper);
	}
}
