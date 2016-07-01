package net.usrlib.pocketbuddha.player;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by rgr-myrg on 6/8/16.
 */
public class SoundPlayer {
	private MediaPlayer mMediaPlayer = null;
	private boolean mIsLoaded = false;
	private boolean mIsPaused = false;
	private boolean mHasPlayStart = false;

	public void loadMp3StreamWithUrl(final String url, final OnPrepared callback) {
		if (url == null || !url.startsWith("http")) {
			callback.onError();
			return;
		}

		release();

		mMediaPlayer = null;
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		try {
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepareAsync();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mMediaPlayer.setOnPreparedListener(
				new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						mIsLoaded = true;
						callback.onReady();
					}
				}
		);

		mMediaPlayer.setOnCompletionListener(
				new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						mHasPlayStart = false;
						mIsPaused = false;

						mp.release();
						callback.onComplete();
					}
				}
		);

		mMediaPlayer.setOnErrorListener(
				new MediaPlayer.OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						mp.release();
						callback.onError();
						return false;
					}
				}
		);
	}

	public boolean isPlaying() {
		return mMediaPlayer == null || mHasPlayStart && mMediaPlayer.isPlaying();
	}

//	public boolean hasPlayStart() {
//		return mHasPlayStart;
//	}
//
//	public boolean isPaused() {
//		return mIsPaused;
//	}

	public void play() {
		if (mMediaPlayer == null || !mIsLoaded) {
			return;
		}

		mHasPlayStart = true;
		mIsPaused = false;
		mMediaPlayer.start();
	}

	public void pause() {
		if (mMediaPlayer == null) {
			return;
		}

		mIsPaused = true;
		mMediaPlayer.pause();
	}

//	public void togglePlaystate() {
//		if (isPlaying()) {
//			pause();
//		} else {
//			play();
//		}
//	}

	public void release() {
		//Release player resources if an instance already exists
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
		}
	}

	public interface OnPrepared {
		void onReady();
		void onComplete();
		void onError();
	}
}
