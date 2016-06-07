package net.usrlib.pocketbuddha.util;

/**
 * Created by rgr-myrg on 6/2/16.
 */
public class HttpResponse implements HttpRequest.Delegate {
	private HttpRequest mHttpRequest;
	private OnFetchComplete mOnFetchListener;
	private boolean mIsLoading = false;

	public void fetchWithUrl(final String url, final OnFetchComplete listener) {
		if (mIsLoading) {
			return;
		}

		if (url == null) {
			listener.onError();
			return;
		}

		mOnFetchListener = listener;
		mIsLoading = true;

		mHttpRequest = new HttpRequest(this);
		mHttpRequest.fetchWithUrl(url);
	}

	@Override
	public void onPostExecuteComplete(Object object) {
		if (object == null) {
			mIsLoading = false;
			mOnFetchListener.onError();
			return;
		}

		mIsLoading = false;
		mOnFetchListener.onSuccess(object);
	}

	@Override
	public void onError(String message) {
		mOnFetchListener.onError();
	}

	public interface OnFetchComplete {
		void onSuccess(Object object);
		void onError();
	}
}
