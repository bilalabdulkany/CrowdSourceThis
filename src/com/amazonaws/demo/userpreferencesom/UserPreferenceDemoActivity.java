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

package com.amazonaws.demo.userpreferencesom;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.scjp.tracker.GPSTracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserPreferenceDemoActivity extends Activity {

    private static final String TAG = "UserPreferenceDemoActivity";
    public static AmazonClientManager clientManager = null;
    private android.widget.EditText txtStatus=null;
    private static String statusMessage="Not Set..";
    GPSTracker gps = null;
    private double latitude=0f;
    private double longitude=0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        clientManager = new AmazonClientManager(this);
        
        txtStatus = (EditText)findViewById(R.id.txtStatus);
        
      //  gps=new GPSTracker(UserPreferenceDemoActivity.this);
        
 final Button btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        
        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {		
				// create class object
		        gps = new GPSTracker(UserPreferenceDemoActivity.this);

				// check if GPS enabled		
		        if(gps.canGetLocation()){
		        	
		        	 latitude = gps.getLatitude();
		        	 longitude = gps.getLongitude();
		        	
		        	// \n is for new line
		        	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();	
		        }else{
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

                new DynamoDBManagerTask()
                        .execute(DynamoDBManagerType.CREATE_TABLE);
            }
        });

        final Button insertUsersBttn = (Button) findViewById(R.id.insert_users_bttn);
        insertUsersBttn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(TAG, "insertUsersBttn clicked.");

                new DynamoDBManagerTask()
                        .execute(DynamoDBManagerType.INSERT_USER);
            }
        });

        final Button listUsersBttn = (Button) findViewById(R.id.list_users_bttn);
        listUsersBttn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(TAG, "listUsersBttn clicked.");

                new DynamoDBManagerTask()
                        .execute(DynamoDBManagerType.LIST_USERS);
            }
        });

        final Button deleteTableBttn = (Button) findViewById(R.id.delete_table_bttn);
        deleteTableBttn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(TAG, "deleteTableBttn clicked.");

                new DynamoDBManagerTask().execute(DynamoDBManagerType.CLEAN_UP);
            }
        });
        
        final Button getWeatherData = (Button) findViewById(R.id.GetWeatherData);
        getWeatherData.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(TAG, "getWeatherData clicked.");
                String serverURL ="http://api.geonames.org/findNearbyPostalCodesJSON?lat=6.0536&lng=80.2117&username=merdocbilal";
                new LongOperation().execute(serverURL);
               // new DynamoDBManagerTask().execute(DynamoDBManagerType.CLEAN_UP);
            }
        });
    }
    
    
 // Class with extends AsyncTask class
    
    private class LongOperation  extends AsyncTask<String, Void, Void> {
          
        // Required initialization
         
        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(UserPreferenceDemoActivity.this);
        String data =""; 
         String uiUpdate,jsonParsed,serverText;
        
        //TextView uiUpdate = (TextView) findViewById(R.id.output);
        //TextView jsonParsed = (TextView) findViewById(R.id.jsonParsed);
        int sizeData = 0;  
        //EditText serverText = (EditText) findViewById(R.id.serverText);
         
         
        protected void onPreExecute() {
            // NOTE: You can call UI Element here.
              
            //Start Progress Dialog (Message)
            
            Dialog.setMessage("Please wait..");
            Dialog.show();
             
           /* try{
                // Set Request parameter
                data +="&" + URLEncoder.encode("data", "UTF-8") + "="+serverText.getText();
                     
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */ 
             
        }
  
        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
             
            /************ Make Post Call To Web Server ***********/
            BufferedReader reader=null;
    
                 // Send data 
                try
                { 
                   
                   // Defined URL  where to send data
                   URL url = new URL(urls[0]);
                      
                  // Send POST data request
        
                  URLConnection conn = url.openConnection(); 
                  conn.setDoOutput(true); 
                  OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
                  wr.write( data ); 
                  wr.flush(); 
               
                  // Get the server response 
                    
                  reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                  StringBuilder sb = new StringBuilder();
                  String line = null;
                 
                    // Read Server Response
                    while((line = reader.readLine()) != null)
                        {
                               // Append server response in string
                               sb.append(line + "");
                        }
                     
                    // Append Server Response To Content String 
                   Content = sb.toString();
                }
                catch(Exception ex)
                {
                    Error = ex.getMessage();
                }
                finally
                {
                    try
                    {
          
                        reader.close();
                    }
        
                    catch(Exception ex) {}
                }
             
            /*****************************************************/
            return null;
        }
          
        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.
              
            // Close progress dialog
            Dialog.dismiss();
              
            if (Error != null) {
                  
               Toast.makeText(getApplicationContext(), "output:"+Error, Toast.LENGTH_SHORT);// uiUpdate.setText("Output : "+Error);
                  
            } else {
               
                // Show Response Json On Screen (activity)
            	Toast.makeText(getApplicationContext(), "output:"+Error, Toast.LENGTH_LONG);
                 
             /****************** Start Parse Response JSON Data *************/
                 
                String OutputData = "";
                JSONObject jsonResponse;
                       
                try {
                       
                     /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                     jsonResponse = new JSONObject(Content);
                       
                     /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                     /*******  Returns null otherwise.  *******/
                     JSONArray jsonMainNode = jsonResponse.optJSONArray("postalCodes");
                       
                     /*********** Process each JSON Node ************/
   
                     int lengthJsonArr = jsonMainNode.length();  
   
                    // for(int i=0; i < lengthJsonArr; i++) 
                     {
                         /****** Get Object for each JSON node.***********/
                         JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                           
                         /******* Fetch node values **********/
                         String name       = jsonChildNode.optString("adminName2").toString();
                         String longit     = jsonChildNode.optString("lng").toString();
                         String latid = jsonChildNode.optString("lat").toString();
                           
                         
                         OutputData += " Name           : "+ name +"  "
                                     + "longitude      : "+ longit +"  "
                                     + "Time                : "+ latid +" " 
                                     +"-------------------------------------------------";
                         Log.i("District", name);
                          
                    }
                 /****************** End Parse Response JSON Data *************/    
                      
                     //Show Parsed Output on screen (activity)
                     Toast.makeText(getApplicationContext(), "output:"+OutputData, Toast.LENGTH_SHORT);
                     //jsonParsed.setText( OutputData );
                      
                       
                 } catch (JSONException e) {
           
                     e.printStackTrace();
                 }
   
                  
             }
        }
          
    }

    private class DynamoDBManagerTask extends
            AsyncTask<DynamoDBManagerType, Void, DynamoDBManagerTaskResult> {

        protected DynamoDBManagerTaskResult doInBackground(
                DynamoDBManagerType... types) {

            String tableStatus = DynamoDBManager.getTestTableStatus();
            statusMessage=txtStatus.getText().toString();
            Log.e("Status", statusMessage+"");

            DynamoDBManagerTaskResult result = new DynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(types[0]);

            if (types[0] == DynamoDBManagerType.CREATE_TABLE) {
                if (tableStatus.length() == 0) {
                    DynamoDBManager.createTable();
                }
            } else if (types[0] == DynamoDBManagerType.INSERT_USER) {
            	//Double.toString(gps.getLatitude());
            	//Double.toString(gps.getLongitude());
            	Log.e("Latitude", latitude +"");
            	Log.e("Longitude", longitude+"");
            	
            	//txtStatus.setText((txtStatus.getText().equals(""))?"Null":txtStatus.getText().toString());
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                	Log.e("Inserting", "Inserting users...");
                	Log.e("Inserting", "Status message..."+statusMessage);
                    DynamoDBManager.insertUsers(statusMessage,latitude,longitude);
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
                    Toast.makeText(
                            UserPreferenceDemoActivity.this,
                            "The test table already exists.\nTable Status: "
                                    + result.getTableStatus(),
                            Toast.LENGTH_LONG).show();
                }

            } else if (result.getTaskType() == DynamoDBManagerType.LIST_USERS
                    && result.getTableStatus().equalsIgnoreCase("ACTIVE")) {

                startActivity(new Intent(UserPreferenceDemoActivity.this,
                        UserListActivity.class));

            } else if (!result.getTableStatus().equalsIgnoreCase("ACTIVE")) {

                Toast.makeText(
                        UserPreferenceDemoActivity.this,
                        "The test table is not ready yet.\nTable Status: "
                                + result.getTableStatus(), Toast.LENGTH_LONG)
                        .show();
            } else if (result.getTableStatus().equalsIgnoreCase("ACTIVE")
                    && result.getTaskType() == DynamoDBManagerType.INSERT_USER) {
                Toast.makeText(UserPreferenceDemoActivity.this,
                        "Users inserted successfully!", Toast.LENGTH_SHORT).show();
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
