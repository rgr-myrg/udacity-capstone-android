package net.usrlib.pocketbuddha.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import net.usrlib.pocketbuddha.data.DbHelper;
import net.usrlib.pocketbuddha.model.FeedItemDTO;
import net.usrlib.pocketbuddha.service.FeedReceiver;
import net.usrlib.pocketbuddha.service.FeedService;

import org.json.JSONException;

/**
 * Created by rgr-myrg on 6/7/16.
 */
public class Presenter {
	private FeedReceiver mFeedReceiver = null;

	public void requestFeedService(final AppCompatActivity app, final OnTransactionComplete callback) {
		mFeedReceiver = new FeedReceiver(new Handler());

		mFeedReceiver.setCallback(new FeedReceiver.OnReceiveResult() {
			@Override
			public void onComplete(int resultCode, Bundle resultData) {
				switch (resultCode) {
					case FeedService.FINISHED:
						onFeedServiceComplete(
								app,
								callback,
								resultData.getString(FeedService.DATA_RESULT)
						);
						break;

					case FeedService.ERROR:
						callback.onError();
						break;
				}
			}
		});

		final Intent intent = new Intent(Intent.ACTION_SYNC, null, app, FeedService.class);

		intent.putExtra(FeedReceiver.NAME, mFeedReceiver);
		intent.putExtra(FeedService.COMMAND_KEY, FeedService.FETCH_COMMAND);

		app.startService(intent);
	}

	public void onFeedServiceComplete(final AppCompatActivity app,
	                                  final OnTransactionComplete callback,
	                                  final String jsonString) {
		final DbHelper dbHelper = new DbHelper(app.getApplicationContext());

		// Notify the View we're still working
		callback.onProgress();

		try {
			dbHelper.bulkInsertItems(
					FeedItemDTO.fromJsonStringAsList(jsonString),
					new DbHelper.OnTransactionComplete() {
						@Override
						public void onSuccess(Object data) {
							callback.onSuccess();
						}

						@Override
						public void onError() {
							callback.onError();
						}
					}
			);
		} catch (JSONException e) {
			e.printStackTrace();
			callback.onError();
		}
	}

	public interface OnTransactionComplete {
		void onSuccess();

		void onProgress();

		void onError();
	}
}
