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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


/**
 * A fragment representing a single Car detail screen. This fragment is either
 * contained in a {@link CarListActivity} in two-pane mode (on tablets) or a
 * {@link CarDetailActivity} on handsets.
 */
public class CarDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";


	private Car mCar;
	private User mUser;
	private LinearLayout mOptionsContent;
	private LinearLayout mOptionsHolder;
	private EditOptionTask mAuthTask = null;
	private int mOptionId;
	private boolean mInstalled;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CarDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = User.get();
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			//mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
			int car_id = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
			mCar = mUser.getCar(car_id);
		}
	}
	
	//Click listener for print button and installed options button
	 private class MyClickListener implements OnClickListener {

		    private User mUser;
		    private Car mCar;


		    public MyClickListener(Context context, int carId) {
		    	mUser = User.get();
		    	mCar = mUser.getCar(carId);
		    }

		    public void onClick(View v) {
		    	if (v.getId() == R.id.installed_options_button){
		    		mOptionsContent.setVisibility(mOptionsContent.isShown() ? View.GONE : View.VISIBLE);
		    	} else if(v.getId() == R.id.print_button) {
		    		String url = "http://qrvin.com/cars/"+mCar.getId()+"/window_sticker.html";
		    		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		    		startActivity(browserIntent);
		    	} 
		    }
	}
	 
	private class OptionClickListener implements OnClickListener {

			private int mOptionId;

			public OptionClickListener(int optionId) {

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
		View rootView = inflater.inflate(R.layout.fragment_car_detail,
				container, false);

		if (mCar != null) {
			//Show Car name, engine, transmission, and msrp price
			((TextView) rootView.findViewById(R.id.car_detail)).setText(mCar.toString());
			((TextView) rootView.findViewById(R.id.textView_engine)).setText(mCar.getEngine());
			((TextView) rootView.findViewById(R.id.textView_transmission)).setText(mCar.getTransmission());
			NumberFormat format = NumberFormat.getCurrencyInstance();
			format.setMaximumFractionDigits(0);
			if(mCar.getMsrpPrice() > 0){
				((TextView) rootView.findViewById(R.id.textView_msrp)).setText(format.format(mCar.getMsrpPrice()));
				((TextView) rootView.findViewById(R.id.textView_was_new)).setText(format.format(mCar.getWasNew()));	
			} else {
				((TextView) rootView.findViewById(R.id.textView_msrp)).setVisibility(View.GONE);
				((TextView) rootView.findViewById(R.id.textView_was_new)).setVisibility(View.GONE);
			}
			
			// Handle installed options dropdown
			// The content is invisible initially
			mOptionsContent = (LinearLayout) rootView.findViewById(R.id.installed_options_content);
			int total = 0;
			try {
				JSONArray options = mCar.getInstalledOptions();
				
				for (int i = 0; i < options.length(); i++) {

					JSONObject option = options.getJSONObject(i);
					
					int price = 0;
					if (option.has("msrp")) {
						price = Math.round(Float.parseFloat(option.getJSONObject("msrp").getString("highValue")));
					}
					total += price;
					String priceString = (price == 0) ? "" : format.format(price);
					
					View child = inflater.inflate(R.layout.installed_option, null);
				    TextView tv = (TextView) child.findViewById(R.id.textView1);
				    tv.setText(option.getString("value"));
				    TextView pv = (TextView) child.findViewById(R.id.textView2);
				    pv.setText(priceString);
				    mOptionsContent.addView(child);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mOptionsContent.setVisibility(View.GONE);
			TextView installedOptionsButton = (TextView)rootView.findViewById(R.id.installed_options_button);
			installedOptionsButton.setText("See Installed Options ("+format.format(total)+")");
			
			//Show checkable list of options
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
					OptionClickListener mcl = new OptionClickListener(i);
					cb.setOnClickListener(mcl);
					
					//All done! Add it to the list
					ul.addView(child);

				}
				mOptionsHolder.addView(ul); //add the last ul
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			//Handle Default Click Listeners
			MyClickListener mcl = new MyClickListener(this.getActivity(), mCar.getId());
			installedOptionsButton.setOnClickListener(mcl);
			rootView.findViewById(R.id.print_button).setOnClickListener(mcl);
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
