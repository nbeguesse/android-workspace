package com.example.barcodescanningapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class User {
	private static User sUser; //Singleton stuff!

	private Integer mId;
	private String mSingleAccessToken;
	private ArrayList<Car> mCars;

	//Singleton private constructor!
	private User(){
		mCars = new ArrayList<Car>();
	}
	//Singleton public constructor!
	public static User get(){
		if(sUser == null){
			sUser = new User();
		}
		return sUser;
	}

	
	public void loadFromJson(String jsonString){
		try {
			//Load user id and session token from JSON
			JSONObject userObject = new JSONObject(jsonString).getJSONObject("user");
			setSingleAccessToken(userObject.getString("single_access_token"));
			setId(userObject.getInt("id"));
			
			//Merge JSON cars with existing cars
			if(userObject.has("cars")){
				JSONArray carArray = userObject.getJSONArray("cars");
				for (int i=0; i < carArray.length(); i++){
					JSONObject carObject = carArray.getJSONObject(i);
					
					// If a duplicate is found, prevent it from being added and
			        // merge it into the existing model.
					Car existing = getCar(carObject.getInt("id"));
					if(existing != null){
						existing.loadFromJson(carObject);
					} else {
						Car temp = new Car();
						temp.loadFromJson(carObject);
						mCars.add(temp);
					}
					
				}
			}
			Log.d("User", "Num cars is "+mCars.size());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
	}
	
	public void logOut(){
		sUser = new User();
	}
	
	public boolean isLoggedIn() {
		return mId != null;
	}
	
	public boolean hasCars(){
		return !mCars.isEmpty();
	}
	public Integer getId() {
		return mId;
	}


	public void setId(Integer id) {
		mId = id;
	}
	public String getSingleAccessToken() {
		return mSingleAccessToken;
	}
	public void setSingleAccessToken(String singleAccessToken) {
		mSingleAccessToken = singleAccessToken;
	}
	public Car getCar(Integer id){
		for (Car c : mCars){
			if(c.getId() == id){
				return c;
			}
		}
		return null;
	}
	
	public Car getCarByPosition(Integer i){
		return mCars.get(i);
	}
	public ArrayList<Car> getCars() {
		return mCars;
	}
  
  
}
