package com.example.barcodescanningapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;

public class MainActivity extends SingleFragmentActivity {
	public static final String HOST_HTTP = "http://qrvin.com";
	public static final String HOST_HTTPS = "https://qrvin.com";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		defaultFragmentHolder();
	}

	protected Fragment createFragment() {
		return new MainActivityFragment();
	}

}

