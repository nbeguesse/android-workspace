package com.example.barcodescanningapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivityFragment extends Fragment implements OnClickListener {

	//private Button mLoginButton;
	private Button mDmsButton;
	private Button mScanButton;
	private Button mSelectButton;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		File.loadUser(getActivity());


	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		super.onCreateView(inflater, parent, savedInstanceState); 
		View v = inflater.inflate(R.layout.activity_main, parent, false);
		mScanButton = (Button)v.findViewById(R.id.scan_button);
		mSelectButton = (Button)v.findViewById(R.id.select_car_button);
		mDmsButton = (Button)v.findViewById(R.id.dms_button);
		mScanButton.setOnClickListener(this);
		mDmsButton.setOnClickListener(this);
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

	
	}

}
