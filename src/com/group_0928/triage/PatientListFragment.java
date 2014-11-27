package com.group_0928.triage;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PatientListFragment extends ListFragment {

	private ArrayList<Patient> mPatients;
	private AlertDialog.Builder mSearch;
	private ProgressDialog pDialog;
	private PatientAdapter adapter;
	private AlertDialog.Builder newPatientDialog;
	private EditText newPatientName;
	private EditText newPatientHealthCard;
	private DatePicker newPatientDOB;
	private ImageButton newPatientButton;
	
	/**
	 * When the ListFragment is created, it loads and sets the list of patients
	 * to display
	 */
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if (System.get(getActivity()).getLoginInfo() == 1){
			getActivity().setTitle(R.string.nurse);
		}else{
			getActivity().setTitle(R.string.physician);
		}
		mPatients = System.get(getActivity()).getPatients();
		if (mPatients.size() == 0){
			new LoadAllPatients().execute();
		}
		adapter = new PatientAdapter(mPatients);
		setListAdapter(adapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v;
		if (System.get(getActivity()).getLoginInfo() == 1){
			v = inflater.inflate(R.layout.fragment_patient_list, container, false);
			newPatientButton = (ImageButton) v.findViewById(R.id.new_patient_button);
			newPatientButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					LayoutInflater factory = LayoutInflater.from(getActivity());
					View view1 = factory.inflate(R.layout.dialog_patient, null);
					newPatientName = (EditText) view1.findViewById(R.id.patient_name_editText);
					newPatientHealthCard = (EditText) view1.findViewById(R.id.patient_healthcard_editText);
					newPatientDOB = (DatePicker) view1.findViewById(R.id.patient_date_picker);
					newPatientDialog = new AlertDialog.Builder(getActivity()).setTitle("Create a new patient").setView(view1).
							setPositiveButton("Create", new DialogInterface.OnClickListener() {
								
							/**
							 * Handles the specific action of creating a new patient 
							 */
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (!(newPatientName.getText().toString().equals("") || 
										newPatientHealthCard.getText().toString().equals(""))){
									Calendar dateOfBirth = new GregorianCalendar();
									dateOfBirth.set(Calendar.DATE, newPatientDOB.getDayOfMonth());
									dateOfBirth.set(Calendar.MONTH, newPatientDOB.getMonth());
									dateOfBirth.set(Calendar.YEAR, newPatientDOB.getYear());
									System.get(getActivity()).createPatient(newPatientHealthCard.getText().toString()
											, newPatientName.getText().toString(), dateOfBirth.getTime());
									adapter.notifyDataSetChanged();
								}else{
									Toast.makeText(getActivity(), "Missing patient name or health card", Toast.LENGTH_SHORT).show();
								}
							}
						});
					newPatientDialog.show();
				}	
			});
			return v;
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	/**
	 * On resume checks if there has been a change in the data set
	 */
	@Override
	public void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
	}
	/**
	 * When an item is clicked starts intent to display the individual patient
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		Patient p = (Patient) (getListAdapter().getItem(position));
		
		Intent i = new Intent(getActivity(), PatientPagerActivity.class);
		i.putExtra(PatientFragment.EXTRA_PATIENT_ID, p.getId());
		startActivity(i);
	}

	/**
	 * Handles clicks to the action bar items. Either creates a search dialog
	 * and searches for desired patient or saves all patients and their
	 * relevant information to a file
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(getActivity());
			return true;
		case R.id.action_search:
			LayoutInflater factory = LayoutInflater.from(getActivity());
			View v = factory.inflate(R.layout.searchdialog, null);
			final EditText search = (EditText) v.findViewById(R.id.search_dialog);
			mSearch = new AlertDialog.Builder(getActivity()).setTitle("Search For Patient by Health Card Number").setView(v).
					setPositiveButton("Search", new DialogInterface.OnClickListener() {
						
					/**
					 * Handles the specific action of searching for a patient then starting
					 * a display activity after a health card number has been input through the dialog
					 */
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Patient p;
						if ((p = System.get(getActivity()).getPatientByHealthCard(search.getText().toString())) != null){
							Intent i = new Intent(getActivity(), PatientPagerActivity.class);
							i.putExtra(PatientFragment.EXTRA_PATIENT_ID, p.getId());
							startActivity(i);
						}else{
							Toast.makeText(getActivity(), R.string.search_error, Toast.LENGTH_SHORT).show();
						}
					}
				});
			mSearch.show();
			return true;
		case R.id.action_save_all:
			//Runs the actual save process as defined within AllPatients
			try {
				System.get(getActivity()).saveAllPatients();
				Toast.makeText(getActivity(), "Saved all patients", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private class PatientAdapter extends ArrayAdapter<Patient>{
		
		/**
		 * Creates an ArrayAdapter using the superclass constructor
		 * sets patients as data source
		 * @param patients - Set of data 
		 */
		public PatientAdapter(ArrayList<Patient> patients) {
			super(getActivity(), 0, patients);
		}
		/**
		 * Creates a view of the patient at index position in the data set 
		 * 
		 * @param position - Desired patient's position within ArrayList
		 * 
		 * @param convertView - View to be displayed for desired patient
		 * 
		 * @param parent - Parent view of all individual patient views
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			if(convertView == null){
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.list_item_patient, null);
			}
			Patient p = getItem(position);
			TextView nameTextView = 
					(TextView) convertView.findViewById(R.id.patient_list_item_nameTextView);
			nameTextView.setText(p.getName());
			TextView vitalsTextView = 
					(TextView) convertView.findViewById(R.id.patient_list_item_vitalsTextView);
			TextView arrivalTextView = 
					(TextView) convertView.findViewById(R.id.patient_list_item_arrivalTextView);
			if(p.getRecords().size() > 0){
				if(p.getRecentRecord().getVitals().size() >= 1){
					vitalsTextView.setText(p.getRecentRecord().getVitals().get(0).toString());
				}
				else{
					vitalsTextView.setText(R.string.no_vitals);
				} 
				arrivalTextView.setText(DateFormat.format("dd-MMM h:mm ", p.getRecentRecord().getArrivalTime()));
			}
			else {
				vitalsTextView.setText(R.string.no_records);
				arrivalTextView.setText("");
			}
			return convertView;
		}
	}
	
	class LoadAllPatients extends AsyncTask<String, String, Void>{
		
		@Override
		/**
		 * Creates a loading dialog while all saved data is read from files
		 */
		protected void onPreExecute(){
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading..");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		/**
		 * Loads all saved data in a background thread
		 */
		protected Void doInBackground(String... arg0) {
			try {
				System.get(getActivity()).loadAllPatients();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
		}
		/**
		 * After patient info has been loaded clears the loading dialog
		 * and recreates the list of patients
		 */
		@Override
		protected void onPostExecute(Void result){
			super.onPostExecute(result);
			pDialog.dismiss();
			adapter = new PatientAdapter(mPatients);
			setListAdapter(adapter);
		}
	}
}
