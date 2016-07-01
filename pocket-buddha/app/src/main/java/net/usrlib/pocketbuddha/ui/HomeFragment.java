package net.usrlib.pocketbuddha.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.usrlib.pocketbuddha.R;

/**
 * Created by rgr-myrg on 6/26/16.
 */
public class HomeFragment extends Fragment implements BaseFragment {
	private View mRootView = null;
	private RecyclerView mRecyclerView = null;
	private HomeAdapter mRecyclerAdapter = null;
	private boolean mIsTablet = false;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
							 @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.home_recycler_view, container, false);

		return mRootView;
	}

	@Override
	public FragmentActivity getFragmentActivity() {
		return getActivity();
	}

	@Override
	public void onCursorReady(Cursor cursor) {
		initRecyclerViewAndAdapter(cursor);
		setIsTablet();
	}

	@Override
	public boolean isTablet() {
		return mIsTablet;
	}

	@Override
	public void setAdapterPosition(int position) {
		final BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity != null) {
			baseActivity.setAdapterPosition(position);
		}
	}

	private void initRecyclerViewAndAdapter(final Cursor cursor) {
		mRecyclerAdapter = new HomeAdapter(this, getContext(), cursor);
		mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.home_recycler_view_list);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(mRecyclerAdapter);
	}

	private void setIsTablet() {
		final BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity != null) {
			mIsTablet = baseActivity.isTablet();
		}
	}
}
