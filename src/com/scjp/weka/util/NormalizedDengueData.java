package com.scjp.weka.util;

public class NormalizedDengueData {
	
	public static int getNormalizedTemp(double value){
		int norm=0;
		if(value>34){
			norm=4;
		}else if(value>30&&value<=34){
			norm=3;
		}else if(value>=25&&value<=30){
			norm=2;
		}else{
			norm=1;
		}
		return norm;
	}
	
	public static int getNormalizedHumidity(double value){		
		int norm=0;
		if(value>88){
			norm=4;
		}else if(value>72&&value<=88){
			norm=3;
		}else if(value>=56&&value<=72){
			norm=2;
		}else{
			norm=1;
		}
		return norm;		
	}
	
	public static int getNormalizedWindSpeed(double value){		
		int norm=0;
		if(value>20){
			norm=4;
		}else if(value>15&&value<=20){
			norm=3;
		}else if(value>=10&&value<=15){
			norm=2;
		}else{
			norm=1;
		}
		return norm;		
	}
	public static int getNormalizedPressure(double value){		
		int norm=0;
		if(value>101.5){
			norm=4;
		}else if(value>101&&value<=101.5){
			norm=3;
		}else if(value>=100.5&&value<=101){
			norm=2;
		}else{
			norm=1;
		}
		return norm;		
	}
	/**
	 * Get the normalized dengue cases given actual dengue cases.
	 * This is for machine learning.
	 */
	public static int getNormalizedDengueValues(double value){		
		int norm=0;
		if(value>20){
			norm=4;
		}else if(value>10&&value<=20){
			norm=3;
		}else if(value>=3&&value<=10){
			norm=2;
		}else{
			norm=1;
		}
		return norm;
		
	}
}
