package net.usrlib.pocketbuddha.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.presenter.Presenter;

/**
 * Created by rgr-myrg on 6/6/16.
 */
public class SplashScreenActivity extends AppCompatActivity {
	private Presenter mPresenter = new Presenter();
	private final int POST_DELAY = 500;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		initPresenterAndStartFeedService();
	}

	private void initPresenterAndStartFeedService() {
		mPresenter.requestFeedService(this, new Presenter.OnTransactionComplete() {
			@Override
			public void onSuccess() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						onFeedServiceSuccess();
					}
				}, POST_DELAY);
			}

			@Override
			public void onProgress() {
				onFeedServiceProgress();
			}

			@Override
			public void onError() {
				onFeedServiceError();
			}
		});
	}

	private void onFeedServiceProgress() {
		final TextView textView = (TextView) findViewById(R.id.splash_screen_progress_msg);

		if (textView != null) {
			textView.setText(getString(R.string.splash_screen_finished_msg));
		}
	}

	private void onFeedServiceSuccess() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				startActivity(
						new Intent(getBaseContext(), HomeActivity.class)
				);

				finish();
			}
		});
	}

	private void onFeedServiceError() {
		final TextView textView = (TextView) findViewById(R.id.splash_screen_progress_msg);

		if (textView != null) {
			textView.setText(getString(R.string.splash_screen_error_msg));
		}
	}
}
