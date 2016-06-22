package net.usrlib.pocketbuddha.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;

import java.util.Random;

/**
 * Created by rgr-myrg on 6/20/16.
 */
public class DailyWidgetProvider extends AppWidgetProvider implements MvpView {
	private RemoteViews mRemoteViews = null;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		mRemoteViews = new RemoteViews(
				context.getPackageName(),
				R.layout.widget_layout
		);

		MvpPresenter.getInstance().requestPaliWord(this, context);

		for (int appWidgetId : appWidgetIds) {
			final Intent intent = new Intent(context, DailyWidgetProvider.class);

			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

			final PendingIntent pendingIntent = PendingIntent.getBroadcast(
					context,
					0,
					intent,
					PendingIntent.FLAG_UPDATE_CURRENT
			);

			mRemoteViews.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.d("MAIN", "WIDGET RECEIVE");
	}

//	private void test(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//		final int count = appWidgetIds.length;
//
//		for (int i = 0; i < count; i++) {
//			int widgetId = appWidgetIds[i];
//			String number = String.format("%03d", (new Random().nextInt(900) + 100));
//
//			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
//					R.layout.widget_layout);
//			remoteViews.setTextViewText(R.id.daily_widget_text, number);
//
//			Intent intent = new Intent(context, DailyWidgetProvider.class);
//			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
//			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//					0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//			remoteViews.setOnClickPendingIntent(R.id.widget_button, pendingIntent);
//			appWidgetManager.updateAppWidget(widgetId, remoteViews);
//		}
//	}

	@Override
	public void onTransactionProgress(MvpPresenter.TransactionType type) {
	}

	@Override
	public void onTransactionSuccess(MvpPresenter.TransactionType type, Bundle data) {
	}

	@Override
	public void onTransactionError(MvpPresenter.TransactionType type) {
	}

	@Override
	public void onTransactionCursorReady(Cursor cursor) {
		bindDataToRemoteView(MvpModel.Dictionary.fromDbCursor(cursor));
	}

	@Override
	public void requestTransaction(Bundle data) {

	}

	private void bindDataToRemoteView(MvpModel.Dictionary data) {
		if (data == null) {
			Log.d("MAIN", "WIDGET bindDataToRemoteView data NULL!!!");
			return;
		}

		mRemoteViews.setTextViewText(R.id.daily_widget_text, data.getTerm());
		mRemoteViews.setTextViewText(R.id.daily_widget_text_description, data.getDefinition());
	}
}
