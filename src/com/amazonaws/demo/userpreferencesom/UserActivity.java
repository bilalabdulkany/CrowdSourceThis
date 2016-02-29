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

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Text;

import com.amazonaws.demo.userpreferencesom.DynamoDBManager.CurrentWeather;
import com.amazonaws.demo.userpreferencesom.DynamoDBManager.UserPreference;

public class UserActivity extends Activity {

    private String userName = "";
    private String updateDate = "";
    private UserPreference userInfo = null;
   private CurrentWeather currentWeather=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_preference);

        userName = getIntent().getExtras().getString("USER_NAME");
        updateDate= getIntent().getExtras().getString("UPDATE_DATE");
        new GetUserInfoTask().execute();
    }

    private void setupActivity() {

    	//User table data
        String userName = userInfo.getUserName();// + " " + userInfo.getUserName();
        String statusView = userInfo.getStatusMessage();
        String latView= userInfo.getLatitude();
        String longView= userInfo.getLongitude();
        String updateDate=userInfo.getUpdateDate();
        
        //Weather table data
        String humidity=currentWeather.getMeanHumidity();
        String temp=currentWeather.getMeanTemp();
        String windSpeed=currentWeather.getMeanWind();
        String seaPressure=currentWeather.getSealevelpressure();
        String district=currentWeather.getDistrict();
        String city=currentWeather.getCity();
        
        
        //Apply to the view//**UserTable**//
        final TextView textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserName.setText("User: "+userName);
        
        final TextView textStatus=(TextView)findViewById(R.id.txtStatus_View);
        textStatus.setText("Dengue Cases: "+statusView);
        
        final TextView updateDateTxt=(TextView)findViewById(R.id.textUpdateDate);
        updateDateTxt.setText("Date: "+updateDate);
        
        
        final TextView textLat=(TextView)findViewById(R.id.txtLat);
        textLat.setText("Latitude: "+latView);
        
        final TextView textLong=(TextView)findViewById(R.id.txtLong);
        textLong.setText("Longitude: "+longView);
        
        //Apply to the view//**Weather Data**//
        final TextView textMeanHumidity = (TextView) findViewById(R.id.txtHumidity);
        textMeanHumidity.setText("Mean Humidity: "+humidity);
	
        final TextView textMeanTemp = (TextView) findViewById(R.id.txtTemp);
        textMeanTemp.setText("Mean Temp: "+temp);
        
        final TextView textDistrict = (TextView) findViewById(R.id.txtDistrict);
        textDistrict.setText("District: "+district);
       
        final TextView textCity = (TextView) findViewById(R.id.txtCity);
        textCity.setText("City: "+city);
        
        
        final TextView textMeanSeaLevelPressure = (TextView) findViewById(R.id.txtPressure);
        textMeanSeaLevelPressure.setText("Mean Sea Level Pressure: "+seaPressure+"kPa");
        
        final TextView textMeanWind = (TextView) findViewById(R.id.txtWindSpeed);
        textMeanWind.setText("Mean wind speed kmph: "+windSpeed);
        
        
     
    }

    private class GetUserInfoTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {

        	  
            userInfo = DynamoDBManager.getUserPreference(userName,updateDate);
            Log.e("USERDATA", "got the current user");
            currentWeather=DynamoDBManager.getCurrentWeatherInstance(userName,updateDate);
            Log.e("USERDATA", "got the current weather from userdata info");
            return null;
        }

        protected void onPostExecute(Void result) {

            setupActivity();
        }
    }

    private class UpdateAttributeTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {

            DynamoDBManager.updateUserPreference(userInfo);

            return null;
        }
    }
}
