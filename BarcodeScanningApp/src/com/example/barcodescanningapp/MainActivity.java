package com.example.barcodescanningapp;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;
import android.text.TextUtils;

public class MainActivity extends Activity implements OnClickListener {
	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	private Button scanBtn;
	private TextView formatTxt, contentTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		scanBtn = (Button)findViewById(R.id.scan_button);
		formatTxt = (TextView)findViewById(R.id.scan_format);
		contentTxt = (TextView)findViewById(R.id.scan_content);
		scanBtn.setOnClickListener(this);
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
			//ZXING LIBRARY
//			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
//			ArrayList<String> coll = new ArrayList<String>();
//			coll.add("CODE_39");
//			scanIntegrator.initiateScan(coll);
			//ZBAR LIBRARY
			Intent intent = new Intent(this, ZBarScannerActivity.class);
			startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
		}
	}
	
//	public void onActivityResult(int requestCode, int resultCode, Intent intent) { //for ZXING
//		//retrieve scan result
//		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
//		if (scanningResult != null) {
//			//we have a result
//			String scanContent = scanningResult.getContents();
//			String scanFormat = scanningResult.getFormatName();
//			formatTxt.setText("FORMAT: " + scanFormat);
//			contentTxt.setText("CONTENT: " + scanContent);
//			
//		} else {
//		    Toast toast = Toast.makeText(getApplicationContext(),
//		            "No scan data received!", Toast.LENGTH_SHORT);
//		        toast.show();
//		}
//	}
	
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
}
