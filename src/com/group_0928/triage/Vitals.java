package com.group_0928.triage;

import java.util.Date;

public class Vitals implements Comparable<Object>{
	private Date datecreated;
	private int temperature;
	private int systolic;
	private int diastolic;
	private int heartRate;
	
	/**
	 * Creates a new instance of Vitals with  temperature, systolic,
	 * diastolic, hearRate and dateCreated date  
	 * @param temperature - desired temperature
	 * @param systolic - desired systolic blood pressure
	 * @param diastolic - desired diastolic blood pressure
	 * @param heartRate - desired heart Rate
	 * @param date - date the vitals were recorded
	 */
	public Vitals(int temperature, int systolic, int diastolic, int heartRate, Date date){
		this.temperature = temperature;
		this.systolic = systolic;
		this.diastolic = diastolic;
		this.heartRate = heartRate;
		this.datecreated = date;
	}
	
	/**
	 * Returns the date a vital was created
	 * @return - the date created
	 */
	public Date getDateCreated(){
		return datecreated;
	}
	
	/**
	 * Returns the temperature of a vital
	 * @return - The temperature
	 */
	public int getTemperature() {
		return temperature;
	}
	/**
	 * Returns the systolic blood pressure of a vital
	 * @return - The systolic blood pressure
	 */
	public int getSystolic() {
		return systolic;
	}
	/**
	 * Returns the disatolic blood pressure of a vital
	 * @return - The diastolic blood pressure
	 */
	public int getDiastolic() {
		return diastolic;
	}
	/**
	 * Returns the heart rate of a vital
	 * @return - The heart rate
	 */
	public int getHeartRate() {
		return heartRate;
	}
	/**
	 * Returns the string to be used for saving a vital
	 * in a text file
	 * @return - The string to be saved
	 */
	public String toSave(){
		return (String.format(",%d,%d,%d,%d,%d", temperature, systolic, diastolic,
				heartRate, datecreated.getTime()));
	}
	
	@Override
	public String toString(){
		return (String.format("Temp: %d BP: (%d / %d) Heart Rate: %d BPM",
				temperature, systolic, diastolic, heartRate));
	}
	

	@Override
	public int compareTo(Object obj) {
		if(((Vitals) obj).getDateCreated().equals(this.getDateCreated()))
			return 0;
		else if (((Vitals)obj).getDateCreated().after(this.getDateCreated()))
			return 1;
		else
		return -1;
	}
}
