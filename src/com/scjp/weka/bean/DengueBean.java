package com.scjp.weka.bean;

/**
 * @attribute Mean_TemperatureC {1,2,3,4}
 * @attribute Mean_Humidity {1,2,3,4}
 * @attribute Mean_Wind_SpeedKmph {1,2,3,4}
 * @attribute Dengue_cases {1,2,3,4}
 * @attribute Mean_SeaLevel_PressurekPa {1,2,3,4}
 * @author Bilal
 *
 */
public class DengueBean {

	private String meanTemp;
	private String meanHumidity;
	private String meanWindspeed;
	private String dengueCases;
	private String meanSeaPressure;
	
	public DengueBean(String meanTemp, String meanHumidity, String meanWindspeed, String dengueCases,
			String meanSeaPressure) {
		super();
		this.meanTemp = meanTemp;
		this.meanHumidity = meanHumidity;
		this.meanWindspeed = meanWindspeed;
		this.dengueCases = dengueCases;
		this.meanSeaPressure = meanSeaPressure;
	}
	public String getMeanTemp() {
		return meanTemp;
	}
	public void setMeanTemp(String meanTemp) {
		this.meanTemp = meanTemp;
	}
	public String getMeanHumidity() {
		return meanHumidity;
	}
	public void setMeanHumidity(String meanHumidity) {
		this.meanHumidity = meanHumidity;
	}
	public String getMeanWindspeed() {
		return meanWindspeed;
	}
	public void setMeanWindspeed(String meanWindspeed) {
		this.meanWindspeed = meanWindspeed;
	}
	public String getDengueCases() {
		return dengueCases;
	}
	public void setDengueCases(String dengueCases) {
		this.dengueCases = dengueCases;
	}
	public String getMeanSeaPressure() {
		return meanSeaPressure;
	}
	public void setMeanSeaPressure(String meanSeaPressure) {
		this.meanSeaPressure = meanSeaPressure;
	}
	
}
