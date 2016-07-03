package net.usrlib.pocketbuddha.util;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;

/**
 * Created by rgr-myrg on 7/1/16.
 */
public class TrackerUtil extends Application {
	public static final String ACTION_CATEGORY_KEY = "Action";
	public static final String QUERY_SEARCH_KEY  = "SearchQuery";
	public static final String TITLE_SEARCH_KEY  = "SearchTitle";
	public static final String SHARE_ACTION_KEY  = "ShareIt";
	public static final String LIKED_ACTION_KEY  = "LikedIt";
	public static final String PLAYED_ACTION_KEY = "PlayedIt";
	private Tracker mTracker;

	synchronized public Tracker getDefaultTracker() {
		if (mTracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			mTracker = analytics.newTracker("GlobalTracker");
		}
		return mTracker;
	}

	public static final synchronized void trackSearchQuery(final Application app, final String query) {
		trackEvent(app, QUERY_SEARCH_KEY, query);
	}

	public static final synchronized void trackTitleSearch(final Application app, final String query) {
		trackEvent(app, TITLE_SEARCH_KEY, query);
	}

	public static final synchronized void trackShareItClicked(final Application app, final String title) {
		trackEvent(app, SHARE_ACTION_KEY, title);
	}

	public static final synchronized void trackFavoriteItClicked(final Application app, final String title) {
		trackEvent(app, LIKED_ACTION_KEY, title);
	}

	public static final synchronized void trackPlayItClicked(final Application app, final String title) {
		trackEvent(app, PLAYED_ACTION_KEY, title);
	}

	public static final synchronized void trackEvent(final Application app,
													 final String action,
													 final String value) {
		final TrackerUtil application = (TrackerUtil) app;

		application.getDefaultTracker().send(
				new HitBuilders.EventBuilder()
						.setCategory(ACTION_CATEGORY_KEY)
						.setAction(action)
						.setLabel(value)
						.setValue(1)
						.build()
		);
	}

	public static final synchronized void trackScreen(final Application app,
	                                                  final String name,
	                                                  final String referrer) {
		final TrackerUtil application = (TrackerUtil) app;
		final Tracker tracker = application.getDefaultTracker();

		tracker.setScreenName(name);
		tracker.setReferrer(referrer);
		tracker.send(new HitBuilders.ScreenViewBuilder().build());
	}
}
