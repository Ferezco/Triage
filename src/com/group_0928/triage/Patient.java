package com.group_0928.triage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import android.text.format.DateFormat;

public class Patient implements Comparable<Object> {
	
	private UUID id;
	private String healthCard;
	private String name;
	private Date birthDate;
	private int urgency;
	private ArrayList<VisitRecord> records;	
	
	/**
	 * Constructs a Patient with Health Card "healthCard", Name name and
	 * Date of Birth "birthDate"
	 * @param healthCard - Patients health card number
	 * @param name - Patients name
	 * @param birthDate - Patients date of birth
	 */
	public Patient(String healthCard, String name, Date birthDate){
		this.id = UUID.randomUUID();
		this.healthCard = healthCard;
		this.name = name;
		this.birthDate = birthDate;
		this.records = new ArrayList<VisitRecord>();
		if (birthDate.getTime() < (new Date().getTime() - 6.31139 * Math.pow(10, 10))){
			this.urgency =1;
		}
		else{
			this.urgency = 0;
		}
	}
	
	/**
	 * Returns a Patient's unique UUID
	 * 
	 * @return - the Patient's unique UUID
	 */
	public UUID getId(){
		return id;
	}
	
	/**
	 * Returns a Patient's name
	 * 
	 * @return - the Patient's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a Patient's date of birth 
	 * 
	 * @return - the Patient's date of birth
	 */
	public Date getBirthDate() {
		return birthDate;
	}
	
	/**
	 * Returns a Patient's health card number
	 * 
	 * @return - the Patient's health card number
	 */
	public String getHealthCard() {
		return healthCard;
	}
	
	/**
	 * Returns a Patient's most recent visit record
	 * 
	 * @return - the Patient's most recent visit record
	 */
	public VisitRecord getRecentRecord(){
		return records.get(0);
	}
	
	/**
	 * Returns a list of the Patient's visit records
	 * 
	 * @return - the set of visit records
	 */
	public ArrayList<VisitRecord> getRecords(){
		return records;
	}
	
	private int getUrgency() {
		return this.urgency + this.getRecentRecord().getUrgency();
	}
	
	/**
	 * Returns a record with arrival time arrivalTime, otherwise null
	 * @param arrivalTime - The desired arrival time
	 * @return - The desired record
	 */
	public VisitRecord getRecordByArrivalTime(Date arrivalTime){
		for (VisitRecord r: records){
			if (r.getArrivalTime().equals(arrivalTime)){
				return r;
			}
		}
		return null;
	}
	
	/**
	 * Creates and returns a new visit record with arrival time "arrivalTime"
	 * 
	 * @return - the newly created visit record
	 */
	public VisitRecord createRecord(Date arrivalTime, boolean seen, String prescription){
		this.records.add(0, new VisitRecord(arrivalTime, seen, prescription));
		Collections.sort(this.records);
		return this.getRecentRecord();
	}
	
	
	/**
	 * Returns a Patient's name
	 * 
	 * @return - the Patient's name
	 */
	@Override
	public String toString(){
		return String.format("%s,%s,%s",this.healthCard, this.name,(String) DateFormat.format("dd-MM-yy", this.birthDate));
	}

	@Override
	public int compareTo(Object obj) {
		if(((Patient) obj).getUrgency() == this.getUrgency())
			return 0;
		else if (((Patient)obj).getUrgency() > (this.getUrgency()))
			return 1;
		else
		return -1;
	}

	
}
