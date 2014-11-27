package com.group_0928.triage;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private EditText username;
	private EditText password;
	private Button loginButton;
	
	
	/**
	 * When the activity is started, creates a view and wires the
	 * nurse login button 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		username = (EditText) findViewById(R.id.login_username);
		password = (EditText) findViewById(R.id.login_password);
		
		loginButton = (Button) findViewById(R.id.nurse_login_button);
		loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					System.get(getBaseContext()).login(username.getText().toString(), password.getText().toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (System.get(getBaseContext()).getLoginInfo() == -1){
					Toast.makeText(getBaseContext(), "Incorrect Username/Password, please try again",
							Toast.LENGTH_SHORT).show();
				}else{
				Intent i = new Intent(LoginActivity.this, HomeActivity.class);
				startActivityForResult(i, 0);
				}
			}
		});
	}
}
