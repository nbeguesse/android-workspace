//Edit Crime
package com.bignerdranch.android.criminalintent;

import java.io.File;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

//Note: pages 195-225 skipped; Side-swiping not implemented
//Because of Emulator rotation glitch I cannot implement/test pages 269-270

public class EditCrimeFragment extends Fragment {
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	private ImageView mPhotoView;
	public static final String EXTRA_CRIME_ID="com.bignerdranch.android.criminalintent.crime_id";
	private static final String DIALOG_IMAGE = "image";
	private ImageButton mPhotoButton;
	private static final int REQUEST_PHOTO = 1;
	public static final String TAG = "CrimeFragment";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		UUID crimeId = (UUID)getActivity().getIntent().getSerializableExtra(EXTRA_CRIME_ID);
		mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
		getActivity().setTitle(mCrime.getTitle());
		setHasOptionsMenu(true); //for icon back button
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) //i.e. tell the debugger to go away
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.edit_crime_fragment, parent, false);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){ //turns the icon into a back button
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		//Handle Title Field
		mTitleField = (EditText)v.findViewById(R.id.crime_title);
		mTitleField.setText(mCrime.getTitle());
		mTitleField.addTextChangedListener(new TextWatcher(){
			public void onTextChanged(
					CharSequence c, int start, int before, int count){
				mCrime.setTitle(c.toString());
			}
			public void beforeTextChanged(CharSequence c, int start, int count, int after){
				//intentionally left blank
			}
			public void afterTextChanged(Editable c){
				//intentionally left blank
			}
		});
		//Handle Date Button
		mDateButton = (Button)v.findViewById(R.id.crime_date);
		mDateButton.setText(mCrime.getFormattedDate());
		mDateButton.setEnabled(false);
		//Handle Solved Checkbox
		mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
		mSolvedCheckBox.setChecked(mCrime.isSolved());
		mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				//Set the crime's solved property
				mCrime.setSolved(isChecked);
			}
		});
		
		mPhotoButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
		mPhotoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
				startActivityForResult(i, REQUEST_PHOTO);
				
			}
		});

        mPhotoView = (ImageView)v.findViewById(R.id.crime_imageView);
        registerForContextMenu(mPhotoView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {         
         @Override
         public void onClick(View v) {
            Photo p = mCrime.getPhoto();
            if (p == null)
               return;
            
            FragmentManager fm = getActivity().getSupportFragmentManager();
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            int orientation = p.getOrientation();
            ImageFragment.newInstance(path, orientation).show(fm, DIALOG_IMAGE);
         }
      });
		//If camera is not available, disable camera functionality
		PackageManager pm = getActivity().getPackageManager();
		boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
				pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
				(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Camera.getNumberOfCameras() > 0);
		if(!hasACamera){
			mPhotoButton.setEnabled(false);
		}
		
		return v;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:
				if(NavUtils.getParentActivityName(getActivity()) != null){ //this line requires PARENT_ACTIVITY metadata in the manifest
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override 
	public void onPause(){
		super.onPause();
		CrimeLab.get(getActivity()).saveCrimes();
	}
	
	@Override
	public void onStart(){
		super.onStart();
		showPhoto();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		PictureUtils.cleanImageView(mPhotoView);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode != Activity.RESULT_OK) return;
		if (requestCode == REQUEST_PHOTO ) {
	         String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
	         int i = data.getIntExtra(CrimeCameraFragment.EXTRA_PHOTO_ORIENTATION, 0);
	         if (filename != null) {
	        	 
	        	 //Check for old photo and replace it
	        	Photo old = mCrime.getPhoto();
	        	if(old != null){
	        		String path = getActivity().getFileStreamPath(old.getFilename()).getAbsolutePath();
                    File f = new File(path);
                    f.delete();
	        	}
	        	//Save the new photo
	            Photo p = new Photo(filename, i);
	            mCrime.setPhoto(p);
	            showPhoto();
	         }
	      }
	}
	
    private void showPhoto() {
        // (Re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
           String path = getActivity().getFileStreamPath(p.getFilename())
                 .getAbsolutePath();
           b = PictureUtils.getScaledDrawable(getActivity(), path);

           int orientation = p.getOrientation();
           if (orientation == CrimeCameraActivity.ORIENTATION_PORTRAIT_INVERTED ||
              orientation == CrimeCameraActivity.ORIENTATION_PORTRAIT_NORMAL) {
              b = PictureUtils.getPortraitDrawable(mPhotoView, b);
           }
        }
        mPhotoView.setImageDrawable(b);
     }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        getActivity().getMenuInflater().inflate(R.menu.crime_photo_delete, menu);
    }

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_delete_photo:
                if (mCrime.getPhoto() != null){
                    String path = getActivity().getFileStreamPath(mCrime.getPhoto().getFilename()).getAbsolutePath();
                    File f = new File(path);
                    f.delete();
                    mCrime.setPhoto(null);
                    mPhotoView.setImageDrawable(null);
                }
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
