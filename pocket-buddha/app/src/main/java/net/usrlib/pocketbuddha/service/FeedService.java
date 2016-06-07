package net.usrlib.pocketbuddha.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import net.usrlib.pocketbuddha.BuildConfig;
import net.usrlib.pocketbuddha.model.FeedItemDTO;
import net.usrlib.pocketbuddha.util.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by home on 6/6/16.
 */
public class FeedService extends IntentService {
	public static final String URL = BuildConfig.FEED_PAGER_END_POINT + "?pg=1";
	public static final String COMMAND_KEY = "command";
	public static final String FETCH_CMD = "fetch";
	public static final int ERROR = -1;
	public static final int STARTED = 0;
	public static final int FINISHED = 1;

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
		final String command = intent.getStringExtra(COMMAND_KEY);

		if (command.equals(FETCH_CMD)) {
			mResultReceiver.send(STARTED, Bundle.EMPTY);
			startFetching();
		}
	}

	private void startFetching() {
		final HttpResponse httpResponse = new HttpResponse();
		final Bundle bundle = new Bundle();

		httpResponse.fetchWithUrl(URL, new HttpResponse.OnFetchComplete() {
			@Override
			public void onSuccess(Object object) {
				try {
					onFetchSuccess(new JSONArray((String) object));
				} catch (JSONException e) {
					bundle.putString(Intent.EXTRA_TEXT, e.toString());
					mResultReceiver.send(ERROR, bundle);

					e.printStackTrace();
				}
			}

			@Override
			public void onError() {
				bundle.putString(Intent.EXTRA_TEXT, "HttpResponse Error");
				mResultReceiver.send(ERROR, bundle);
			}
		});
	}

	private void onFetchSuccess(final JSONArray jsonArray) {
		final Bundle bundle = new Bundle();

		if (jsonArray == null || jsonArray.length() == 0 || jsonArray.isNull(0)) {
			bundle.putString(Intent.EXTRA_TEXT, "HttpResponse is Null");
			mResultReceiver.send(ERROR, bundle);
			return;
		}

		final ArrayList<FeedItemDTO> items = new ArrayList<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				items.add(
						FeedItemDTO.fromJsonObject(jsonArray.getJSONObject(i))
				);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		bundle.putParcelableArrayList(FeedReceiver.DATA, items);

		mResultReceiver.send(FINISHED, bundle);
	}
}
