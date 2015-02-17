package com.example.barcodescanningapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class File {
	private static Context mContext;
	
	public static String getFilename(){
		return "qrvin.json"+(new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date()));
	}

	public static void saveUser(Context context){
		try {
			mContext = context;
			User u = User.get();
			//Build the object in JSON
			JSONObject top = new JSONObject();
			JSONObject userObj = new JSONObject();
			JSONArray array = new JSONArray();
			for(Car c : u.getCars()) array.put(c.toJSON());
			userObj.put("cars", array);
			userObj.put("single_access_token", u.getSingleAccessToken());
			userObj.put("id", u.getId());
			top.put("user", userObj);

			//Write the file to disk
			Writer writer = null;
			try {
				OutputStream out = mContext.openFileOutput(getFilename(), Context.MODE_PRIVATE);
				writer = new OutputStreamWriter(out);
				writer.write(top.toString());
			} finally {
				if(writer != null) writer.close();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void loadUser(Context context){
		mContext = context;
		User u = User.get(); 
		if(u.isLoggedIn()) return; //i.e. they rotated the screen onCreate
		BufferedReader reader = null;
		try {
			try{
				//Open and read the file into a StringBuilder
				InputStream in = mContext.openFileInput(getFilename());
				reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder jsonString = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null){
					jsonString.append(line);
				}
				u.loadFromJson(jsonString.toString());
			} catch (FileNotFoundException e){
				//Ignore this one; it happens when starting fresh
			} finally {
				if(reader != null) reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void erase(Context context){
		context.deleteFile(getFilename());
	}

}
