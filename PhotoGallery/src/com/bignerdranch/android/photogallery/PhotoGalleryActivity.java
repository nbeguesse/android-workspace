package com.bignerdranch.android.photogallery;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.Menu;

public class PhotoGalleryActivity extends SingleFragmentActivity {

	public Fragment createFragment(){
		return new PhotoGalleryFragment();
	}
}
