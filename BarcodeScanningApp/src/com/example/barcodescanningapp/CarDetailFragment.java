package com.example.barcodescanningapp;

import java.text.NumberFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
	
	 private class MyClickListener implements OnClickListener {

		    private User mUser;
		    private Car mCar;
		    private Context mContext;

		    public MyClickListener(Context context, int carId) {
		    	mUser = User.get();
		    	mCar = mUser.getCar(carId);
		    	mContext = context;
		    }

		    public void onClick(View v) {

		    	if (v.getId() == R.id.installed_options_button){
		    		mOptionsContent.setVisibility(mOptionsContent.isShown() ? View.GONE : View.VISIBLE);
		    	} else if(v.getId() == R.id.print_button) {
		    		String docMimeType = "application/pdf";
		    		String docTitle = mCar.toString();
		    		Uri docUri = Uri.parse("http://qrvin.com/cars/"+mCar.getId()+"/window_sticker.pdf");
		    		Intent printIntent = new Intent(mContext, PrintDialogActivity.class);
		    		printIntent.setDataAndType(docUri, docMimeType);
		    		printIntent.putExtra("title", docTitle);
		    		startActivity(printIntent);
		    	} else if(v.getId() == R.id.edit_options_button){
		    		Intent intent = new Intent(mContext, EditOptionsActivity.class);
		    		intent.putExtra(EditOptionsFragment.ARG_ITEM_ID, mCar.getId());
					startActivity(intent);
		    	}
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
			((TextView) rootView.findViewById(R.id.textView_msrp)).setText(format.format(mCar.getMsrpPrice()));
			((TextView) rootView.findViewById(R.id.textView_was_new)).setText(format.format(mCar.getWasNew()));	
			
			// Handle installed options dropdown
			//It's invisible initially
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
			
			//Handle Click Listeners
			MyClickListener mcl = new MyClickListener(this.getActivity(), mCar.getId());
			installedOptionsButton.setOnClickListener(mcl);
			rootView.findViewById(R.id.edit_options_button).setOnClickListener(mcl);
			rootView.findViewById(R.id.print_button).setOnClickListener(mcl);
		} 

		return rootView;
	}
	
	
}
