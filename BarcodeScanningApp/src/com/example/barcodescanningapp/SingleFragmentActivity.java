package com.example.barcodescanningapp;


import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import com.example.barcodescanningapp.R;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public abstract class SingleFragmentActivity extends FragmentActivity {

	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		User user = User.get();
		//Hide login option if logged in
		menu.findItem(R.id.login_button).setVisible(!user.isLoggedIn());
		//Hide logout option if logged out
		menu.findItem(R.id.logout_button).setVisible(user.isLoggedIn());
		//Set custom title for VIN Scanner
	    menu.findItem(R.id.scan_button).setTitle(user.hasCars() ? R.string.scan_another : R.string.scan);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		buttonPress(item.getItemId());
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ZBAR_SCANNER_REQUEST:
			case ZBAR_QR_SCANNER_REQUEST:
				if (resultCode == Activity.RESULT_OK) {
					Toast.makeText(getApplicationContext(), "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
				} else if(resultCode == Activity.RESULT_CANCELED && data != null) {
					String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
					if(!TextUtils.isEmpty(error)) {
						Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
					}
				}
				break;
		}
	}
	
	public void onClick(View v){
		buttonPress(v.getId());
	}
	
	public void buttonPress(int id){
		
		//scan with ZBAR LIBRARY
		if(id==R.id.scan_button){
			Intent intent = new Intent(getApplicationContext(), ZBarScannerActivity.class);
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
			
		//Launch Login Activity
		} else if (id==R.id.login_button) {
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
			
		//Launch Car List Activity
		} else if(id == R.id.my_cars_button){
			Intent intent = new Intent(getApplicationContext(), CarListActivity.class);
			startActivity(intent);	
			
		//Logout and return Home
		} else if(id == R.id.logout_button){
			File.erase(getApplicationContext());
			User.get().logOut();
			Intent intent = new Intent(this, MainActivity.class);
	        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
	        startActivity(intent);
	        finish();
			
		}
	}
	

	
	

}
