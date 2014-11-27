package com.group_0928.triage;

import java.util.ArrayList;
import java.util.UUID;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;

public class PatientPagerActivity extends FragmentActivity {
	private ViewPager mViewPager;
	private ArrayList<Patient> mPatients;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(mViewPager);
		setTitle(R.string.patients);
		UUID patientId = (UUID)getIntent()
				.getSerializableExtra(PatientFragment.EXTRA_PATIENT_ID);
		mPatients = System.get(this).getPatients();
		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm){

			@Override
			public Fragment getItem(int position) {
				Patient patient = mPatients.get(position);
				return PatientFragment.newInstance(patient.getId());
			}

			@Override
			public int getCount() {
				return mPatients.size();
			}
		});
		
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
		
			@Override
			public void onPageSelected(int position){
				Patient p = mPatients.get(position);
				if (p.getName() != null){
					setTitle(p.getName());
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {	
			}
		});
		for (int i = 0; i < mPatients.size(); i++){
			if(mPatients.get(i).getId().equals(patientId)){
				mViewPager.setCurrentItem(i);
				break;
			}
		}
	}
	/**
	 * When the activity is created, inflates all of the action bar items
	 * specified within /res/menu/patient_pager
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.patient_pager, menu);
		return super.onCreateOptionsMenu(menu);
	}
}
