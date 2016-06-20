package net.usrlib.pocketbuddha.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.usrlib.material.MaterialTheme;
import net.usrlib.material.Theme;
import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;

/**
 * Created by rgr-myrg on 6/4/16.
 */
public class SearchResultAdapter extends RecyclerView.Adapter {
	public static final String ITEM_POSITION_KEY = "searchResultItemPosition";
	private final MaterialTheme mMaterialTheme = MaterialTheme.get(Theme.TOPAZ);

	private LayoutInflater mInflater = null;
	private Context mContext = null;
	private Cursor mCursor = null;

	private String mSearchQuery = null;

	public SearchResultAdapter(final Context mContext, final Cursor cursor, final String query) {
		this.mInflater = LayoutInflater.from(mContext);
		this.mContext = mContext;
		this.mCursor = cursor;
		this.mSearchQuery = query;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(
				mInflater.inflate(
						R.layout.search_results_item,
						parent,
						false
				)
		);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
		final ViewHolder viewHolder = (ViewHolder) holder;
		final MvpModel data = getItem(position);

		if (data != null && position >= 0 && position < mCursor.getCount()) {
			viewHolder.bindData(data);
		}
	}

	@Override
	public int getItemCount() {
		return mCursor == null ? 0 : mCursor.getCount();
	}

	public void changeCursor(Cursor cursor) {
		Cursor swappedCursor = swapCursor(cursor);

		if (swappedCursor != null) {
			swappedCursor.close();
		}
	}

	private Cursor swapCursor(Cursor cursor) {
		if (mCursor == cursor) {
			return null;
		}

		final Cursor previousCursor = mCursor;

		mCursor = cursor;

		if (cursor != null) {
			this.notifyDataSetChanged();
		}

		return previousCursor;
	}

	private MvpModel getItem(final int position) {
		mCursor.moveToPosition(position);

		return MvpModel.fromDbCursor(mCursor);
	}

	private void startDetailActivity(final int position) {
		final Intent intent = new Intent(
				mContext.getApplicationContext(),
				DetailActivity.class
		);

		intent.putExtra(ITEM_POSITION_KEY, position);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		mContext.startActivity(intent);
	}

	private class ViewHolder extends RecyclerView.ViewHolder {
		private TextView textCircle;
		private TextView description;
		private TextView title;

		public ViewHolder(View itemView) {
			super(itemView);
			textCircle  = (TextView) itemView.findViewById(R.id.search_results_item_circle);
			description = (TextView) itemView.findViewById(R.id.search_results_item_description);
			title = (TextView) itemView.findViewById(R.id.search_results_item_title);

		}

		public void bindData(final MvpModel data) {
			final String descriptionText = data.getEnglish().replaceAll(
					 mSearchQuery,
					"<b><i>" + mSearchQuery + "</i></b>"
			);

			textCircle.setText(String.valueOf(data.getTitle().charAt(0)).toUpperCase());
			textCircle.getBackground().setColorFilter(
					Color.parseColor(mMaterialTheme.getNextColor().hex),
					PorterDuff.Mode.SRC
			);

			title.setText(data.getTitle());
			description.setText(Html.fromHtml(descriptionText));

			textCircle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startDetailActivity(getAdapterPosition());
				}
			});

			title.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startDetailActivity(getAdapterPosition());
				}
			});

			description.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startDetailActivity(getAdapterPosition());
				}
			});
		}
	}
}
