package com.example.barcodescanningapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class Car {
	private int mId;
	private String mVin;
	private String mMake;
	private String mModel;
	private String mStyle;
	private int mYear;
	private String mComments;
	private JSONArray mStandardOptions;
	private JSONArray mOptions;
	private String mEngine;
	private String mTransmission;
	private String mDrivetrain;
	private int mMsrpPrice;
	private String mExteriorColor;
	private String mInteriorColor;
	private int mTotalMsrp;
	private int mMiles;
	private String mPrimaryImage;
	
	public void loadFromJson(JSONObject o){

		setId(o.optInt("id"));
		setVin(o.optString("vin"));
		mMake = o.optString("make");
		mModel = o.optString("model");
		mStyle = o.optString("style");
		mYear = o.optInt("year");
		mComments = o.optString("comments");
		mStandardOptions = o.optJSONArray("standard_options");
		mOptions = o.optJSONArray("options");
		mEngine = o.optString("engine");
		mTransmission = o.optString("transmission");
		mDrivetrain = o.optString("drivetrain");
		mMsrpPrice = o.optInt("msrp_price");
		mExteriorColor = o.optString("exterior_color");
		mInteriorColor = o.optString("interior_color");
		mTotalMsrp = o.optInt("total_msrp");
		mMiles = o.optInt("miles");
		mPrimaryImage = o.optString("get_primary_image");

		return;
	}
	
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.putOpt("id", this.getId());
		json.putOpt("vin", this.getVin());
		json.putOpt("make", mMake);
		json.putOpt("model", mModel);
		json.putOpt("style", mStyle);
		json.putOpt("year", mYear);
		json.putOpt("comments", mComments);
		json.putOpt("standard_options", mStandardOptions);
		json.putOpt("options", mOptions);
		json.putOpt("engine", mEngine);
		json.putOpt("transmission", mTransmission);
		json.putOpt("drivetrain", mDrivetrain);
		json.putOpt("msrp_price", mMsrpPrice);
		json.putOpt("exterior_color", mExteriorColor);
		json.putOpt("interior_color", mInteriorColor);
		json.putOpt("total_msrp", mTotalMsrp);
		json.putOpt("miles", mMiles);
		json.putOpt("get_primary_image", mPrimaryImage);
		return json;
	}
	
	public String toString(){
		List<String> list = new ArrayList<String>();
		list.add(Integer.toString(mYear));
		list.add(mMake);
		list.add(mModel);
		list.removeAll(Collections.singleton(null));
		String out = TextUtils.join(" ", list);
		if(TextUtils.isEmpty(out.trim())) out = "New Car";
		return out;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public String getVin() {
		return mVin;
	}

	public void setVin(String vin) {
		this.mVin = vin;
	}
	public JSONArray getInstalledOptions() throws JSONException{
		JSONArray installed = new JSONArray();
		for (int i=0; i < mOptions.length(); i++){
			JSONObject option = mOptions.getJSONObject(i);
			if(option.has("installed")){
				if(String.valueOf(option.get("installed")) == "true"){
					installed.put(option);
				}
			}
		}

		installed.put(destinationCharge());
		return installed;
	}
	
	
	public int getWasNew(){
		int out = 0;
		if(mMsrpPrice > 0){
			out = mMsrpPrice;
			JSONArray options;
			try {
				options = getInstalledOptions();
				for (int i=0; i < options.length(); i++){
					JSONObject option = options.getJSONObject(i);
					if(option.has("msrp")){
						JSONObject temp = option.getJSONObject("msrp");
						out += Math.round( Float.parseFloat(temp.getString("highValue")));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		return out;
	}



	public JSONArray getOptions() {
		return mOptions;
	}

	public String getEngine() {
		return mEngine;
	}

	public String getTransmission() {
		return mTransmission;
	}

	public int getMsrpPrice() {
		return mMsrpPrice;
	}

	public String getExteriorColor() {
		return mExteriorColor;
	}

	public String getInteriorColor() {
		return mInteriorColor;
	}

	public void setInstalledOption(int optionId, boolean installed){
		JSONArray temp = new JSONArray();
		for(int i=0; i<mOptions.length(); i++){
			try {
				JSONObject original = mOptions.getJSONObject(i);
				JSONObject copy = new JSONObject(original.toString());
				
				if(i == optionId){
					copy.put("installed", installed);
				}
				temp.put(copy);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		
		}
		mOptions = temp;
	}
	private JSONObject destinationCharge() throws JSONException{
		JSONObject destinationCharge = new JSONObject();
		destinationCharge.put("name", "OTHER");
		JSONObject dcInvoice = new JSONObject();
		dcInvoice.put("highValue", 795);
		dcInvoice.put("lowValue", 795);
		JSONObject dcMsrp = new JSONObject();
		dcMsrp.put("highValue", 795);
		dcMsrp.put("lowValue", 795);
		destinationCharge.put("msrp", dcMsrp);
		destinationCharge.put("code", "");
		destinationCharge.put("installed", true);
		destinationCharge.put("value", "DESTINATION CHARGE");
		return destinationCharge;
	}

}
