package com.example.barcodescanningapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Car {
	private int mId;
	private String mVin;
	public String mMake;
	public String mModel;
	public String mStyle;
	public int mYear;
	public String mComments;
	public JSONArray mStandardOptions;
	public JSONArray mOptions;
	public String mEngine;
	public String mTransmission;
	public String mDrivetrain;
	public int mMsrpPrice;
	public String mExteriorColor;
	public String mInteriorColor;
	public int mTotalMsrp;
	public int mMiles;
	public String mPrimaryImage;
	
	public void loadFromJson(JSONObject o){
		try {
			setId(o.getInt("id"));
			setVin(o.optString("vin"));
			mMake = o.optString("make");
			mModel = o.optString("model");
			mStyle = o.optString("style");
			mYear = o.optInt("year");
			mComments = o.optString("comments");
			mStandardOptions = o.optJSONArray("standard_options");
			mOptions = o.optJSONArray("options");
			mEngine = o.optString("engine");
			mTransmission = o.optString("Transmission");
			mDrivetrain = o.optString("drivetrain");
			mMsrpPrice = o.optInt("msrp_price");
			mExteriorColor = o.optString("exterior_color");
			mInteriorColor = o.optString("interior_color");
			mTotalMsrp = o.optInt("total_msrp");
			mMiles = o.optInt("miles");
			mPrimaryImage = o.optString("get_primary_image");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	public String toString(){
		return ""+this.getId();
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


}
