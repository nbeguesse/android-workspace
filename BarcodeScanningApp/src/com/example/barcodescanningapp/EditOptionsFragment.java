package com.example.barcodescanningapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


public class EditOptionsFragment extends Fragment {

	public static final String ARG_ITEM_ID = "hls.mobile.car_id";


	private Car mCar;
	private User mUser;
	private LinearLayout mOptionsHolder;
	private EditOptionTask mAuthTask = null;
	private int mOptionId;
	private boolean mInstalled;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = User.get();

		int car_id = (getActivity().getIntent().getIntExtra(ARG_ITEM_ID, 0));
		mCar = mUser.getCar(car_id);
		
	}
	
	private class MyClickListener implements OnClickListener {

		private int mOptionId;

		public MyClickListener(int optionId) {

			mOptionId = optionId;
		}

		public void onClick(View v) {

			CheckBox checkBox = (CheckBox) v;
			boolean installed = checkBox.isChecked();
			//Log.d("CAR", "Option " + mOptionId + " is " + installed);

			sync(mOptionId, installed);

		}

	}
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_edit_options,
				container, false);
		
		if (mCar != null) {
			NumberFormat format = NumberFormat.getCurrencyInstance();
			format.setMaximumFractionDigits(0);
			mOptionsHolder = (LinearLayout) rootView.findViewById(R.id.installed_options_checklist); 
			
			String category = "";
			LinearLayout ul = getNewVerticalLinearLayout();
			
			try {
				JSONArray options = mCar.getOptions();
				for (int i = 0; i < options.length(); i++) {

					JSONObject option = options.getJSONObject(i);
					// add category names at the top of each ul as the ul is added to optionsHolder
					if (!category.equals(option.getString("name"))) {
						category = option.getString("name");
						mOptionsHolder.addView(ul);
						TextView header = new TextView(this.getActivity());
						header.setText(option.getString("name"));
						mOptionsHolder.addView(header);
						ul = getNewVerticalLinearLayout();
					}
					
					//get child row template
					View child = inflater.inflate(R.layout.installed_option_with_checkbox, null);
					
					//Handle pricing value
					int price = 0;
					if (option.has("msrp")) {
						price = Math.round(Float.parseFloat(option.getJSONObject("msrp").getString("highValue")));
					}
					String priceString = (price == 0) ? "" : format.format(price);
					TextView pv = (TextView) child.findViewById(R.id.textView2);
					pv.setText(priceString);
					
					//Handle option name value
					CheckBox cb = (CheckBox) child.findViewById(R.id.checkBox1);
					cb.setText(option.getString("value"));
					cb.setChecked(String.valueOf(option.get("installed")).equals("true"));
					
					//Set click listener for the checkbox
					MyClickListener mcl = new MyClickListener(i);
					cb.setOnClickListener(mcl);
					
					//All done! Add it to the list
					ul.addView(child);

				}
				mOptionsHolder.addView(ul); //add the last ul
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} 

		return rootView;
	}
	
	public LinearLayout getNewVerticalLinearLayout() {
		LinearLayout ul = new LinearLayout(this.getActivity());
		ul.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		ul.setOrientation(LinearLayout.VERTICAL);
		return ul;
	}
	
	public void sync(int optionId, boolean installed) {

		mOptionId = optionId;
		mInstalled = installed;
		mCar.setInstalledOption(optionId, installed);
		if(mUser.isLoggedIn()){
			mAuthTask = new EditOptionTask();
			mAuthTask.execute((Void) null);
		}
		File.saveUser(getActivity());
	
	}
	

	public class EditOptionTask extends AsyncTask<Void, Void, String> {

		private static final String EDIT_SUCCESS = "Success"; 
		private static final String EDIT_ERROR = "Error"; // Causes "try again" message
		
		@Override
		protected String doInBackground(Void... params) {

			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(MainActivity.HOST_HTTP+"/cars/"+mCar.getId()+"/update_option"); //Note: using R.string.host doesn't work

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = mUser.createNameValuePairs(3);
				nameValuePairs.add(new BasicNameValuePair("option_id", ""+mOptionId));
				nameValuePairs.add(new BasicNameValuePair("id", ""+mCar.getId()));
				nameValuePairs.add(new BasicNameValuePair("installed", mInstalled+""));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				// Convert the String Response to JSON
				String mLoginResponse = inputStreamToString(response.getEntity().getContent());

				JSONObject jsonObject = new JSONObject(mLoginResponse);
				if(jsonObject.has("errors")) {
					//Return error message to the UI
					JSONArray errorsObject = jsonObject.getJSONArray("errors");
					return errorsObject.getString(0); 
				} else {
					return EDIT_SUCCESS;
				}

			} catch (ClientProtocolException e) {
				return EDIT_ERROR;
			} catch (IOException e) {
				return EDIT_ERROR;
			} catch (JSONException e) {
				return EDIT_ERROR;
			}

			
		}

		@Override
		protected void onPostExecute(final String response) {


			
			if (response==EDIT_SUCCESS) {
				//Do nothing!
			} else if (response == EDIT_ERROR){
				
				//Try again if any exceptions were thrown
				Toast.makeText(getActivity(), "There was an error connecting. Please try again later.", Toast.LENGTH_SHORT).show();
			} else {
				
				//Display whatever message we got from the server
				Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
			}
		}


		
		private String inputStreamToString(InputStream is) throws IOException {
		    String line = "";
		    StringBuilder total = new StringBuilder();
		    
		    // Wrap a BufferedReader around the InputStream
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		    // Read response until the end
		    while ((line = rd.readLine()) != null) { 
		        total.append(line); 
		    }
		    
		    // Return full string
		    return total.toString();
		}
	}
	
}
