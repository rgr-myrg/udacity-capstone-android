package net.usrlib.pocketbuddha.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by rgr-myrg on 6/24/16.
 */
public class WidgetRemoteService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new WidgetRemoteViews(getApplicationContext(), intent);
	}
}
