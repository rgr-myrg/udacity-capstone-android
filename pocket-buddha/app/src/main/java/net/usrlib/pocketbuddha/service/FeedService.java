package net.usrlib.pocketbuddha.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import net.usrlib.pocketbuddha.util.HttpResponse;

/**
 * Created by rgr-myrg on 6/6/16.
 */
public class FeedService extends IntentService {
	public static final String END_POINT_KEY = "endPointUrl";
	public static final String COMMAND_KEY   = "command";
	public static final String FETCH_COMMAND = "fetch";
	public static final String DATA_RESULT   = "jsonDataString";

	public static final int STARTED  = 0;
	public static final int FINISHED = 1;
	public static final int ERROR = -1;

	private ResultReceiver mResultReceiver;

	public FeedService() {
		super(null);
	}

	public FeedService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mResultReceiver = intent.getParcelableExtra(FeedReceiver.NAME);

		if (!intent.hasExtra(COMMAND_KEY) || !intent.hasExtra(END_POINT_KEY)) {
			mResultReceiver.send(ERROR, Bundle.EMPTY);
		}

		final String command  = intent.getStringExtra(COMMAND_KEY);
		final String endPoint = intent.getStringExtra(END_POINT_KEY);

		if (command.equals(FETCH_COMMAND)) {
			mResultReceiver.send(STARTED, Bundle.EMPTY);
			startFetching(endPoint);
		}
	}

	private void startFetching(final String url) {
		final HttpResponse httpResponse = new HttpResponse();
		final Bundle bundle = new Bundle();

		httpResponse.fetchWithUrl(url, new HttpResponse.OnFetchComplete() {
			@Override
			public void onSuccess(Object object) {
				bundle.putString(DATA_RESULT, (String) object);
				mResultReceiver.send(FINISHED, bundle);
			}

			@Override
			public void onError() {
				bundle.putString(Intent.EXTRA_TEXT, "HttpResponse Error");
				mResultReceiver.send(ERROR, bundle);
			}
		});
	}
}
