package net.usrlib.pocketbuddha.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.presenter.Presenter;

/**
 * Created by rgr-myrg on 6/6/16.
 */
public class SplashScreenActivity extends AppCompatActivity
		implements Presenter.OnTransactionComplete {

	private TextView mMessageTextView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		mMessageTextView = (TextView) findViewById(R.id.splash_screen_message_textview);

		Presenter.getInstance().requestFeedService(this);

		Glide.with(this)
				.load(R.drawable.happy_monk_275x275)
				.into(((ImageView) findViewById(R.id.splash_screen_imageview)));
	}

	@Override
	public void onTransactionSuccess(Presenter.TransactionType type, Bundle bundle) {
		switch (type) {
			case FEED_SERVICE:
				Presenter.getInstance().requestDbBulkInsert(this, bundle);
				break;

			case DB_BULK_INSERT:
				displayMessage(R.string.splash_screen_finished_msg);
				onDbBulkInsertComplete();
				break;
		}
	}

	@Override
	public void onTransactionProgress(Presenter.TransactionType type) {
		displayMessage(R.string.splash_screen_progress_msg);
	}

	@Override
	public void onTransactionError(Presenter.TransactionType type) {
		displayMessage(R.string.splash_screen_error_msg);
	}

	private void onDbBulkInsertComplete() {
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

	private void displayMessage(final int msgId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mMessageTextView != null) {
					mMessageTextView.setText(getString(msgId));
				}
			}
		});
	}
}
