package net.usrlib.pocketbuddha.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import net.usrlib.material.MaterialTheme;
import net.usrlib.material.Theme;
import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;
import net.usrlib.pocketbuddha.mvp.MvpView;

/**
 * Created by rgr-myrg on 5/19/16.
 */
public class FavoritesAdapter extends RecyclerView.Adapter {
	private final MaterialTheme mMaterialTheme = MaterialTheme.get(Theme.TOPAZ);
	private final ViewBinderHelper mBinderHelper = new ViewBinderHelper();

	private LayoutInflater mInflater = null;
	private Context mContext = null;
	private Cursor mCursor = null;

	public FavoritesAdapter(final Context mContext, final Cursor cursor) {
		this.mInflater = LayoutInflater.from(mContext);
		this.mContext = mContext;
		this.mCursor = cursor;

		// Allow Swipe to reveal one row at a time
		mBinderHelper.setOpenOnlyOne(true);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(
				mInflater.inflate(
						R.layout.favorites_item_swipe_view,
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
			viewHolder.bindData(data.getTitle());

			// BinderHelper requires a unique key. Use title.
			mBinderHelper.bind(viewHolder.swipeLayout, data.getTitle());
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
		final Intent intent = new Intent(mContext, DetailActivity.class);

		// Let DetailActivity know it should request Favorites
		intent.putExtra(FavoritesActivity.NAME, true);
		intent.putExtra(MvpModel.POSITION_KEY, position);

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		mContext.startActivity(intent);
	}

	private void requestUpdateTransaction(final int position) {
		final MvpModel data = getItem(position);
		data.setFavorite(false);

		final Bundle bundle = new Bundle();

		bundle.putParcelable(MvpModel.NAME, data);
		bundle.putString(
				MvpPresenter.TRANSACTION_TYPE_KEY,
				MvpPresenter.TransactionType.DB_UPDATE.toString()
		);

		((MvpView) mContext).requestTransaction(bundle);
	}

	private class ViewHolder extends RecyclerView.ViewHolder {
		private SwipeRevealLayout swipeLayout;
		private ImageView bookmarkDeleteIcon;
		private TextView bookmarkDescription;
		private TextView bookmarkCircle;

		public ViewHolder(View itemView) {
			super(itemView);

			swipeLayout = (SwipeRevealLayout) itemView.findViewById(R.id.favorites_item_swipe_layout);
			bookmarkDeleteIcon = (ImageView) itemView.findViewById(R.id.favorites_item_delete_icon);
			bookmarkDescription = (TextView) itemView.findViewById(R.id.favorites_item_description);
			bookmarkCircle = (TextView) itemView.findViewById(R.id.favorites_item_circle);
		}

		public void bindData(final String text) {
			bookmarkCircle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startDetailActivity(getAdapterPosition());
				}
			});

			bookmarkDescription.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startDetailActivity(getAdapterPosition());
				}
			});

			bookmarkDeleteIcon.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					requestUpdateTransaction(getAdapterPosition());
				}
			});

			bookmarkDescription.setText(text);

			bookmarkCircle.setText(String.valueOf(text.charAt(0)).toUpperCase());

			bookmarkCircle.getBackground().setColorFilter(
					Color.parseColor(mMaterialTheme.getNextColor().hex),
					PorterDuff.Mode.SRC
			);
		}
	}
}
