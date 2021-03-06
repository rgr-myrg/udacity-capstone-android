package net.usrlib.pocketbuddha.ui;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.usrlib.pattern.TinyEvent;
import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;
import net.usrlib.pocketbuddha.mvp.MvpPresenter;

/**
 * Created by rgr-myrg on 6/8/16.
 */
public class DetailFragment extends Fragment {
	public static final String NAME = DetailFragment.class.getSimpleName();
	private MvpModel mData = null;
	private View mRootView = null;

	private TinyEvent.Listener mListener = new TinyEvent.Listener() {
		@Override
		public void onSuccess(Object data) {
			final MvpModel mvpModel = (MvpModel) data;
			if (mvpModel.getTitle().equals(mData.getTitle())) {
				Log.d("FRAGMENT", "onSuccess " + mData.toString());

				mData = mvpModel;
			}
		}
	};

	public DetailFragment() {}

	public static DetailFragment newInstance(final MvpModel data) {
		final DetailFragment fragment = new DetailFragment();
		fragment.mData = data;

		Log.d("FRAGMENT", fragment.mData.getTitle() + " isFavorite: " + fragment.mData.isFavorite());

		MvpPresenter.getInstance().OnRequestItemUpdateComplete.addListener(fragment.mListener);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
		setRetainInstance(true);

		if (savedInstanceState != null) {
			mData = savedInstanceState.getParcelable(MvpModel.NAME);
			Log.d("FRAGMENT",mData.getTitle() + " isFavorite: " + mData.isFavorite());
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.home_detail_view, container, false);

		final Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
		final Bundle bundle = getArguments();

		if (bundle != null) {
			// Don't need navigation icon for Tablet split layout.
			// If HomeAdapter is true, we're in a split layout.
			if (bundle.getBoolean(HomeAdapter.NAME, false)) {
				toolbar.setNavigationIcon(null);
			}

			final MvpModel mvpModel = (MvpModel) bundle.getParcelable(MvpModel.NAME);

			if (mvpModel != null) {
				mData = mvpModel;
			}
		} else {
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getBaseActivity().onBackPressed();
				}
			});
		}

		bindDataToView(mRootView, mData);

		return mRootView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(MvpModel.NAME, mData);
	}

	public void bindDataToView(final View view, final MvpModel data) {
		if (data == null) {
			return;
		}

		final ImageView image  = (ImageView) view.findViewById(R.id.detail_item_image);
		final ImageView icon   = (ImageView) view.findViewById(R.id.detail_item_favorite_icon);
		final TextView header  = (TextView) view.findViewById(R.id.detail_item_header);
		final TextView pali    = (TextView) view.findViewById(R.id.detail_item_pali);
		final TextView english = (TextView) view.findViewById(R.id.detail_item_english);

		final BaseActivity parent = getBaseActivity();
		parent.setTitle(data.getTitle());

		Glide.with(this).load(data.getImageUrl()).into(image);

		final int iconResource = data.isFavorite()
				? R.drawable.ic_star_black_36dp
				: R.drawable.ic_star_border_black_36dp;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			icon.setImageDrawable(parent.getDrawable(iconResource));
		} else {
			icon.setImageBitmap(
					BitmapFactory.decodeResource(getResources(), iconResource)
			);
		}

		header.setText(Html.fromHtml(data.getTitle()));
		pali.setText(Html.fromHtml(data.getPali()));
		english.setText(Html.fromHtml(data.getEnglish()));
	}

	private BaseActivity getBaseActivity() {
		return (BaseActivity) getActivity();
	}
}