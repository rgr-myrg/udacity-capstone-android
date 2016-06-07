package net.usrlib.pocketbuddha.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.model.FeedItemDTO;
import net.usrlib.pocketbuddha.service.FeedReceiver;
import net.usrlib.pocketbuddha.service.FeedService;

import java.util.ArrayList;

/**
 * Created by rgr-myrg on 6/6/16.
 */
public class SplashScreenActivity extends AppCompatActivity {
	private FeedReceiver mFeedReceiver;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		mFeedReceiver = new FeedReceiver(new Handler());
		mFeedReceiver.setCallback(new FeedReceiver.OnReceiveResult() {
			@Override
			public void onComplete(int resultCode, Bundle resultData) {
				Log.d("SPLASH","OnReceiveResult");
				if (resultData == null) {
					return;
				}

				final ArrayList<FeedItemDTO> items = resultData.getParcelableArrayList(FeedReceiver.DATA);
				//Log.d("SPLASH","OnReceiveResult: " + items.toString());

			}
		});

		final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, FeedService.class);
		intent.putExtra(FeedReceiver.NAME, mFeedReceiver);
		intent.putExtra(FeedService.COMMAND_KEY, FeedService.FETCH_CMD);

		startService(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mFeedReceiver == null) {
			return;
		}

		mFeedReceiver.setCallback(null);
	}
}
