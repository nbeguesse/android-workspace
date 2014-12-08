package com.example.barcodescanningapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class CarVinActivity extends Activity {
	
	private EditText mVinView;
	private View mUploadFormView;
	private View mUploadStatusView;
	private TextView mUploadStatusMessageView;
	private TextView mUploadErrorMessageView;
	private JSONObject mCarObject = null;
	private User mUser;
	private String mScannedVin = null;
	public static final String VIN_TO_SEND = "com.qrvin.barcodescanningapp.vin_to_send";
	
	/**
	 * Keep track of the upload task to ensure we can cancel it if requested.
	 */
	private CarUploadTask mAuthTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_car_vin);
		mUser = User.get();
		mVinView = (EditText)findViewById(R.id.vin);

		findViewById(R.id.vin_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptUpload();
					}
				}
		);
		mUploadFormView = findViewById(R.id.upload_form);
		mUploadStatusView = findViewById(R.id.upload_status);
		mUploadStatusMessageView = (TextView) findViewById(R.id.upload_status_message);
		mUploadErrorMessageView = (TextView)findViewById(R.id.upload_error);
		mUploadErrorMessageView.setText("");
		
		mScannedVin = getIntent().getStringExtra(VIN_TO_SEND);
		if(!TextUtils.isEmpty(mScannedVin)){
			mVinView.setText(mScannedVin);
			attemptUpload();
		}
	}
	

	public void attemptUpload() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mVinView.setError(null);

		// Store values at the time of the upload attempt.
		String mVin = mVinView.getText().toString();


		boolean cancel = false;
		View focusView = null;

		// Check for a blank vin number.
		if (TextUtils.isEmpty(mVin)) {
			mVinView.setError(getString(R.string.error_field_required));
			focusView = mVinView;
			cancel = true;
		} else if (mVin.length() != 17) {
			mVinView.setError(getString(R.string.vin_must_be_17_chars));
			focusView = mVinView;
			cancel = true;
			
		}

		if (cancel) {
			// There was an error; don't attempt upload and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the upload attempt.
			mUploadStatusMessageView.setText(R.string.upload_progress);
			showProgress(true);
			mAuthTask = new CarUploadTask();
			mAuthTask.execute((Void) null);
		}
	}
	
	/**
	 * Shows the progress UI and hides the previous form
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mUploadStatusView.setVisibility(View.VISIBLE);
			mUploadStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mUploadStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mUploadFormView.setVisibility(View.VISIBLE);
			mUploadFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mUploadFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mUploadStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mUploadFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous upload task
	 */
	public class CarUploadTask extends AsyncTask<Void, Void, String> {

		private static final String UPLOAD_SUCCESS = "Success"; 
		private static final String UPLOAD_ERROR = "Error"; // Causes "try again" message
		
		@Override
		protected String doInBackground(Void... params) {

			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(MainActivity.HOST_HTTP+"/cars/vin.json");

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = mUser.createNameValuePairs(1);
				nameValuePairs.add(new BasicNameValuePair("car[vin]", mVinView.getText().toString()));
				
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				// Convert the String Response to JSON
				String uploadResponse = inputStreamToString(response.getEntity().getContent());

				JSONObject jsonObject = new JSONObject(uploadResponse);
				if(jsonObject.has("errors")) {
					//Return error message to the UI
					JSONArray errorsObject = jsonObject.getJSONArray("errors");
					return errorsObject.getString(0); 
				} else {
					mCarObject = jsonObject.optJSONObject("car");
					return UPLOAD_SUCCESS;
				}

			} catch (ClientProtocolException e) {
				return UPLOAD_ERROR;
			} catch (IOException e) {
				return UPLOAD_ERROR;
			} catch (JSONException e) {
				return UPLOAD_ERROR;
			}

			
		}

		@Override
		protected void onPostExecute(final String response) {
			mAuthTask = null;
			showProgress(false);

			
			if (response==UPLOAD_SUCCESS) {
				//Load Car from Json
				if(mCarObject != null) mUser.loadCarFromJson(mCarObject);
				finish(); 
				//Navigate to car list
				Intent intent = new Intent(getApplicationContext(), CarListActivity.class);
				try {
					//Go automatically to the new car id
					intent.putExtra(CarListActivity.ARG_ITEM_ID, mCarObject.getInt("id"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				startActivity(intent);
			} else if (response == UPLOAD_ERROR){
				
				//Try again if any exceptions were thrown
				mUploadErrorMessageView.setText("There was a problem. Please try again later.");
			} else {
				
				//Display whatever message we got from the server
				mUploadErrorMessageView.setText(response);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
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
