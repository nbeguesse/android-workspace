package com.example.barcodescanningapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class EditOptionsActivity extends SingleFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		defaultFragmentHolder();
	}
	protected Fragment createFragment() {
		return new EditOptionsFragment();
	}

}
