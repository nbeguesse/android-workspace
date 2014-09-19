package com.example.barcodescanningapp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import android.text.TextUtils;

public class MainActivity extends Activity implements OnClickListener {
	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;

	private Button mLoginButton;
	private User mUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mUser = User.get(getApplicationContext());
		mLoginButton = (Button)findViewById(R.id.login_button);
		findViewById(R.id.scan_button).setOnClickListener(this);
		mLoginButton.setOnClickListener(this);
		refreshView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClick(View v){
		if(v.getId()==R.id.scan_button){
			//scan
			//ZBAR LIBRARY
			Intent intent = new Intent(this, ZBarScannerActivity.class);
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		} else if (v.getId()==R.id.login_button) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);

		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		refreshView();
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case ZBAR_SCANNER_REQUEST:
			case ZBAR_QR_SCANNER_REQUEST:
				if (resultCode == RESULT_OK) {
					Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
				} else if(resultCode == RESULT_CANCELED && data != null) {
					String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
					if(!TextUtils.isEmpty(error)) {
						Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
					}
				}
				break;
		}
	}
	
	private void refreshView(){
		if(mUser.isLoggedIn()){
			mLoginButton.setVisibility(View.GONE);
		}
	}

}
