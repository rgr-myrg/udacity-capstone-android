package net.usrlib.pocketbuddha.mvp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.usrlib.pocketbuddha.AnalyticsApplication;
import net.usrlib.pocketbuddha.BuildConfig;
import net.usrlib.pocketbuddha.provider.FeedContract;
import net.usrlib.pocketbuddha.provider.SearchContract;
import net.usrlib.pocketbuddha.provider.WordContract;
import net.usrlib.pocketbuddha.service.FeedReceiver;
import net.usrlib.pocketbuddha.service.FeedService;
import net.usrlib.pocketbuddha.util.Preferences;

import org.json.JSONException;

/**
 * Created by rgr-myrg on 6/7/16.
 */
public class MvpPresenter {
	public static final String NAME = MvpPresenter.class.getSimpleName();
	public static final String TRANSACTION_TYPE_KEY = "transactionType";

	private static MvpPresenter sInstance = null;
	private FeedReceiver mFeedReceiver = null;
	private boolean mHasInitLoader = false;
	private Uri mLastDbQueryUri = null;

	public static final MvpPresenter getInstance() {
		if (sInstance == null) {
			sInstance = new MvpPresenter();
		}

		return sInstance;
	}

	public Uri getLastDbQueryUri() {
		return mLastDbQueryUri;
	}

	public void requestFeedDownloadService(final AppCompatActivity app) {
		requestEndPointTransaction(
				app,
				BuildConfig.FEED_PAGER_END_POINT,
				TransactionType.DOWNLOAD_FEED_ITEMS_SERVICE
		);
	}

	public void requestDictionaryDownloadService(final AppCompatActivity app) {
		requestEndPointTransaction(
				app,
				BuildConfig.PALI_TERMS_END_POINT,
				TransactionType.DOWNLOAD_DICTIONARY_SERVICE
		);
	}

	public void requestEndPointTransaction(final AppCompatActivity app,
	                                       final String url,
	                                       final TransactionType type) {
		final MvpView mvpView = (MvpView) app;

		mFeedReceiver = new FeedReceiver(new Handler());
		mFeedReceiver.setCallback(new FeedReceiver.OnReceiveResult() {
			@Override
			public void onComplete(int resultCode, Bundle resultData) {
				switch (resultCode) {
					case FeedService.FINISHED:
						Log.i(NAME, "onComplete invoking onTransactionSuccess");
						mvpView.onTransactionSuccess(
								type,
								resultData
						);

						break;

					case FeedService.ERROR:
						Log.i(NAME, "onComplete invoking onTransactionError");
						mvpView.onTransactionError(type);
						break;
				}
			}
		});

		final Intent intent = new Intent(Intent.ACTION_SYNC, null, app, FeedService.class);

		intent.putExtra(FeedReceiver.NAME, mFeedReceiver);
		intent.putExtra(FeedService.COMMAND_KEY, FeedService.FETCH_COMMAND);
		intent.putExtra(FeedService.END_POINT_KEY, url);

		app.startService(intent);

		mvpView.onTransactionProgress(type);
	}

	public void requestBulkInsertWithFeedItems(final AppCompatActivity app, final Bundle data) {
		requestDbBulkInsert(
				app,
				data,
				TransactionType.DB_FEED_ITEMS_BULK_INSERT
		);
	}

	public void requestBulkInsertWithDictionaryItems(final AppCompatActivity app,
	                                                 final Bundle data) {
		requestDbBulkInsert(
				app,
				data,
				TransactionType.DB_DICTIONARY_BULK_INSERT
		);
	}

	public void requestItemsFromDb(final AppCompatActivity app) {
		requestLoaderManagerForDbQuery(app, FeedContract.ItemsEntry.CONTENT_URI);
	}

	public void requestFavoritesSortByTitleAsc(final AppCompatActivity app) {
		requestLoaderManagerForDbQuery(
				app, FeedContract.ItemsEntry.CONTENT_FAVORITES_BY_TITLE_ASC_URI
		);
	}

	public void requestFavoritesSortByTitleDesc(final AppCompatActivity app) {
		requestLoaderManagerForDbQuery(
				app, FeedContract.ItemsEntry.CONTENT_FAVORITES_BY_TITLE_DESC_URI
		);
	}

	public void requestFavoritesSortByDateAsc(final AppCompatActivity app) {
		requestLoaderManagerForDbQuery(
				app, FeedContract.ItemsEntry.CONTENT_FAVORITES_BY_DATE_ASC_URI
		);
	}

	public void requestFavoritesSortByDateDesc(final AppCompatActivity app) {
		requestLoaderManagerForDbQuery(
				app, FeedContract.ItemsEntry.CONTENT_FAVORITES_BY_DATE_DESC_URI
		);
	}

	public void requestDbBulkInsert(final AppCompatActivity app,
	                                final Bundle data,
	                                final TransactionType type) {
		final MvpView mvpView = (MvpView) app;
		final ContentResolver contentResolver = app.getContentResolver();

		if (data == null || data == Bundle.EMPTY || contentResolver == null) {
			mvpView.onTransactionError(type);
			return;
		}

		final String jsonDataString = data.getString(FeedService.DATA_RESULT);

		if (jsonDataString == null) {
			mvpView.onTransactionError(type);
			return;
		}

		Uri uri = null;
		ContentValues[] values = null;

		try {
			switch (type) {
				case DB_FEED_ITEMS_BULK_INSERT:
					uri = FeedContract.ItemsEntry.BULK_INSERT_CONTENT_URI;
					values = MvpModel.fromJsonStringAsContentValues(jsonDataString);
					break;
				case DB_DICTIONARY_BULK_INSERT:
					uri = WordContract.DictionaryEntry.BULK_INSERT_CONTENT_URI;
					values = MvpModel.Dictionary.fromJsonStringAsContentValues(jsonDataString);
					break;
				default:
					Log.w(NAME, "requestDbBulkInsert uri not supported. " + uri);
					break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (uri == null || values == null) {
			mvpView.onTransactionError(type);
			return;
		}

		Log.i(NAME, "requestDbBulkInsert " + uri);

		// Notify the View of progress
		mvpView.onTransactionProgress(type);

		final int rowCount = contentResolver.bulkInsert(uri, values);

		if (rowCount > 0) {
			contentResolver.notifyChange(uri, null);
			mvpView.onTransactionSuccess(
					type,
					Bundle.EMPTY
			);
		} else {
			mvpView.onTransactionError(type);
		}
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

	public void requestLoaderManagerForDbQuery(final AppCompatActivity app, final Uri uri) {
		final MvpView mvpView = (MvpView) app;
		final ContentResolver contentResolver = app.getContentResolver();

		if (contentResolver == null || uri == null) {
			mvpView.onTransactionError(TransactionType.DB_QUERY);
			return;
		}

		// Save last Uri in case we need to repeat the query in a diff activity
		mLastDbQueryUri = uri;

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

		Log.d(NAME, "mHasInitLoader: " + mHasInitLoader);

		// Init Loader for Updates invariably
//		if (uri.equals(FeedContract.ItemsEntry.CONTENT_ITEM_UPDATE_URI)) {
//			Log.d(NAME, "CONTENT_ITEM_UPDATE_URI initLoader");
//			loaderManager.initLoader(MvpModel.DB_QUERY_LOADER_ID, null, loaderHelper);
//		} else {
			if (!mHasInitLoader) {
				mHasInitLoader = true;
				loaderManager.initLoader(MvpModel.DB_QUERY_LOADER_ID, null, loaderHelper);
			} else {
				loaderManager.restartLoader(MvpModel.DB_QUERY_LOADER_ID, null, loaderHelper);
			}
	//	}
	}

	public void requestTitleSearch(final AppCompatActivity app, final Uri uri) {
		AnalyticsApplication.trackTitleSearch(app.getApplication(), uri.getLastPathSegment());
		requestSearch(app, uri);
	}

	public void requestTextSearch(final AppCompatActivity app, final String query) {
		AnalyticsApplication.trackSearchQuery(app.getApplication(), query);
		requestSearch(
				app,
				SearchContract.SearchEntry.SEARCH_TEXT_CONTENT_URI
						.buildUpon()
						.appendPath(query)
						.build()
		);
	}

	public void requestSearch(final AppCompatActivity app, final Uri uri) {
		final MvpView mvpView = (MvpView) app;
		final ContentResolver contentResolver = app.getContentResolver();

		if (contentResolver == null || uri == null) {
			mvpView.onTransactionError(TransactionType.SEARCH_ITEM);
			return;
		}

		final LoaderManager loaderManager = app.getSupportLoaderManager();

		if (loaderManager == null) {
			mvpView.onTransactionError(TransactionType.SEARCH_ITEM);
			return;
		}

		final MvpModel.LoaderHelper loaderHelper = new MvpModel.LoaderHelper(
				app.getApplicationContext(),
				uri,
				mvpView
		);

		// TODO: Does loader need a restart if already started for subsequent searches?
		loaderManager.initLoader(MvpModel.DB_SEARCH_LOADER_ID, null, loaderHelper);
	}

	public void requestPaliWord(final MvpView mvpView, final Context context) {
		final ContentResolver contentResolver = context.getContentResolver();

		if (contentResolver == null) {
			mvpView.onTransactionError(TransactionType.DAILY_WORD);
			return;
		}

		final Cursor cursor = contentResolver.query(
				WordContract.DictionaryEntry.DAILY_WORD_CONTENT_URI.buildUpon()
						.appendPath(String.valueOf(Preferences.getCurrentWordItemId(context)))
						.build(),
				null, null, null, null
		);

		if (cursor == null) {
			mvpView.onTransactionError(TransactionType.DAILY_WORD);
			return;
		}

		final int itemId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));

		Preferences.setCurrentWordItemId(context, itemId);
		mvpView.onTransactionCursorReady(cursor);
	}

	public static enum TransactionType {
		DOWNLOAD_FEED_ITEMS_SERVICE,
		DOWNLOAD_DICTIONARY_SERVICE,
		DB_FEED_ITEMS_BULK_INSERT,
		DB_DICTIONARY_BULK_INSERT,
		DB_QUERY,
		DB_UPDATE,
		SEARCH_ITEM,
		DAILY_WORD
	}
}
