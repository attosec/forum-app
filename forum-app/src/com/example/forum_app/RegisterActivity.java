package com.example.forum_app;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class RegisterActivity extends Activity {
	
	//private Button btRegister;
	private EditText etNickname;
	private EditText etPassword;
	private EditText etPasswordConfirm;
	private Spinner spCountry;
	private EditText etEmail;
	private Spinner spGender;
	private TextView tvRegisterError; 
	
	private Pattern regexp_pattern;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		//this.btRegister = (Button) this.findViewById(com.example.forum_app.R.id.btRegister);
		this.tvRegisterError = (TextView) this.findViewById(com.example.forum_app.R.id.tvRegisterError);
		
		this.etNickname = (EditText) this.findViewById(com.example.forum_app.R.id.etNickname);
		this.etPassword = (EditText) this.findViewById(com.example.forum_app.R.id.etPassword);
		this.etPasswordConfirm = (EditText) this.findViewById(com.example.forum_app.R.id.etPasswordConfirm);
		this.spCountry = (Spinner) this.findViewById(com.example.forum_app.R.id.spCountry);
		this.etEmail = (EditText) this.findViewById(com.example.forum_app.R.id.etEmail);
		this.spGender = (Spinner) this.findViewById(com.example.forum_app.R.id.spGender); 
		
		Locale[] locales = Locale.getAvailableLocales();
		String[] countries = new String[locales.length];
		Set<String> strings = new HashSet<String>();

		for (int i = 0; i < countries.length; i++)
		{
			String country = locales[i].getDisplayCountry(new Locale(this.getString(R.string.country_language),this.getString( R.string.country_language_state))).trim();
			if (!country.isEmpty())
			{
				strings.add(country);
			}
		}
		
		countries = strings.toArray(new String[0]);
		java.util.Arrays.sort(countries);
		
		ArrayAdapter<String> country_adapter = new ArrayAdapter<String>(this,
		            android.R.layout.simple_spinner_item, countries);
		spCountry.setAdapter(country_adapter);
		
		this.regexp_pattern = Patterns.EMAIL_ADDRESS;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
	
	public void clickRegister(View view) {
		this.tvRegisterError.setText("");
		if(this.etNickname.getText().toString().trim().isEmpty())
		{
			this.tvRegisterError.setText(R.string.err_missing_nickname);
		}
		else if(this.etPassword.getText().toString().isEmpty() || this.etPasswordConfirm.getText().toString().isEmpty())
		{
			this.tvRegisterError.setText(R.string.err_missing_password);
		}
		else if(this.etPassword.getText().toString().length() < 6 || this.etPasswordConfirm.getText().toString().length() < 6 )
		{
			this.tvRegisterError.setText(R.string.err_too_short_passwords);
		}
		else if(!this.etPassword.getText().toString().equals(this.etPasswordConfirm.getText().toString()))
		{
			this.tvRegisterError.setText(R.string.err_unequal_passwords);
		}
		else if(this.etEmail.getText().toString().trim().isEmpty())
		{
			this.tvRegisterError.setText(R.string.err_missing_email);
		}
		else if(!this.regexp_pattern.matcher(this.etEmail.getText().toString()).matches())
		{
			this.tvRegisterError.setText(R.string.err_invalid_email);
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	
			builder.setMessage(R.string.registration_complete_message)
			       .setTitle(R.string.registration_complete_title);
	
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   Intent switch_to_main = new Intent(RegisterActivity.this, MainActivity.class);
			        	   startActivity(switch_to_main);
			           }
			       });
			AlertDialog dialog = builder.create();
			dialog.show();
			
		}
	}

}

