package net.usrlib.pocketbuddha.widget;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;
import net.usrlib.pocketbuddha.provider.WordContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgr-myrg on 6/24/16.
 */
public class WidgetRemoteViews implements RemoteViewsService.RemoteViewsFactory, MvpView {
	private List<ContentValues> mListItems = null;
	private Context mContext = null;

	public WidgetRemoteViews(final Context context, final Intent intent) {
		this.mContext = context;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
		// Request new data from the provider
		MvpPresenter.getInstance().requestPaliWord(this, mContext);
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public int getCount() {
		return mListItems.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		final RemoteViews remoteView = new RemoteViews(
				mContext.getPackageName(),
				R.layout.widget_detail_list_item
		);

		ContentValues listItem = mListItems.get(position);

		remoteView.setTextViewText(
				R.id.daily_widget_term,
				Html.fromHtml(
						listItem.getAsString(WordContract.DictionaryEntry.PALI_TERM_COLUMN) + ":"
				)
		);

		remoteView.setTextViewText(
				R.id.daily_widget_definition,
				Html.fromHtml(
						listItem.getAsString(WordContract.DictionaryEntry.DEFINITION_COLUMN)
				)
		);

		return remoteView;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
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
	public void onTransactionCursorReady(Cursor cursor) {
		final MvpModel.Dictionary data = MvpModel.Dictionary.fromDbCursor(cursor);

		// Keep resetting the array since there's only one element on the widget
		mListItems = new ArrayList<>();
		mListItems.add(data.toContentValues());
	}

	@Override
	public void requestTransaction(Bundle data) {

	}
}
