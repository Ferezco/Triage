package com.group_0928.triage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;
import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.app.NavUtils;
import android.util.Log;

public class System {
	private ArrayList<Patient> mPatients;
	private static System sAllPatients;
	private int loginInfo;
	private Context appContext;

	public System(Context appContext){
		appContext = appContext;
		mPatients = new ArrayList<Patient>();
	}
	
	public static System get(Context c){
		if(sAllPatients == null){
			sAllPatients = new System(c.getApplicationContext());
		}
		sAllPatients.appContext = c;
		return sAllPatients;
	}
	/**
	 * Returns the Patient with UUID id if it exists, else null
	 * 
	 * @return - the Patient
	 */
	public Patient getPatient(UUID id){
		for( Patient p: mPatients){
			if(p.getId().equals(id)){
				return p;
			}
		}
		return null;
	}
	
	public int getLoginInfo(){
		return loginInfo;
	}
	
	/**
	 * Returns an ArrayList of all the patients
	 * 
	 * @return - The Array List of Patients
	 */
	public ArrayList<Patient> getPatients() {
		/*if (loginInfo == 1){
			
		}*/
		return mPatients;
	}
	
	/**
	 * Returns the Patient with health card healthCard, if it exists
	 * else null
	 * @param healthCard - Desired health card number
	 * @return Patient with desired health card number
	 */
	public Patient getPatientByHealthCard(String healthCard){
		for (Patient p: mPatients){
			if (p.getHealthCard().equals(healthCard)){
				return p;
			}
		}
		return null;
	}
	
	/**
	 * Adds Patient patient to the ArrayList of all patients
	 * then return patient
	 * 
	 * @return - The patient
	 */
	public void createPatient(String healthCard, String name, Date birthDate) {
		mPatients.add(new Patient(healthCard, name, birthDate));
	}
	
	/**
	 * Creates a record at arrivalTime for a given patient with a health card 
	 * number healthcardnumber
	 * @param healthCard - Health card number of desired patient
	 * @param arrivalTime - Arrival Time of the new record
	 */
	public void createRecord(String healthCard, Date arrivalTime, boolean seen, String prescription){
		this.getPatientByHealthCard(healthCard).createRecord(arrivalTime, seen, prescription);
	}
	
	/**
	 * Creates a new vital for a given record with arrivalTime arrivalTime
	 * for a given Patient with health card number healthCard
	 * @param healthCard - Health card number of desired Patient
	 * @param arrivalTime - Arrival Time of desired record
	 * @param temperature - desired temperature
	 * @param systolic - desired systolic blood pressure
	 * @param diastolic - desired diastolic blood pressure
	 * @param heartRate - desired heart Rate
	 * @param date - date the vitals were recorded
	 */
	public void createVital(String healthCard, Date arrivalTime, 
			int temperature, int systolic, int diastolic, int heartRate, Date dateCreated){
		this.getPatientByHealthCard(healthCard).getRecordByArrivalTime(arrivalTime)
		.addVitals(temperature, systolic, diastolic, heartRate, dateCreated);
	}
	
	public void login(String username, String password) throws IOException{
		loginInfo = -1;
		AssetManager am = appContext.getAssets();
		Scanner scanner = new Scanner(am.open("passwords.txt"));
		String[] reader;
		while(scanner.hasNext()){
			reader = scanner.nextLine().split(",");
			if (reader[0].equals(username) && reader[1].equals(password)){
				if (reader[2].equals("nurse")){
					loginInfo = 1;
				}else if( reader[2].equals("physician")){
					loginInfo = 2;
				}
				break;
			}
		}
	}
	
	public void loadAllPatients() throws IOException, ParseException {
		String[] reader;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
		Scanner scanner;
		try {
			scanner = new Scanner(appContext.openFileInput("patient_records.txt"));
			Log.d("patients", "completed reading from dynamic");
		} catch (FileNotFoundException e) {
			AssetManager am = appContext.getAssets();
			scanner = new Scanner(am.open("patient_records.txt"));
		}
		while (scanner.hasNextLine()){
			reader = scanner.nextLine().split(",");
			Log.d("dynamic", reader[1]);
			System.get(appContext).createPatient(reader[0], reader[1], df.parse(reader[2]));
		}
		scanner.close();
		scanner = new Scanner(appContext.openFileInput("visit_records.txt"));
		while (scanner.hasNextLine()){
			reader = scanner.nextLine().split(",");
			System.get(appContext).createRecord(reader[0], new Date(Long.parseLong(reader[1])),
					Boolean.parseBoolean(reader[2]), reader[3]);
		}
		scanner.close();
		scanner = new Scanner(appContext.openFileInput("vitals_records.txt"));
		while (scanner.hasNextLine()){
			reader = scanner.nextLine().split(",");
			System.get(appContext).createVital(reader[0], new Date(Long.parseLong(reader[1])),
					Integer.parseInt(reader[2]),Integer.parseInt(reader[3]),
					Integer.parseInt(reader[4]), Integer.parseInt(reader[5]),
					new Date(Long.parseLong(reader[6])));
		}
		scanner.close();
	}
	/**
	 * Deletes previously written storage files, then recreates and writes
	 * and writes to them all of the current Records and Vitals of all 
	 * Patients
	 * @param c - Context of the app when this method was called
	 * @throws IOException 
	 */
	public void saveAllPatients() throws IOException {
		FileOutputStream visit_records, vitals_records, patient_records;
		appContext.deleteFile("visit_records.txt");
		visit_records = appContext.openFileOutput("visit_records.txt", appContext.MODE_PRIVATE);
		OutputStreamWriter visits = new OutputStreamWriter(visit_records);
		
		appContext.deleteFile("vital_records.txt");
		vitals_records = appContext.openFileOutput("vitals_records.txt", appContext.MODE_PRIVATE);
		OutputStreamWriter vitals = new OutputStreamWriter(vitals_records);
		
		appContext.deleteFile("patient_records.txt");
		patient_records = appContext.openFileOutput("patient_records.txt", appContext.MODE_PRIVATE);
		OutputStreamWriter patients = new OutputStreamWriter(patient_records);
		for (Patient p: mPatients){
			patients.write(p.toString() + "\n");
			Log.d("write", p.toString());
			for (VisitRecord r: p.getRecords()){
				visits.write(String.format("%s,%d,%b,%s", p.getHealthCard(),r.getArrivalTime().getTime(),
						r.getSeen(), r.getPrescription())+"\n");
				Log.d("write", "Record " + String.format("%s,%d,%b,%s", p.getHealthCard(),r.getArrivalTime().getTime(),
						r.getSeen(), r.getPrescription()));
				for (Vitals v: r.getVitals()){
					Log.d("write", "Vital " + String.format("%s,%d",p.getHealthCard(),r.getArrivalTime().getTime())
							+ v.toSave());
					vitals.write(String.format("%s,%d",p.getHealthCard(),r.getArrivalTime().getTime())
							+ v.toSave()+"\n");
				}
			}
		}
		vitals.close();
		visits.close();
		patients.close();
}
}

