package net.usrlib.pocketbuddha.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import net.usrlib.pocketbuddha.R;

import java.util.Calendar;

/**
 * Created by rgr-myrg on 6/20/16.
 */
public class WidgetProvider extends AppWidgetProvider {
	public static final String ACTION_SCHEDULED_UPDATE = "net.usrlib.pocketbuddha.widget.SCHEDULED_UPDATE";

//	private Context mContext = null;
//	private int[] mAppWidgetIds;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_SCHEDULED_UPDATE)) {
			final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
					new ComponentName(context, WidgetProvider.class)
			);

			onUpdate(
					context,
					appWidgetManager,
					appWidgetIds
			);
		}

		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//		mContext = context;
//		mAppWidgetIds = appWidgetIds;

		for (int appWidgetId : appWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(
					context.getPackageName(),
					R.layout.widget_layout
			);

			Intent intent = new Intent(context, WidgetRemoteService.class);

			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

			remoteViews.setRemoteAdapter(R.id.widget_list, intent);
			remoteViews.setEmptyView(R.layout.widget_layout, R.id.widget_list_empty_view);

			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}

		scheduleNextUpdate(context);

//		for (int appWidgetId : appWidgetIds) {
//			final Intent intent = new Intent(context, WidgetProvider.class);
//
//			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//
//			final PendingIntent pendingIntent = PendingIntent.getBroadcast(
//					context,
//					0,
//					intent,
//					PendingIntent.FLAG_UPDATE_CURRENT
//			);
//
//			mRemoteViews.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
//			mRemoteViews.setRemoteAdapter(R.id.widget_list, intent);
//			appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
//		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/*
	 * Invoked by MvpPresenter when requestPaliWord() returns a cursor from the content provider.
	 */
//	@Override
//	public void onTransactionCursorReady(Cursor cursor) {
//		final MvpModel.Dictionary data = MvpModel.Dictionary.fromDbCursor(cursor);
//		final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
////		final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
////				new ComponentName(mContext, WidgetProvider.class)
////		);
//
//		Log.d("WIDGET", data.getTerm());
//		for (int widgetId : mAppWidgetIds) {
//			Log.d("WIDGET", "widgetId: " + widgetId);
//
//			RemoteViews remoteViews = new RemoteViews(
//					mContext.getPackageName(),
//					R.layout.widget_layout
//			);
//
//			Intent intent = new Intent(mContext, WidgetRemoteService.class);
//
//			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//
//			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, mAppWidgetIds);
//			intent.putExtra(MvpModel.Dictionary.NAME, data.toContentValues());
//
//			remoteViews.setRemoteAdapter(R.id.widget_list, intent);
//			remoteViews.setEmptyView(R.layout.widget_layout, R.id.widget_list_empty_view);
//
//			appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.widget_list);
//			appWidgetManager.updateAppWidget(widgetId, remoteViews);
//		}
//
//		super.onUpdate(mContext, appWidgetManager, mAppWidgetIds);
//	}
//
//	@Override
//	public void onTransactionProgress(MvpPresenter.TransactionType type) {
//	}
//
//	@Override
//	public void onTransactionSuccess(MvpPresenter.TransactionType type, Bundle data) {
//	}
//
//	@Override
//	public void onTransactionError(MvpPresenter.TransactionType type) {
//	}
//
//	@Override
//	public void requestTransaction(Bundle data) {
//	}

	/*
	 * Solution for updating widget at midnight.
	 * http://stackoverflow.com/questions/27422007/update-android-widget-at-midnight
	 */
	private void scheduleNextUpdate(final Context context) {
		final Calendar calendar = Calendar.getInstance();

//		calendar.set(Calendar.HOUR_OF_DAY, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 1);
//		calendar.set(Calendar.MILLISECOND, 0);
//		calendar.add(Calendar.DAY_OF_YEAR, 1);

		calendar.set(Calendar.MILLISECOND, 60000);

		final Intent intent = new Intent(context, WidgetProvider.class);
		intent.setAction(ACTION_SCHEDULED_UPDATE);

		final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(
				Context.ALARM_SERVICE
		);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			alarmManager.set(
					AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(),
					pendingIntent
			);
		} else {
			alarmManager.setExact(
					AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(),
					pendingIntent
			);
		}
	}
}
