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
		mTransmission = o.optString("Transmission");
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


}
