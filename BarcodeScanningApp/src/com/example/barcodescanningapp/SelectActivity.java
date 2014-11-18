package com.example.barcodescanningapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SelectActivity extends Activity {
	private ArrayList<String> mArrayValues = new ArrayList<String>();
	private Bundle extras = new Bundle();
	private String mId, mYear, mMake, mMakeId, mModel, mModelId, mStyle,
			mStyleId = null;
	private View mEmptyView;
	private SelectCarTask mAuthTask = null;

	private ArrayAdapter<String> adapter;
	private ArrayList<String> mList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// setup important stuff
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		setupActionBar();

		// views and initial variables
		ListView mListView = (ListView) findViewById(android.R.id.list);
		mEmptyView = (View) findViewById(android.R.id.empty);

		// Get variables in extras and set up the outgoing bundle
		Intent intent = getIntent();
		if (!intent.hasExtra("EXTRA_YEAR")) {
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			for (int i = year; i >= 1981; --i) {
				mArrayValues.add(Integer.toString(i));
			}
			mList = new ArrayList<String>(mArrayValues);
		} else {
			mYear = intent.getStringExtra("EXTRA_YEAR");
			extras.putString("EXTRA_YEAR", mYear);
			if (intent.hasExtra("EXTRA_MAKE")) {
				mMake = intent.getStringExtra("EXTRA_MAKE");
				mMakeId = intent.getStringExtra("EXTRA_MAKE_ID");
				extras.putString("EXTRA_MAKE", mMake);
				extras.putString("EXTRA_MAKE_ID", mMakeId);

				if (intent.hasExtra("EXTRA_MODEL")) {
					mModel = intent.getStringExtra("EXTRA_MODEL");
					mModelId = intent.getStringExtra("EXTRA_MODEL_ID");
					extras.putString("EXTRA_MODEL", mModel);
					extras.putString("EXTRA_MODEL_ID", mModelId);

					if (intent.hasExtra("EXTRA_STYLE")) {
						mStyle = intent.getStringExtra("EXTRA_STYLE");
						mStyleId = intent.getStringExtra("EXTRA_STYLE_ID");

					}
				}
			}
		}

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mList);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				final String item = (String) parent.getItemAtPosition(position);
				final String value = mArrayValues.get(position);
				Intent newIntent = new Intent(getApplicationContext(),
						SelectActivity.class);
				if (mYear == null) {
					extras.putString("EXTRA_YEAR", item);
				} else if (mMake == null) {
					extras.putString("EXTRA_MAKE", item);
					extras.putString("EXTRA_MAKE_ID", value);
				} else if (mModel == null) {
					extras.putString("EXTRA_MODEL", item);
					extras.putString("EXTRA_MODEL_ID", value);
				} else if (mStyle == null) {
					extras.putString("EXTRA_STYLE", item);
					extras.putString("EXTRA_STYLE_ID", value);
				}
				newIntent.putExtras(extras);
				startActivity(newIntent);

			}
		});

		// Start loading URL immediately if the list is empty
		if (mList.isEmpty()) {
			// show loading screen
			showProgress(true);
			// make request
			mAuthTask = new SelectCarTask();
			mAuthTask.execute((Void) null);
		}

	}

	private void showProgress(final boolean show) {
		mEmptyView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public class SelectCarTask extends AsyncTask<Void, Void, String> {

		private static final String CONNECTION_SUCCESS = "Success"; // Shows list of options
		private static final String CONNECTION_ERROR = "Error"; // Causes "try again" message
		private static final String GOT_CAR = "Got Finished Car"; // Goes to car info

		@Override
		protected String doInBackground(Void... params) {

			try {
				HttpClient httpclient = new DefaultHttpClient();

				if (mStyleId != null) {

					String url = R.string.host + "/cars/vin?car[year]=" + mYear
							+ "&car[make]=" + mMake + "&car[model]=" + mModel
							+ "&car[chrome_style_id]=" + mStyleId;
					HttpGet httpget = new HttpGet(url);
					HttpResponse response = httpclient.execute(httpget);
					String stringResponse = inputStreamToString(response
							.getEntity().getContent());
					JSONObject jsonObject = new JSONObject(stringResponse);
					if (jsonObject.has("car")) {
						//load the car object and add to the car list
						JSONObject carObject = jsonObject.getJSONObject("car");
						User.get().loadCarFromJson(carObject);
						//save the car id so we can flip to that page
						mId = Integer.toString(carObject.getInt("id")); 
					}
					return GOT_CAR;
				} else {
					String url = R.string.host + "/cars/chrome_select?year="
							+ mYear;
					if (mMakeId != null)
						url += "&make_id=" + mMakeId;
					if (mModelId != null)
						url += "&model_id=" + mModelId;
					// Execute HTTP Post Request
					HttpGet httpget = new HttpGet(url);
					HttpResponse response = httpclient.execute(httpget);
					// Convert the String Response to JSON
					String stringResponse = inputStreamToString(response
							.getEntity().getContent());

					JSONArray jsonObject = new JSONArray(stringResponse);
					for (int i = 0; i < jsonObject.length(); ++i) {
						JSONArray pair = jsonObject.getJSONArray(i);
						mArrayValues.add(pair.getString(0));
						mList.add(pair.getString(1));
					}
					return CONNECTION_SUCCESS;
				}

			} catch (ClientProtocolException e) {
				return CONNECTION_ERROR;
			} catch (IOException e) {
				return CONNECTION_ERROR;
			} catch (JSONException e) {
				return CONNECTION_ERROR;
			}

		}

		@Override
		protected void onPostExecute(final String response) {
			mAuthTask = null;
			showProgress(false);

			if (response == CONNECTION_SUCCESS) {
				adapter.notifyDataSetChanged();
			} else if (response == GOT_CAR) {
				if(User.get().isLoggedIn()){
				  File.saveUser(getApplicationContext());
				}
				Intent intent = new Intent(getApplicationContext(), CarListActivity.class);
				intent.putExtra("My Intent Extra", mId);
				startActivity(intent);
				finish();
			} else if (response == CONNECTION_ERROR) {
				// Try again if any exceptions were thrown
				finish();
				Toast.makeText(getApplicationContext(),
						R.string.connection_problem, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			finish();
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

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
