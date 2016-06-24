package net.usrlib.pocketbuddha.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;

/**
 * Created by rgr-myrg on 6/20/16.
 */
public class WidgetProvider extends AppWidgetProvider implements MvpView {
	private AppWidgetManager mAppWidgetManager = null;
	private Context mContext = null;
	private int[] mWidgetIds;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		mContext = context;
		mAppWidgetManager = appWidgetManager;
		mWidgetIds = appWidgetIds;

		MvpPresenter.getInstance().requestPaliWord(this, context);

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
//		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/*
	 * Invoked by MvpPresenter when requestPaliWord() returns a cursor from the content provider.
	 */
	@Override
	public void onTransactionCursorReady(Cursor cursor) {
		final MvpModel.Dictionary data = MvpModel.Dictionary.fromDbCursor(cursor);

		for (int widgetId : mWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(
					mContext.getPackageName(),
					R.layout.widget_layout
			);

			Intent intent = new Intent(mContext, WidgetRemoteService.class);

			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, mWidgetIds);
			intent.putExtra(MvpModel.Dictionary.NAME, data.toContentValues());

			remoteViews.setRemoteAdapter(R.id.widget_list, intent);
			remoteViews.setEmptyView(R.layout.widget_layout, R.id.widget_list_empty_view);

			mAppWidgetManager.updateAppWidget(widgetId, remoteViews);
		}

		super.onUpdate(mContext, mAppWidgetManager, mWidgetIds);
	}

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
	public void requestTransaction(Bundle data) {
	}
}
