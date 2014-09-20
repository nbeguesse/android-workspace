package com.example.barcodescanningapp;

import com.example.barcodescanningapp.base.SingleFragmentActivity;

import android.support.v4.app.Fragment;

public class MainActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new MainActivityFragment();
	}

}

