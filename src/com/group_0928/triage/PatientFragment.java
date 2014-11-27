package com.group_0928.triage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class PatientFragment extends Fragment {

	private Patient mPatient;
	private RecordAdapter mAdapter;
	private TextView mBirthDate;
	private TextView mHealthCard;
	private CheckBox mSeen;
	private AlertDialog.Builder mNewVital;
	private AlertDialog.Builder mNewRecord;
	private AlertDialog.Builder mPrescriptions;
	private ExpandableListView mRecords;
	
	public static final String EXTRA_PATIENT_ID =
			"com.example.emergencyroom";
	
	/**
	 * Creates a new PatientFragment with patientId inside of its bundle
	 * @param patientId
	 * @return - The new PatientFragment
	 */
	public static PatientFragment newInstance(UUID patientId){
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_PATIENT_ID, patientId);
		
		PatientFragment fragment = new PatientFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		UUID patientId = (UUID) getArguments().getSerializable(EXTRA_PATIENT_ID);
		mPatient = System.get(getActivity()).getPatient(patientId);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle SaveInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_patient, container, false);
		
		mBirthDate = (TextView) rootView.findViewById(R.id.patient_single_item_birthdateTextView);
		mBirthDate.setText("Birth Date: " + DateFormat.format("dd-MMM-yy", mPatient.getBirthDate()));
		
		mHealthCard = (TextView) rootView.findViewById(R.id.patient_single_item_healthcardTextView);
		mHealthCard.setText("Health Card: " + mPatient.getHealthCard());
		
		mRecords = (ExpandableListView) rootView.findViewById(R.id.patient_single_item_vital_list);
		mAdapter = new RecordAdapter(getActivity(), mPatient.getRecords());
		mRecords.setAdapter(mAdapter);
		mRecords.setGroupIndicator(null);
		/*mRecords.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				if(ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
					/*LayoutInflater factory = LayoutInflater.from(getActivity());
					View v = factory.inflate(R.layout.dialog_vitals, null);
					EditText temp = (EditText) v.findViewById(R.id.vital_temperature_editText);
					final EditText systolic = (EditText) v.findViewById(R.id.vital_systolic_editText);
					final EditText diastolic = (EditText) v.findViewById(R.id.vital_diastolic_editText);
					final EditText heartRate = (EditText) v.findViewById(R.id.vital_heart_rate_editText);
					mNewVital = new AlertDialog.Builder(activity).setTitle(R.string.vital_dialog_title).setView(v).
							setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if((diastolic.getText().toString().equals("") || 
											systolic.getText().toString().equals("") ||
											diastolic.getText().toString().equals("") ||
											heartRate.getText().toString().equals(""))){
										Toast.makeText(activity, R.string.new_vital_error, Toast.LENGTH_LONG).show();
									} 
					Toast.makeText(getActivity(), "long click", Toast.LENGTH_SHORT).show();
					return true;
				}
				return false;
			}
			
		});*/
		for(int i = 0; i < mAdapter.getGroupCount(); i++){
			mRecords.expandGroup(i);
		}
		return rootView;
	}
	
	/**
	 * Handles clicks to the action bar items. Either creates a record dialog
	 * and creates a new record with desired arrivalTime or saves all 
	 * patients and their relevant information to a file
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(getActivity());
			return true;
		case R.id.save_patient:
			try {
				System.get(getActivity()).saveAllPatients();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Toast.makeText(getActivity(), "Saved Patient " + mPatient.getName(), Toast.LENGTH_SHORT).show();
			return true;
		case R.id.create_record:
			LayoutInflater factory = LayoutInflater.from(getActivity());
			View v = factory.inflate(R.layout.dialog_visit_record, null);
			final DatePicker datePicker = (DatePicker) v.findViewById(R.id.record_date_picker);
			final TimePicker timePicker = (TimePicker) v.findViewById(R.id.record_time_picker);
			mNewRecord = new AlertDialog.Builder(getActivity()).setTitle("Create New Record").setView(v).
					setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
						
						/**
						 * Handles the specific action of creating a new record for a given patient
						 * from the date selected within the dialog
						 */
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Calendar arrivalTime = new GregorianCalendar();
							arrivalTime.set(Calendar.DATE, datePicker.getDayOfMonth());
							arrivalTime.set(Calendar.MONTH, datePicker.getMonth());
							arrivalTime.set(Calendar.YEAR, datePicker.getYear());
							arrivalTime.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
							arrivalTime.set(Calendar.MINUTE, timePicker.getCurrentMinute());
							mPatient.createRecord(arrivalTime.getTime(), false, "N/A");
							mAdapter.notifyDataSetChanged();}
						}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			mNewRecord.show();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
/**
 * Super class descriptions essential describe all actions, any
 * other functionality has been described
 * 	
 * @author g3orozco
 */
private class RecordAdapter extends BaseExpandableListAdapter{
	
	private final ArrayList<VisitRecord> records;
	public LayoutInflater inflater;
	public Activity activity;
	
	
	public RecordAdapter(Activity act, ArrayList<VisitRecord> records) {
		activity = act;
		this.records = records;
		inflater = act.getLayoutInflater();
	}
	
	@Override
	public Object getChild(int recordposition, int vitalposition) {
		return records.get(recordposition).getVitals().get(vitalposition);
	}
	
	@Override
	public long getChildId(int arg0, int arg1) {
		return 0;
	}
	
	@Override
	public View getChildView(int recordPosition, int vitalPosition,
			boolean isLastVital, View convertView, ViewGroup parent) {
		
		final String vital = getChild(recordPosition, vitalPosition).toString();
		TextView text = null;
		if (convertView == null){
			convertView = inflater.inflate(R.layout.vitals_group, null);
		}
		text = (TextView) convertView.findViewById(R.id.TextView1);
		text.setText(vital);
		convertView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Toast.makeText(activity, vital,
						Toast.LENGTH_SHORT).show();
			}
			
		});
		return convertView;
	}

	@Override
	public int getChildrenCount(int recordPosition) {
		return records.get(recordPosition).getVitals().size();
	}

	@Override
	public Object getGroup(int recordPosition) {
		return records.get(recordPosition);
	}

	@Override
	public int getGroupCount() {
		return records.size();
	}

	@Override
	public long getGroupId(int arg0) {
		return 0;
	}

	
	@Override
	public View getGroupView(int recordPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null){
			convertView = inflater.inflate(R.layout.record_group, null);
		}
		//Bug arround seen button
		VisitRecord record =  (VisitRecord) getGroup(recordPosition);
		CheckedTextView recordName = (CheckedTextView) convertView.findViewById(R.id.record_name);
		recordName.setText("Record #: " + (recordPosition+1));
		recordName.setChecked(isExpanded);
		
		TextView arrivalTime = (TextView) convertView.findViewById(R.id.record_arrival);
		arrivalTime.setText("Arrived: " + record.toString());
		
		TextView prescription = (TextView) convertView.findViewById(R.id.record_prescription);
		prescription.setText("Prescriptions: " + record.getPrescription());
		
		mSeen = (CheckBox) convertView.findViewById(R.id.record_seen_checkbox);
		Log.d("record seen", Boolean.toString(record.getSeen()));
		mSeen.setChecked(record.getSeen());
		mSeen.setOnClickListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				recordPosition
				.setSeen(!record.getSeen());
				
			}
		});
		
		ImageButton actionButton = (ImageButton) convertView.findViewById(R.id.new_vital_button);
		actionButton.setFocusable(false);
		if (System.get(activity).getLoginInfo() == 1){
		actionButton.setOnClickListener(new OnClickListener() {
			
			/**
			 * Handles clicks of the newVitalButton, creating a dialog where vitals can be input
			 * then creating a new vital with the given data
			 */
			@Override
			public void onClick(View arg0) {
				LayoutInflater factory = LayoutInflater.from(activity);
				View v = factory.inflate(R.layout.dialog_vitals, null);
				final EditText temp = (EditText) v.findViewById(R.id.vital_temperature_editText);
				final EditText systolic = (EditText) v.findViewById(R.id.vital_systolic_editText);
				final EditText diastolic = (EditText) v.findViewById(R.id.vital_diastolic_editText);
				final EditText heartRate = (EditText) v.findViewById(R.id.vital_heart_rate_editText);
				mNewVital = new AlertDialog.Builder(activity).setTitle(R.string.vital_dialog_title).setView(v).
						setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if((diastolic.getText().toString().equals("") || 
										systolic.getText().toString().equals("") ||
										diastolic.getText().toString().equals("") ||
										heartRate.getText().toString().equals(""))){
									Toast.makeText(activity, R.string.new_vital_error, Toast.LENGTH_LONG).show();
								} else{
									record.addVitals(Integer.parseInt(temp.getText().toString()),
											Integer.parseInt(systolic.getText().toString()),
											Integer.parseInt(diastolic.getText().toString()),
											Integer.parseInt(heartRate.getText().toString()),
											new Date());
									notifyDataSetChanged();
								}
							}
						});
				mNewVital.setCancelable(true);
				mNewVital.show();
			}
		});}
		else{
			actionButton.setOnClickListener(new OnClickListener() {
				
				/**
				 * Handles clicks of the newVitalButton, creating a dialog where vitals can be input
				 * then creating a new vital with the given data
				 */
				@Override
				public void onClick(View arg0) {
					LayoutInflater factory = LayoutInflater.from(activity);
					View v = factory.inflate(R.layout.dialog_prescription, null);
					final EditText prescription = (EditText) v.findViewById(R.id.prescription_editText);
					prescription.setText(record.getPrescription());
					mPrescriptions = new AlertDialog.Builder(activity).setTitle(R.string.vital_dialog_title).setView(v).
							setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if((prescription.getText().toString().equals(""))){
										record.setPrescription("N/A");
									} else{
										record.setPrescription(prescription.getText().toString());
										notifyDataSetChanged();
									}
								}
							});
					mPrescriptions.setCancelable(true);
					mPrescriptions.show();
				}
			});}
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return false;
	}
}
} 
