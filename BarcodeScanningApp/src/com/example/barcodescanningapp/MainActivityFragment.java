package com.example.barcodescanningapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivityFragment extends Fragment implements OnClickListener {

	private Button mLoginButton;
	private Button mMyCarsButton;
	private Button mScanButton;
	private Button mSelectButton;
	private User mUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mUser = User.get();
		File.loadUser(getActivity());


	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		super.onCreateView(inflater, parent, savedInstanceState); 
		View v = inflater.inflate(R.layout.activity_main, parent, false);
		mLoginButton = (Button)v.findViewById(R.id.login_button);
		mMyCarsButton = (Button)v.findViewById(R.id.my_cars_button);
		mScanButton = (Button)v.findViewById(R.id.scan_button);
		mSelectButton = (Button)v.findViewById(R.id.select_car_button);
		mScanButton.setOnClickListener(this);
		mLoginButton.setOnClickListener(this);
		mMyCarsButton.setOnClickListener(this);
		mSelectButton.setOnClickListener(this);

		return v;
	}

	@Override
	public void onClick(View v){
		((MainActivity)getActivity()).buttonPress(v.getId());
	}
	
	
	@Override
	public void onResume(){
		super.onResume(); 
		refreshView();
	}
	

	private void refreshView(){
		mLoginButton.setVisibility(mUser.isLoggedIn() ? View.GONE : View.VISIBLE);
		mScanButton.setText(mUser.hasCars() ? R.string.scan_another : R.string.scan);
		mMyCarsButton.setVisibility(mUser.hasCars() ? View.VISIBLE : View.GONE);
		
	}

}
