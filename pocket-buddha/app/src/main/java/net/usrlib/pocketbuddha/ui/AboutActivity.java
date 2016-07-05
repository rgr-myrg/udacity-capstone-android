package net.usrlib.pocketbuddha.ui;

import android.os.Bundle;
import android.webkit.WebView;

import net.usrlib.pocketbuddha.BuildConfig;
import net.usrlib.pocketbuddha.R;

/**
 * Created by rgr-myrg on 6/11/16.
 */
public class AboutActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initContentView(R.layout.about_activity);

		setWebView();
		setAdView();
	}

	private void setWebView() {
		final WebView webView = (WebView) findViewById(R.id.about_web_view);

		webView.setWebViewClient(new BaseWebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(BuildConfig.POCKET_BUDDHA_ABOUT_URL);
	}
}
