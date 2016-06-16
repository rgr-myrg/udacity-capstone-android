package net.usrlib.pocketbuddha.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import net.usrlib.pocketbuddha.data.DbHelper;
import net.usrlib.pocketbuddha.model.FeedItemDTO;
import net.usrlib.pocketbuddha.provider.FeedContract;
import net.usrlib.pocketbuddha.service.FeedReceiver;
import net.usrlib.pocketbuddha.service.FeedService;

import org.json.JSONException;

/**
 * Created by rgr-myrg on 6/7/16.
 */
public class Presenter {
	private static Presenter sInstance = null;
	private FeedReceiver mFeedReceiver = null;
	private Cursor mSelectAllDbCursor  = null;

	public static final Presenter getInstance() {
		if (sInstance == null) {
			sInstance = new Presenter();
		}

		return sInstance;
	}

	public void requestFeedService(final AppCompatActivity app) {
		final TransactionEvent callback = (TransactionEvent) app;

		mFeedReceiver = new FeedReceiver(new Handler());

		mFeedReceiver.setCallback(new FeedReceiver.OnReceiveResult() {
			@Override
			public void onComplete(int resultCode, Bundle resultData) {
				switch (resultCode) {
					case FeedService.FINISHED:
						callback.onTransactionSuccess(TransactionType.FEED_SERVICE, resultData);
						break;

					case FeedService.ERROR:
						callback.onTransactionError(TransactionType.FEED_SERVICE);
						break;
				}
			}
		});

		final Intent intent = new Intent(Intent.ACTION_SYNC, null, app, FeedService.class);

		intent.putExtra(FeedReceiver.NAME, mFeedReceiver);
		intent.putExtra(FeedService.COMMAND_KEY, FeedService.FETCH_COMMAND);

		app.startService(intent);
	}

	public void requestDbBulkInsert(final AppCompatActivity app, final Bundle data) {
		final TransactionEvent callback = (TransactionEvent) app;
		final ContentResolver contentResolver = app.getContentResolver();

		if (data == null || data == Bundle.EMPTY || contentResolver == null) {
			callback.onTransactionError(TransactionType.DB_BULK_INSERT);
			return;
		}

		final String jsonDataString = data.getString(FeedService.DATA_RESULT);

		if (jsonDataString == null) {
			callback.onTransactionError(TransactionType.DB_BULK_INSERT);
			return;
		}

		// Notify the View we're working
		callback.onTransactionProgress(TransactionType.DB_BULK_INSERT);

		try {
			final int rowCount = contentResolver.bulkInsert(
			//final int rowCount = mFeedProvider.bulkInsert(
					FeedContract.ItemsEntry.CONTENT_BULK_INSERT_URI,
					FeedItemDTO.fromJsonStringAsContentValues(jsonDataString)
			);

			if (rowCount > 0) {
				callback.onTransactionSuccess(TransactionType.DB_BULK_INSERT, Bundle.EMPTY);
				contentResolver.notifyChange(FeedContract.ItemsEntry.CONTENT_BULK_INSERT_URI, null);
			} else {
				callback.onTransactionError(TransactionType.DB_BULK_INSERT);
			}
		} catch (JSONException e) {
			callback.onTransactionError(TransactionType.DB_BULK_INSERT);
			e.printStackTrace();
		}
//		final DbHelper dbHelper = DbHelper.getInstance(app);
//
//		// Notify the View we're still working
//		callback.onTransactionProgress(TransactionType.DB_BULK_INSERT);
//
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					dbHelper.bulkInsertItems(
//							FeedItemDTO.fromJsonStringAsList(jsonDataString),
//							new DbHelper.TransactionEvent() {
//								@Override
//								public void onDbCursorReady(Object data) {
//									callback.onTransactionSuccess(
//											TransactionType.DB_BULK_INSERT,
//											Bundle.EMPTY
//									);
//								}
//
//								@Override
//								public void onDbError() {
//									callback.onTransactionError(TransactionType.DB_BULK_INSERT);
//								}
//							}
//					);
//				} catch (JSONException e) {
//					e.printStackTrace();
//					callback.onTransactionError(TransactionType.DB_BULK_INSERT);
//				}
//			}
//		}).start();
//		try {
//			dbHelper.bulkInsertItems(
//					FeedItemDTO.fromJsonStringAsList(jsonDataString),
//					new DbHelper.TransactionEvent() {
//						@Override
//						public void onDbCursorReady(Object data) {
//							callback.onTransactionSuccess(
//									TransactionType.DB_BULK_INSERT,
//									Bundle.EMPTY
//							);
//						}
//
//						@Override
//						public void onDbError() {
//							callback.onTransactionError(TransactionType.DB_BULK_INSERT);
//						}
//					}
//			);
//		} catch (JSONException e) {
//			e.printStackTrace();
//			callback.onTransactionError(TransactionType.DB_BULK_INSERT);
//		}
	}

	public void requestItemsFromDb(final AppCompatActivity app,
	                               final DatabaseEvent callback) {
		if (mSelectAllDbCursor != null) {
			callback.onDbCursorReady(mSelectAllDbCursor);
			return;
		}

		final DbHelper dbHelper = DbHelper.getInstance(app);

		if (dbHelper == null) {
			callback.onDbError();
			return;
		}

		// TODO: use CursorLoader to query the provider
		mSelectAllDbCursor = dbHelper.selectAll();

		if (mSelectAllDbCursor == null) {
			callback.onDbError();
			return;
		}

		callback.onDbCursorReady(mSelectAllDbCursor);
	}

	public void requestItemsFromDb(final AppCompatActivity app) {
		final DatabaseEvent callback = (DatabaseEvent) app;
		final ContentResolver contentResolver = app.getContentResolver();

		if (contentResolver == null) {
			callback.onDbError();
			return;
		}

		final LoaderManager loaderManager = app.getSupportLoaderManager();

		if (loaderManager == null) {
			callback.onDbError();
			return;
		}

		final LoaderHelper loaderHelper = new LoaderHelper(app, FeedContract.ItemsEntry.CONTENT_URI);
		loaderManager.initLoader(1, null, loaderHelper);

//		final Cursor cursor = contentResolver.query(
//				FeedContract.ItemsEntry.CONTENT_URI, null, null, null, null
//		);
//
//		if (cursor == null) {
//			callback.onDbError();
//			return;
//		}
//
//		callback.onDbCursorReady(cursor);
	}

	public void requestFavoriteColumnUpdate(final AppCompatActivity app,
	                                        final FeedItemDTO feedItemDTO,
	                                        final DatabaseEvent callback) {
		final DbHelper dbHelper = DbHelper.getInstance(app);

		if (dbHelper == null) {
			callback.onDbError();
			return;
		}

		dbHelper.updateFavoriteColumn(feedItemDTO, new DbHelper.OnTransactionComplete() {
			@Override
			public void onSuccess(Object data) {
				requestItemsFromDb(app, callback);
			}

			@Override
			public void onError() {
				callback.onDbError();
			}
		});
	}

	public static enum TransactionType {
		FEED_SERVICE,
		DB_BULK_INSERT
	}

	public interface TransactionEvent {
		void onTransactionSuccess(TransactionType type, Bundle data);
		void onTransactionProgress(TransactionType type);
		void onTransactionError(TransactionType type);
	}

	public interface DatabaseEvent {
		void onDbCursorReady(Cursor cursor);
		void onDbError();
	}

	private class LoaderHelper implements LoaderManager.LoaderCallbacks<Cursor> {
		private Context mAppContext  = null;
		private Uri mSelectQueryUri  = null;
		private DatabaseEvent mEvent = null;

		public LoaderHelper(AppCompatActivity app, Uri mSelectQueryUri) {
			this.mAppContext = app.getApplicationContext();
			this.mSelectQueryUri = mSelectQueryUri;
			this.mEvent = (DatabaseEvent) app;
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(
					mAppContext,
					mSelectQueryUri,
					null, null, null, null
			);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mEvent.onDbCursorReady(data);

			mAppContext = null;
			mSelectQueryUri = null;
			mEvent = null;
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	}
}
