package com.group_0928.triage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.text.format.DateFormat;

public class VisitRecord implements Comparable<Object>{
	private ArrayList<Vitals> vitals;
	private String prescription;
	private int urgency;
	private boolean seen;
	private Date arrivalTime;
	
	/**
	 * Creates a new VisitRecord at arrivalTime, with prescription and seen
	 * @param arrivalTime
	 */
	public VisitRecord(Date arrivalTime, boolean seen, String prescription){
		this.arrivalTime = arrivalTime;
		this.prescription = prescription;
		this.seen = seen;
		this.vitals = new ArrayList<Vitals>();
	}

	/**
	 * Returns the list of Vitals within a record
	 * @return - the list of Vitals
	 */
	public ArrayList<Vitals> getVitals() {
		return vitals;
	}
	/**
	 * Creates a new vital from the specified data
	 * @param temperature - desired temperature
	 * @param systolic - desired systolic blood pressure
	 * @param diastolic - desired diastolic blood pressure
	 * @param heartRate - desired heart Rate
	 * @param dateCreated - date the vitals were recorded
	 */
	public void addVitals(int temperature, int systolic, int diastolic, int heartRate, Date dateCreated){
		this.vitals.add(0, new Vitals(temperature, systolic, diastolic, heartRate, dateCreated));
		this.setUrgency();
	}

	/**
	 * Returns the rrivalTime of a VisitRecord
	 * @return - the arrivaltime
	 */
	public Date getArrivalTime(){
		return arrivalTime;
	}
	
	@Override
	public String toString(){
		return (String) DateFormat.format("dd-MM-yy", arrivalTime);
	}
	
	public String getPrescription(){
		return prescription;
	}
	
	public boolean getSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public void setPrescription(String prescription){
		this.prescription = prescription;
	}
	
	private void setUrgency(){
		this.urgency = 0;
		Vitals recent = this.vitals.get(0);
		if (recent.getDiastolic() >= 90 || recent.getSystolic() >= 140){
			this.urgency++;
		}
		if (recent.getTemperature() >= 39){
			this.urgency++;
		}
		if (recent.getHeartRate() >= 100 || recent.getHeartRate() <= 50){
			this.urgency++;
		}
	}
	
	public int getUrgency(){
		return urgency;
	}

	@Override
	public int compareTo(Object obj) {
		if(((VisitRecord) obj).getArrivalTime().equals(this.getArrivalTime()))
			return 0;
		else if (((VisitRecord)obj).getArrivalTime().after(this.getArrivalTime()))
			return 1;
		else
		return -11;
	}
}
