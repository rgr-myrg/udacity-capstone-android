package net.usrlib.pocketbuddha;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by rgr-myrg on 7/1/16.
 */
public class AnalyticsApplication extends Application {
	public static final String ACTION_CATEGORY_KEY = "Action";
	public static final String QUERY_SEARCH_KEY = "SearchQuery";
	public static final String TITLE_SEARCH_KEY = "SearchTitle";
	private Tracker mTracker;

	synchronized public Tracker getDefaultTracker() {
		if (mTracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			mTracker = analytics.newTracker("GlobalTracker");
		}
		return mTracker;
	}

	public static final synchronized void trackSearchQuery(final Application app, final String query) {
		final AnalyticsApplication application = (AnalyticsApplication) app;

		application.getDefaultTracker().send(
				new HitBuilders.EventBuilder()
						.setCategory(ACTION_CATEGORY_KEY)
						.setAction(QUERY_SEARCH_KEY)
						.setLabel(query)
						.setValue(1)
						.build()
		);
	}

	public static final synchronized void trackTitleSearch(final Application app, final String query) {
		final AnalyticsApplication application = (AnalyticsApplication) app;

		application.getDefaultTracker().send(
				new HitBuilders.EventBuilder()
						.setCategory(ACTION_CATEGORY_KEY)
						.setAction(TITLE_SEARCH_KEY)
						.setLabel(query)
						.setValue(1)
						.build()
		);
	}
}
