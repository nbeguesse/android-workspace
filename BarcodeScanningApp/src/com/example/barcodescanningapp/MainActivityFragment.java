package com.example.barcodescanningapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

public class MainActivityFragment extends Fragment implements OnClickListener {
	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;

	private Button mLoginButton;
	private Button mMyCarsButton;
	private Button mScanButton;
	private User mUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mUser = User.get(getActivity());


	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		super.onCreateView(inflater, parent, savedInstanceState); 
		View v = inflater.inflate(R.layout.activity_main, parent, false);
		mLoginButton = (Button)v.findViewById(R.id.login_button);
		mMyCarsButton = (Button)v.findViewById(R.id.my_cars_button);
		mScanButton = (Button)v.findViewById(R.id.scan_button);
		mScanButton.setOnClickListener(this);
		mLoginButton.setOnClickListener(this);
		mMyCarsButton.setOnClickListener(this);
		
		//refreshView();
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		//Hide login option if logged in
		menu.findItem(R.id.login_button).setVisible(!mUser.isLoggedIn());
		//Hide logout option if logged out
		menu.findItem(R.id.logout_button).setVisible(mUser.isLoggedIn());
		//Set custom title for VIN Scanner
	    menu.findItem(R.id.scan_button).setTitle(mUser.hasCars() ? "Scan Another VIN" : "Scan VIN");
	}
	
	public void onClick(View v){
		buttonPress(v.getId());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		buttonPress(item.getItemId());
		return true;
	}
	
	public void buttonPress(int id){
		if(id==R.id.scan_button){
			//scan
			//ZBAR LIBRARY
			Intent intent = new Intent(getActivity(), ZBarScannerActivity.class);
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		} else if (id==R.id.login_button) {
			//Launch Login Activity
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		} else if(id == R.id.my_cars_button){
			//Launch Car List Activity
			Intent intent = new Intent(getActivity(), CarListActivity.class);
			startActivity(intent);			
		}		
	}
	
	@Override
	public void onResume(){
		super.onResume(); 
		refreshView();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ZBAR_SCANNER_REQUEST:
			case ZBAR_QR_SCANNER_REQUEST:
				if (resultCode == Activity.RESULT_OK) {
					Toast.makeText(getActivity(), "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
				} else if(resultCode == Activity.RESULT_CANCELED && data != null) {
					String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
					if(!TextUtils.isEmpty(error)) {
						Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
					}
				}
				break;
		}
	}

	

	
	private void refreshView(){
		mLoginButton.setVisibility(mUser.isLoggedIn() ? View.GONE : View.VISIBLE);
		mScanButton.setText(mUser.hasCars() ? R.string.scan_another : R.string.scan);
		mMyCarsButton.setVisibility(mUser.hasCars() ? View.VISIBLE : View.GONE);
		
	}

}
