package net.usrlib.pocketbuddha.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.usrlib.pocketbuddha.R;
import net.usrlib.pocketbuddha.mvp.MvpModel;

/**
 * Created by rgr-myrg on 6/8/16.
 */
public class DetailFragment extends Fragment {
	private MvpModel mData = null;
	private View mRootView = null;

	public DetailFragment() {}

	public static DetailFragment newInstance(final MvpModel data) {
		final DetailFragment fragment = new DetailFragment();
		fragment.mData = data;

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.home_detail_view, container, false);

		Toolbar toolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getParentActivity().onBackPressed();
			}
		});

		getParentActivity().bindDataToView(mRootView, mData);

		return mRootView;
	}

	private DetailActivity getParentActivity() {
		return (DetailActivity) getActivity();
	}
}