package net.usrlib.pocketbuddha.service;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

/**
 * Created by home on 6/6/16.
 */
@SuppressLint("ParcelCreator")
public class FeedReceiver extends ResultReceiver {
	public static final String NAME = FeedReceiver.class.getSimpleName();

	private OnReceiveResult mCallback;

	public FeedReceiver(Handler handler) {
		super(handler);
	}

	public void setCallback(OnReceiveResult callback) {
		this.mCallback = callback;
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		super.onReceiveResult(resultCode, resultData);

		if (mCallback == null) {
			return;
		}

		mCallback.onComplete(resultCode, resultData);
	}

	public interface OnReceiveResult {
		void onComplete(int resultCode, Bundle resultData);
	}
}
