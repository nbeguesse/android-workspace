package com.example.barcodescanningapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;


public class User {
	private static User sUser; //Singleton stuff!
	private Context mAppContext;
	private Integer mId;
	private String mSingleAccessToken;
	private ArrayList<Car> cars;
	
	//Singleton private constructor!
	private User(Context appContext){
		mAppContext = appContext;
		cars = new ArrayList<Car>();
	}
	//Singleton public constructor!
	public static User get(Context c){
		if(sUser == null){
			sUser = new User(c.getApplicationContext());
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
						cars.add(temp);
					}
					
				}
			}
			Log.d("User", "Num cars is "+cars.size());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
	}
	
	public boolean isLoggedIn() {
		return mId != null;
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
		for (Car c : cars){
			if(c.getId() == id){
				return c;
			}
		}
		return null;
	}
  
  
}
