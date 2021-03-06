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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.scjp.tracker.GPSTracker;

import android.util.Log;

public class DynamoDBManager {

    private static final String TAG = "DynamoDBManager";
    private static GPSTracker gps = null;

    /*
     * Creates a table with the following attributes: Table name: testTableName
     * Hash key: userNo type N Read Capacity Units: 10 Write Capacity Units: 5
     */
    public static void createTable() {
    	createTable(Constants.UserTableName, 10l, 10l, "userName", "S", "updateDate", "S");
    	createTable(Constants.WeatherDataTableName, 10l, 10l, "userName", "S", "updateDate", "S");
    }

    
    /**
     * Sample create table function
     * @param tableName
     * @param readCapacityUnits
     * @param writeCapacityUnits
     * @param hashKeyName
     * @param hashKeyType
     * 
     */
    private static void createTable(
            String tableName, long readCapacityUnits, long writeCapacityUnits, 
            String hashKeyName, String hashKeyType) {

            createTable(tableName, readCapacityUnits, writeCapacityUnits,
                hashKeyName, hashKeyType, null, null);
        }

        private static void createTable(
            String tableName, long readCapacityUnits, long writeCapacityUnits, 
            String hashKeyName, String hashKeyType, 
            String rangeKeyName, String rangeKeyType) {
        	 AmazonDynamoDBClient ddb = MainDenguePredictorActivity.clientManager
                     .ddb();

            try {
            	Log.d(TAG, "Create table called");
                ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
                keySchema.add(new KeySchemaElement()
                    .withAttributeName(hashKeyName)
                    
                    .withKeyType(KeyType.HASH));
                
                ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
                attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName(hashKeyName)
                    .withAttributeType(hashKeyType));

                if (rangeKeyName != null) {
                    keySchema.add(new KeySchemaElement()
                        .withAttributeName(rangeKeyName)
                        .withKeyType(KeyType.RANGE));
                    attributeDefinitions.add(new AttributeDefinition()
                        .withAttributeName(rangeKeyName)
                        .withAttributeType(rangeKeyType));
                }

                CreateTableRequest request = new CreateTableRequest()
                        .withTableName(tableName)
                        .withKeySchema(keySchema)
                        .withProvisionedThroughput( new ProvisionedThroughput()
                            .withReadCapacityUnits(readCapacityUnits)
                            .withWriteCapacityUnits(writeCapacityUnits));


                request.setAttributeDefinitions(attributeDefinitions);

                Log.d(TAG, "Sending Create table request");
                ddb.createTable(request);
                Log.d(TAG, "Create request response successfully recieved");

            } catch (AmazonServiceException ex) {
            	 Log.e(TAG, "Error sending create table request", ex);
                 MainDenguePredictorActivity.clientManager
                         .wipeCredentialsOnAuthError(ex);
            }
        }
    /*
     * Retrieves the table description and returns the table status as a string.
     */
    public static String getTestTableStatus() {

        try {
            AmazonDynamoDBClient ddb = MainDenguePredictorActivity.clientManager
                    .ddb();

            DescribeTableRequest request = new DescribeTableRequest()
                    .withTableName(Constants.UserTableName);
            DescribeTableResult result = ddb.describeTable(request);

            String status = result.getTable().getTableStatus();
            return status == null ? "" : status;

        } catch (ResourceNotFoundException e) {
        } catch (AmazonServiceException ex) {
            MainDenguePredictorActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return "";
    }

    /*
     * Inserts ten users with userNo from 1 to 10 and random names.
     */
    static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    static String updateStaticDate = dateFormatter.format(new Date());
    public static void insertUsers(String status,double latitude, double longitude, String date) {
        AmazonDynamoDBClient ddb = MainDenguePredictorActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
       
        

        
        try {
                UserPreference userPreference = new UserPreference();
                userPreference.setUserName("Bilal");
                userPreference.setLatitude(latitude+"");
                userPreference.setStatusMessage(status);
                userPreference.setLongitude(longitude+"");
                userPreference.setUpdateDate(date);
                Log.d(TAG, "Inserting users " + latitude+" "+longitude);
                mapper.save(userPreference);
                Log.d(TAG, "Users inserted");
        } catch (AmazonServiceException ex) {
            Log.e(TAG, "Error inserting users");
            MainDenguePredictorActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }catch(Exception e){
        	Log.e(TAG, "Error in inserting users");
        }
    }
    
    /* Inserts ten users with userNo from 1 to 10 and random names.
    */
   public static void insertCurrentWeather(String district,String meanTemp,
    String meanHumidity,
    String meanWind,
    String sealevelpressure,
    int dengueCases,
    String latitude,
    String longitude, String userName, String date,String city) {
       AmazonDynamoDBClient ddb = MainDenguePredictorActivity.clientManager
               .ddb();
       DynamoDBMapper mapper = new DynamoDBMapper(ddb);
       
      // String updateStaticDate = dateFormatter.format(new Date());

       
       try {
               CurrentWeather currentWeather = new CurrentWeather();
               currentWeather.setUserName("Bilal");
               currentWeather.setDistrict(district);
               currentWeather.setMeanHumidity(meanHumidity);
               currentWeather.setLatitude(latitude+"");
               currentWeather.setMeanTemp(meanTemp);
               currentWeather.setLongitude(longitude+"");
               currentWeather.setMeanTemp(meanTemp);
               currentWeather.setMeanWind(meanWind);
               currentWeather.setSealevelpressure(sealevelpressure);
               currentWeather.setUpdateDate(date);
               currentWeather.setCity(city);
               Log.d(TAG, "Inserting Weather " + latitude+" "+longitude+" "+meanHumidity+" "+meanTemp+ " "+sealevelpressure+ " "+meanWind+ "--"+userName);
               mapper.save(currentWeather);
               Log.d(TAG, "Weather Data inserted!");
       } catch (AmazonServiceException ex) {
           Log.e(TAG, "Error inserting weather");
           MainDenguePredictorActivity.clientManager
                   .wipeCredentialsOnAuthError(ex);
       }catch(Exception e){
       	Log.e(TAG, "Error in inserting users");
       }
   }

    /*
     * Scans the table and returns the list of users.
     */
    public static ArrayList<UserPreference> getUserList() {

        AmazonDynamoDBClient ddb = MainDenguePredictorActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        try {
            PaginatedScanList<UserPreference> result = mapper.scan(
                    UserPreference.class, scanExpression);

            ArrayList<UserPreference> resultList = new ArrayList<UserPreference>();
            for (UserPreference up : result) {
                resultList.add(up);
            }
            return resultList;

        } catch (AmazonServiceException ex) {
            MainDenguePredictorActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return null;
    }

    /*
     * Retrieves all of the attribute/value pairs for the specified user.
     */
    public static UserPreference getUserPreference(String userName) {

        AmazonDynamoDBClient ddb = MainDenguePredictorActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            UserPreference userPreference = mapper.load(UserPreference.class,
            		userName);

            return userPreference;

        } catch (AmazonServiceException ex) {
            MainDenguePredictorActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return null;
    }
    /**
     * Get the UserPreference Table data given the below parameters
     * @param userName
     * @param updateDate
     * @return
     */
    public static UserPreference getUserPreference(String userName,String updateDate) {

        AmazonDynamoDBClient ddb = MainDenguePredictorActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
        	UserPreference userPreference = new UserPreference();
        	userPreference.setUserName(userName);
        	String queryString=updateDate;
        	
        	Condition rangeKeyCondition = new Condition()
        	        .withComparisonOperator(ComparisonOperator.BEGINS_WITH.toString())
        	        .withAttributeValueList(new AttributeValue().withS(queryString.toString()));
        	
        	DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
        	        .withHashKeyValues(userPreference)
        	        .withRangeKeyCondition("updateDate", rangeKeyCondition)
        	        .withConsistentRead(false);
             
        	PaginatedQueryList<UserPreference> result = mapper.query(UserPreference.class, queryExpression);

            return result.get(0);

        } catch (AmazonServiceException ex) {
            MainDenguePredictorActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return null;
    }

    /**
     * Get the CurrentWeather Table data for given date/time with the below parameters
     * @param userName
     * @param updateDate
     * @return
     */
    public static CurrentWeather getCurrentWeatherInstance(String username, String updateDate) {

        AmazonDynamoDBClient ddb = MainDenguePredictorActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
        	CurrentWeather userCurrentWeather = new CurrentWeather();
        	userCurrentWeather.setUserName(username);
        	String queryString=updateDate;
        	
        	Condition rangeKeyCondition = new Condition()
        	        .withComparisonOperator(ComparisonOperator.BEGINS_WITH.toString())
        	        .withAttributeValueList(new AttributeValue().withS(queryString.toString()));
        	
        	DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
        	        .withHashKeyValues(userCurrentWeather)
        	        .withRangeKeyCondition("updateDate", rangeKeyCondition)
        	        .withConsistentRead(false);
             
        	PaginatedQueryList<CurrentWeather> result = mapper.query(CurrentWeather.class, queryExpression);
        	Log.e("RESULT","Gettig the results.." );
            return result.get(0);

        } catch (AmazonServiceException ex) {
        	Log.e("ERROR", "Error in inserting current weather");
            MainDenguePredictorActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }

        return null;
    }

    
    /*
     * Updates one attribute/value pair for the specified user.
     */
    public static void updateUserPreference(UserPreference updateUserPreference) {

        AmazonDynamoDBClient ddb = MainDenguePredictorActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            mapper.save(updateUserPreference);

        } catch (AmazonServiceException ex) {
            MainDenguePredictorActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }

    /*
     * Deletes the specified user and all of its attribute/value pairs.
     */
    public static void deleteUser(UserPreference deleteUserPreference) {

        AmazonDynamoDBClient ddb = MainDenguePredictorActivity.clientManager
                .ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            mapper.delete(deleteUserPreference);

        } catch (AmazonServiceException ex) {
            MainDenguePredictorActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }

    /*
     * Deletes the test table and all of its users and their attribute/value
     * pairs.
     */
    public static void cleanUp() {

        AmazonDynamoDBClient ddb = MainDenguePredictorActivity.clientManager
                .ddb();

        DeleteTableRequest request = new DeleteTableRequest()
                .withTableName(Constants.UserTableName);
        DeleteTableRequest reqWeather = new DeleteTableRequest().withTableName(Constants.WeatherDataTableName);
        
        try {
            ddb.deleteTable(request);
            ddb.deleteTable(reqWeather);

        } catch (AmazonServiceException ex) {
            MainDenguePredictorActivity.clientManager
                    .wipeCredentialsOnAuthError(ex);
        }
    }

    @DynamoDBTable(tableName = Constants.UserTableName)
    public static class UserPreference {
        private String userName;
        private String statusMessage;
        private String latitude;
        private String longitude;
        private String updateDate;
        
        @DynamoDBRangeKey(attributeName = "updateDate")
        public String getUpdateDate() {
			return updateDate;
		}

		public void setUpdateDate(String updateDate) {
			this.updateDate = updateDate;
		}

		@DynamoDBAttribute(attributeName = "latitude")
        public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		
		@DynamoDBAttribute(attributeName = "statusMessage")
        public String getStatusMessage() {
			return statusMessage;
		}

		public void setStatusMessage(String statusMessage) {
			this.statusMessage = statusMessage;
		}
		
		@DynamoDBAttribute(attributeName = "longitude")
		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		@DynamoDBHashKey(attributeName = "userName")
        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

    }
    
    @DynamoDBTable(tableName = Constants.WeatherDataTableName)
    public static class CurrentWeather {
        private String meanTemp;
        private String meanHumidity;
        private String meanWind;
        private String sealevelpressure;
        private int dengueCases;
        private String latitude;
        private String longitude;
        private String updateDate;
        private String district;
        private String city;
        private String userName;
        
        @DynamoDBAttribute(attributeName = "city")
        public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		@DynamoDBHashKey(attributeName = "userName")
        public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		@DynamoDBRangeKey(attributeName = "updateDate")
        public String getUpdateDate() {
			return updateDate;
		}
        
		@DynamoDBAttribute(attributeName = "district")
        public String getDistrict() {
            return district;
        }
        public void setDistrict(String District) {
			this.district = District;
		}
        
        @DynamoDBAttribute(attributeName = "meanWind")
		public String getMeanWind() {
			return meanWind;
		}

		public void setMeanWind(String meanWind) {
			this.meanWind = meanWind;
		}

		@DynamoDBAttribute(attributeName = "sealevelpressure")
		public String getSealevelpressure() {
			return sealevelpressure;
		}

		public void setSealevelpressure(String sealevelpressure) {
			this.sealevelpressure = sealevelpressure;
		}

		@DynamoDBAttribute(attributeName = "dengueCases")
		public int getDengueCases() {
			return dengueCases;
		}

		public void setDengueCases(int dengueCases) {
			this.dengueCases = dengueCases;
		}

		public void setUpdateDate(String updateDate) {
			this.updateDate = updateDate;
		}

		@DynamoDBAttribute(attributeName = "latitude")
        public String getLatitude() {
			return latitude;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		
		@DynamoDBAttribute(attributeName = "meanHumidity")
        public String getMeanHumidity() {
			return meanHumidity;
		}

		public void setMeanHumidity(String meanHumidity) {
			this.meanHumidity = meanHumidity;
		}
		
		@DynamoDBAttribute(attributeName = "longitude")
		public String getLongitude() {
			return longitude;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		@DynamoDBAttribute(attributeName = "meanTemp")
        public String getMeanTemp() {
            return meanTemp;
        }

        public void setMeanTemp(String meanTemp) {
            this.meanTemp = meanTemp;
        }

    }

	
}
