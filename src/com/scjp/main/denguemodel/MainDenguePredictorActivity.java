/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.scjp.main.denguemodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.scjp.tracker.GPSTracker;
import com.scjp.weka.bean.DengueBean;
import com.scjp.weka.model.ClassifyDengueCases;
import com.scjp.weka.util.NormalizedDengueData;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainDenguePredictorActivity extends Activity {

	private static final String TAG = "CrowdSourceThis";
	public static AmazonClientManager clientManager = null;
	private android.widget.EditText txtStatus = null;
	private static String statusMessage = "Not Set..";
	GPSTracker gps = null;
	private double latitude = 0f;
	private double longitude = 0f;
	private double temp = 0f;
	private double pressure = 0f;
	private double humidity = 0f;
	private double windspeed=0f;
	private String district = "Null";
	private String iconCode = "01d";
	private String Maincity="Null";
	private int predictedDengueValue=0;
	DengueBean bean=null;
	
	com.scjp.tracker.AlertDialogManager alert = new com.scjp.tracker.AlertDialogManager();
	ImageView img;
	Bitmap bitmap;
	// flag for Internet connection status
	Boolean isInternetPresent = false;
	private com.scjp.tracker.ConnectionDetector cd;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		cd = new com.scjp.tracker.ConnectionDetector(getApplicationContext());

		// Check if Internet present
		isInternetPresent = cd.isConnectingToInternet();
		if (!isInternetPresent) {
			// Internet Connection is not present
			alert.showAlertDialog(MainDenguePredictorActivity.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			// return;
		}

		clientManager = new AmazonClientManager(this);

		txtStatus = (EditText) findViewById(R.id.txtStatus);
		img = (ImageView) findViewById(R.id.img);

		
		final Button btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

		// show location button click event
		btnShowLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// create class object
				gps = new GPSTracker(MainDenguePredictorActivity.this);

				// check if GPS enabled
				if (gps.canGetLocation()) {

					latitude = gps.getLatitude();
					longitude = gps.getLongitude();

					// \n is for new line
					Toast.makeText(getApplicationContext(),
							"Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
				} else {
					// can't get location
					// GPS or Network is not enabled
					// Ask user to enable GPS/network in settings
					gps.showSettingsAlert();
				}

			}
		});

		final Button createTableBttn = (Button) findViewById(R.id.create_table_bttn);
		createTableBttn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log.i(TAG, "createTableBttn clicked.");

				new DynamoDBManagerTask().execute(DynamoDBManagerType.CREATE_TABLE);
			}
		});

		final Button insertUsersBttn = (Button) findViewById(R.id.insert_users_bttn);
		insertUsersBttn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log.i(TAG, "insertUsersBttn clicked.");
				new DynamoDBManagerTask().execute(DynamoDBManagerType.INSERT_USER);
			}
		});

		final Button listUsersBttn = (Button) findViewById(R.id.list_users_bttn);
		listUsersBttn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log.i(TAG, "listUsersBttn clicked.");

				new DynamoDBManagerTask().execute(DynamoDBManagerType.LIST_USERS);
			}
		});

		final Button deleteTableBttn = (Button) findViewById(R.id.delete_table_bttn);
		deleteTableBttn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log.i(TAG, "deleteTableBttn clicked.");

				new DynamoDBManagerTask().execute(DynamoDBManagerType.CLEAN_UP);
			}
		});

		
		//Predict Dengue Cases
		final Button predictButton = (Button) findViewById(R.id.predict_bttn);
		predictButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log.i(TAG, "PredictBttn clicked.");
				ClassifyDengueCases predictCase = new ClassifyDengueCases();
				double predictionValue=0;
				try{
					Context ctx=getApplicationContext();
					
				predictionValue=predictCase.readClassifier(bean, "2010-2012", "Colombo",ctx);
				}catch(Exception e){
					Log.e("Error", "Error in file"+e);
					
					
				}
				Toast.makeText(getApplicationContext(), "The Model Predicts: "+predictionValue+" Dengue Cases today for city: "+Maincity+" District: "+district, Toast.LENGTH_LONG).show();
			}
		});
		final Button getWeatherData = (Button) findViewById(R.id.GetWeatherData);
		getWeatherData.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Log.i(TAG, "getWeatherData clicked.");
				HashMap<String, String> urlMap = new HashMap<String, String>();

				String serverURL = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon="
						+ longitude + "&appid=f428c77a063e968735f60f9c82878238";
				String iconUrl = "http://openweathermap.org/img/w/" + iconCode + ".png";
				String type = "weather";

				urlMap.put("weather", serverURL);
				urlMap.put("weatherType", type);

				serverURL = "http://api.geonames.org/findNearbyPostalCodesJSON?lat=" + latitude + "&lng=" + longitude
						+ "&username=merdocbilal";
				type = "district";
				urlMap.put("district", serverURL);
				//urlMap.put("districtType", type);
				urlMap.put("icon", iconUrl);

				new LongOperation().execute(urlMap);
				// executing district
			}
		});

	
	}

	/**
	 * 
	 * @author Bilal
	 *
	 */
	private class LongOperation extends AsyncTask<HashMap<String, String>, Void, Bitmap> {

		// Required initialization

		private final HttpClient Client = new DefaultHttpClient();
		private String Content;
		private String weatherContent;
		private String districtContent;
		private String Error = null;
		private ProgressDialog Dialog = new ProgressDialog(MainDenguePredictorActivity.this);
		String data = "";
		String type = "";
		String uiUpdate, jsonParsed, serverText;

		private String getJSONContents(URL url) {
			BufferedReader reader = null;

			// Send data
			try {

				// Defined URL where to send data
				// URL url = new URL(urls[0]);
				// type = urls[1];

				// Send POST data request

				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(data);
				wr.flush();

				// Get the server response

				reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

				// Read Server Response
				while ((line = reader.readLine()) != null) {
					// Append server response in string
					sb.append(line + "");
				}

				// Append Server Response To Content String
				Content = sb.toString();
				// bitmap = BitmapFactory.decodeStream((InputStream)new
				// URL(urls[2]).getContent());

			} catch (Exception ex) {
				Error = ex.getMessage();
			} finally {
				try {

					reader.close();

				}

				catch (Exception ex) {
				}
				return Content;
			}

		}

		// TextView uiUpdate = (TextView) findViewById(R.id.output);
		// TextView jsonParsed = (TextView) findViewById(R.id.jsonParsed);
		int sizeData = 0;
		// EditText serverText = (EditText) findViewById(R.id.serverText);

		protected void onPreExecute() {
			// NOTE: You can call UI Element here.

			// Start Progress Dialog (Message)

			Dialog.setMessage("Please wait..");
			Dialog.show();


		}

		// Call after onPreExecute method
		protected Bitmap doInBackground(HashMap<String, String>... map) {

			/************ Make Post Call To Web Server ***********/
		//	type = map[0].get("weatherType").toString();// urls[1];

			BufferedReader reader = null;
			try {
				Log.i("WEATHER", map[0].get("weather").toString());
				Log.i("DISTRICT", map[0].get("district").toString());
				weatherContent = getJSONContents(new URL(map[0].get("weather").toString()));// urls[0]));
				//Log.i("WEATHER", map[0].get("weather").toString());
				districtContent = getJSONContents(new URL(map[0].get("district").toString()));// urls[0]));
				bitmap = BitmapFactory.decodeStream((InputStream) new URL(map[0].get("icon").toString()).getContent());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception e){
				Log.e("Error on map", e.getMessage());
				
			}
			// Send data
			return bitmap;
			/*****************************************************/

		}

		protected void onPostExecute(Bitmap bitmap) {
			// NOTE: You can call UI Element here.

			// Close progress dialog
			Dialog.dismiss();

			if (Error != null) {

				Toast.makeText(getApplicationContext(), "output:" + Error, Toast.LENGTH_SHORT);// uiUpdate.setText("Output
																								// :
																								// "+Error);

			} else {

				// Show Response Json On Screen (activity)
				Toast.makeText(getApplicationContext(), "output:" + Error, Toast.LENGTH_LONG);

				/******************
				 * Start Parse Response JSON Data
				 *************/

				String OutputData = "";
				JSONObject jsonResponse;

				try {

					/******
					 * Creates a new JSONObject with name/value mappings from
					 * the JSON string.
					 ********/
					jsonResponse = new JSONObject(weatherContent);
					JSONArray jsonMainNode;
					JSONObject jsonChildNode;
					/*****
					 * Returns the value mapped by name if it exists and is a
					 * JSONArray.
					 ***/
					/******* Returns null otherwise. *******/

				/**
				 * Getting the json parsed format for Weather
				 * 
				 * {"coord":{"lon":79.92,"lat":6.9},
				 * "weather":[{"id":801,"main":"Clouds","description":"few clouds","icon":"02n"}],
				 * "base":"stations","main":{"temp":303.15,"pressure":1012,"humidity":70,"temp_min":303.15,"temp_max":303.15},
				 * "visibility":10000,"wind":{"speed":2.1,"deg":290},
				 * "clouds":{"all":20},"dt":1459005000,"sys":{"type":1,"id":7864,"message":0.008,"country":"LK","sunrise":1458952870,"sunset":1458996619},
				 * "id":1250164,"name":"Battaramulla South","cod":200}
				 */
					
					{

						jsonMainNode = jsonResponse.optJSONArray("weather");
						JSONObject jsonMain = jsonResponse.optJSONObject("main");
						/*********** Process each JSON Node ************/

						

						// for(int i=0; i < lengthJsonArr; i++)
						{
							/****** Get Object for each JSON node. ***********/
							jsonChildNode = jsonMainNode.getJSONObject(0);
							temp = jsonMain.getDouble("temp") - 273.15;

							pressure = jsonMain.getDouble("pressure");
							pressure=pressure/10;//convert pressure to kPa
							humidity = jsonMain.getDouble("humidity");
							
							/*******Get the Wind Speed***********/
							jsonMain=jsonResponse.optJSONObject("wind");
							windspeed=jsonMain.getDouble("speed");
							Log.i("Wind", windspeed+"");
							/******* Fetch node values **********/
							String main = jsonChildNode.optString("main").toString();
							String weatherdata = "T:" + temp + " P:" + pressure + " H:" + humidity+
							"W: "+windspeed;
							String icon = jsonChildNode.optString("icon").toString();
							
							String iconUrl = "http://openweathermap.org/img/w/" + icon + ".png";
							
							

							Log.i("Weather: ", weatherdata);
							iconCode = icon;

							Toast.makeText(getApplicationContext(), "Main:" + main, Toast.LENGTH_LONG).show();

						}
					}

					/*
					 * Getting the json parsed format for District
					 */
					{

						jsonResponse = new JSONObject(districtContent);

						jsonMainNode = jsonResponse.optJSONArray("postalCodes");

						/*********** Process each JSON Node ************/

						int lengthJsonArr = jsonMainNode.length();

						// for(int i=0; i < lengthJsonArr; i++)
						{
							/****** Get Object for each JSON node. ***********/
							jsonChildNode = jsonMainNode.getJSONObject(0);

							/******* Fetch node values **********/
							String name = jsonChildNode.optString("adminName2").toString();
							String city = jsonChildNode.optString("placeName").toString();
							String longit = jsonChildNode.optString("lng").toString();
							String latid = jsonChildNode.optString("lat").toString();

							OutputData += " Name           : " + name + "  " + "longitude      : " + longit + "  "
									+ "Time                : " + latid + " "
									+ "-------------------------------------------------"+" City: "+city;
							Log.i("District", name);
							district = name;
							Maincity=city;
							//Convert to normalized data
							int _temp=NormalizedDengueData.getNormalizedTemp(temp);
							int _humidity=NormalizedDengueData.getNormalizedHumidity(humidity);
							int _windspeed=NormalizedDengueData.getNormalizedWindSpeed(windspeed);
							int _pressure=NormalizedDengueData.getNormalizedPressure(pressure);
							
							//Add the weather data info to the DengueBean
							bean = new DengueBean(_temp+"", _humidity+"", _windspeed+"", "?", _pressure+"");

							Toast.makeText(getApplicationContext(), "District:" + name+"\n"+"city: "+city, Toast.LENGTH_LONG).show();
						}
					}

					if (bitmap != null) {
						img.setImageBitmap(bitmap);
						// Dialog.dismiss();
					} else {
						Dialog.dismiss();
						Toast.makeText(MainDenguePredictorActivity.this, "Image Does Not exist or Network Error",
								Toast.LENGTH_SHORT).show();
					}
					Toast.makeText(getApplicationContext(),
							"District:" + district + "\n " + "Weather: T:" + temp + " P:" + pressure + " H:" + humidity+"\n"+" W:"+windspeed,
							 
							Toast.LENGTH_LONG).show();
					/******************
					 * End Parse Response JSON Data
					 *************/

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class DynamoDBManagerTask extends AsyncTask<DynamoDBManagerType, Void, DynamoDBManagerTaskResult> {

		protected DynamoDBManagerTaskResult doInBackground(DynamoDBManagerType... types) {

			String tableStatus = DynamoDBManager.getTestTableStatus();
			statusMessage = txtStatus.getText().toString();
			Log.e("Status", statusMessage + "");

			DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
			result.setTableStatus(tableStatus);
			result.setTaskType(types[0]);

			if (types[0] == DynamoDBManagerType.CREATE_TABLE) {
				if (tableStatus.length() == 0) {
					DynamoDBManager.createTable();
				}
			} else if (types[0] == DynamoDBManagerType.INSERT_USER) {
				// Double.toString(gps.getLatitude());
				// Double.toString(gps.getLongitude());
				Log.e("Latitude", latitude + "");
				Log.e("Longitude", longitude + "");
				 SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        	   String updateStaticDate = dateFormatter.format(new Date());
				// txtStatus.setText((txtStatus.getText().equals(""))?"Null":txtStatus.getText().toString());
				if (tableStatus.equalsIgnoreCase("ACTIVE")) {
					Log.e("Inserting", "Inserting users...");
					Log.e("Inserting", "Status message..." + statusMessage);
					DynamoDBManager.insertUsers(statusMessage, latitude, longitude,updateStaticDate);
					
					DynamoDBManager.insertCurrentWeather(district, temp + "", humidity + "",windspeed+"", pressure + "", predictedDengueValue,
							latitude + "", longitude + "","Bilal",updateStaticDate,Maincity);
				}
			} else if (types[0] == DynamoDBManagerType.LIST_USERS) {
				if (tableStatus.equalsIgnoreCase("ACTIVE")) {
					DynamoDBManager.getUserList();
				}
			} else if (types[0] == DynamoDBManagerType.CLEAN_UP) {
				if (tableStatus.equalsIgnoreCase("ACTIVE")) {
					DynamoDBManager.cleanUp();
				}
			}

			return result;
		}

		protected void onPostExecute(DynamoDBManagerTaskResult result) {

			if (result.getTaskType() == DynamoDBManagerType.CREATE_TABLE) {

				if (result.getTableStatus().length() != 0) {
					Toast.makeText(MainDenguePredictorActivity.this,
							"The test table already exists.\nTable Status: " + result.getTableStatus(),
							Toast.LENGTH_LONG).show();
				}

			} else if (result.getTaskType() == DynamoDBManagerType.LIST_USERS
					&& result.getTableStatus().equalsIgnoreCase("ACTIVE")) {

				startActivity(new Intent(MainDenguePredictorActivity.this, UserListActivity.class));

			} else if (!result.getTableStatus().equalsIgnoreCase("ACTIVE")) {

				Toast.makeText(MainDenguePredictorActivity.this,
						"The test table is not ready yet.\nTable Status: " + result.getTableStatus(), Toast.LENGTH_LONG)
						.show();
			} else if (result.getTableStatus().equalsIgnoreCase("ACTIVE")
					&& result.getTaskType() == DynamoDBManagerType.INSERT_USER) {
				Toast.makeText(MainDenguePredictorActivity.this, "Users inserted successfully!", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private enum DynamoDBManagerType {
		GET_TABLE_STATUS, CREATE_TABLE, INSERT_USER, LIST_USERS, CLEAN_UP
	}

	private class DynamoDBManagerTaskResult {
		private DynamoDBManagerType taskType;
		private String tableStatus;

		public DynamoDBManagerType getTaskType() {
			return taskType;
		}

		public void setTaskType(DynamoDBManagerType taskType) {
			this.taskType = taskType;
		}

		public String getTableStatus() {
			return tableStatus;
		}

		public void setTableStatus(String tableStatus) {
			this.tableStatus = tableStatus;
		}
	}
}
