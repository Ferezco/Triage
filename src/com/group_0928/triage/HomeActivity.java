package com.group_0928.triage;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;

public class HomeActivity extends Activity {
	
	/**
	 * When the activity is created content view is set then filled by
	 * by a new PatientListFragment 
	 */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
		getActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment == null){
        	fm.beginTransaction()
        	.add(R.id.fragment_container, new PatientListFragment())
        	.commit();
        }
    }
	
	/**
	 * When the activity is created, inflates all of the action bar items
	 * specified within /res/menu/nurse_home
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.home, menu);
			return super.onCreateOptionsMenu(menu);
	}
	
}
