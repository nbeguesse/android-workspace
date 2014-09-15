//Edit Crime
package com.bignerdranch.android.criminalintent;

import java.util.UUID;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

//Note: pages 195-225 skipped; Side-swiping not implemented
//Because of Emulator rotation glitch I cannot implement/test pages 269-270

public class EditCrimeFragment extends Fragment {
	private Crime mCrime;
	private EditText mTitleField;
	private Button mDateButton;
	private CheckBox mSolvedCheckBox;
	public static final String EXTRA_CRIME_ID="com.bignerdranch.android.criminalintent.crime_id";
	
	
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
}
