package net.usrlib.pocketbuddha.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.util.BitmapUtil;

/**
 * Created by rgr-myrg on 6/7/16.
 */
public class HomeAdapter extends RecyclerView.Adapter {
	private LayoutInflater mInflater = null;
	private BaseFragment mFragment = null;
	private Context mContext = null;
	private Cursor mCursor = null;

	public HomeAdapter(final BaseFragment fragment, final Context mContext, final Cursor cursor) {
		this.mInflater = LayoutInflater.from(mContext);
		this.mFragment = fragment;
		this.mContext  = mContext;
		this.mCursor   = cursor;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(
				mInflater.inflate(
						R.layout.home_recycler_item,
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

	private class ViewHolder extends RecyclerView.ViewHolder {
		private CardView cardView;
		private ImageView itemImage;
		private TextView itemTitle;

		public ViewHolder(final View view) {
			super(view);

			cardView  = (CardView) view.findViewById(R.id.home_recycler_item_card_view);
			itemImage = (ImageView) view.findViewById(R.id.item_image);
			itemTitle = (TextView) view.findViewById(R.id.item_title);
		}

		public void bindData(final MvpModel data) {
			Glide.with(mContext)
					.load(data.getImageUrl())
					.asBitmap().centerCrop()
					.into(
							new BitmapImageViewTarget(itemImage) {
								@Override
								public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
									super.onResourceReady(bitmap, anim);
									onBitmapLoaded(bitmap, itemTitle);
								}
							}
					);

			itemTitle.setText(data.getTitle());

			cardView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startDetailActivity(getAdapterPosition());
				}
			});
		}
	}

	private void startDetailActivity(int position) {
		if (!mFragment.isTablet()) {
			final Intent intent = new Intent(mContext, DetailActivity.class);

			intent.putExtra(MvpModel.POSITION_KEY, position);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			mContext.startActivity(intent);
		} else {
			final Bundle bundle = new Bundle();
			bundle.putParcelable(MvpModel.NAME, getItem(position));

			final DetailFragment fragment = new DetailFragment();
			fragment.setArguments(bundle);

			mFragment.getFragmentActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.tablet_detail_container, fragment, DetailFragment.NAME)
					.commit();
		}
	}

	private void onBitmapLoaded(final Bitmap bitmap, final TextView textView) {
		final int color = BitmapUtil.getColorFromBitmap(bitmap);

		if (color == -1) {
			return;
		}

		textView.setBackgroundColor(color);
	}
}