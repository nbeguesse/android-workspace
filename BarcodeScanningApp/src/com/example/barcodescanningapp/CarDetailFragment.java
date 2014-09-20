package com.example.barcodescanningapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Car detail screen. This fragment is either
 * contained in a {@link CarListActivity} in two-pane mode (on tablets) or a
 * {@link CarDetailActivity} on handsets.
 */
public class CarDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Car mCar;
	private User mUser;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CarDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = User.get(getActivity());
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			//mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
			int car_id = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
			mCar = mUser.getCar(car_id);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_car_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mCar != null) {
			((TextView) rootView.findViewById(R.id.car_detail))
					.setText(mCar.toString());
		} 

		return rootView;
	}
}
