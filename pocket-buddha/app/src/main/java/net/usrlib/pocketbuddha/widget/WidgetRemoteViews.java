package net.usrlib.pocketbuddha.widget;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.provider.WordContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgr-myrg on 6/24/16.
 */
public class WidgetRemoteViews implements RemoteViewsService.RemoteViewsFactory {
	private List<ContentValues> mListItems = new ArrayList<>();
	private Context mContext = null;

	public WidgetRemoteViews(final Context mContext, final Intent intent) {
		this.mContext = mContext;

		if (intent.hasExtra(MvpModel.Dictionary.NAME)) {
			this.mListItems.add(
					(ContentValues) intent.getParcelableExtra(MvpModel.Dictionary.NAME)
			);
		}
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
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
				listItem.getAsString(WordContract.DictionaryEntry.PALI_TERM_COLUMN)
		);

		remoteView.setTextViewText(
				R.id.daily_widget_definition,
				listItem.getAsString(WordContract.DictionaryEntry.DEFINITION_COLUMN)
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
}
