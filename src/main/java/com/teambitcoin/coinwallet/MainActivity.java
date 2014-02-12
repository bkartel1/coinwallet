package com.teambitcoin.coinwallet;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.teambitcoin.coinwallet.*;
import com.teambitcoin.coinwallet.models.Database;
import com.teambitcoin.coinwallet.models.User;
import com.teambitcoin.coinwallet.models.Database;

public class MainActivity extends Activity implements View.OnClickListener {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (User.getLoggedInUser()!= null){//User must not be logged in to view this activity
        	startActivity(new Intent(this, LanderActivity.class));
        	return;
        }

        //adding the button object
    	Button button = (Button) findViewById(R.id.forgot_button);
    	button.setOnClickListener(this);

    	findViewById(R.id.register_button).setOnClickListener(this);
    	findViewById(R.id.login_button).setOnClickListener(this);
    	Database.initializeDatabase(getApplicationContext());    	
    }
    
    public void forgotButtonClicked(){
    	Context context = getApplicationContext();
    	CharSequence text = "Successfully entered forgot password menu";
    	int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
    	
    	startActivity(new Intent("com.teambitcoin.coinwallet.ForgotPassword"));
    }
        
    public void onClick(View v) {
    	switch (v.getId()){
    	case R.id.forgot_button:
    		forgotButtonClicked();
    		break;
    	case R.id.login_button:
    		loginClicked();
    		break;
    	case R.id.register_button:
    		registerClicked();
    		break;
    	case R.id.finish_register_button:
    		finishRegistration();
    		break;
    	}
	}
    
    protected void finishRegistration() {
    	EditText questionField, answerField;
    	questionField = (EditText)findViewById(R.id.question_field);
    	answerField = (EditText)findViewById(R.id.answer_field);
    	User.getLoggedInUser().setQnA(questionField.getText().toString(), answerField.getText().toString());
    	startActivity(new Intent(this, LanderActivity.class));
    }
    
    protected void registerClicked() {
    	EditText usernameField, passwordField;
    	usernameField = (EditText)findViewById(R.id.username_field);
    	passwordField = (EditText)findViewById(R.id.password_field);
    	String username = usernameField.getText().toString();
    	String password = passwordField.getText().toString();
    	if (!User.isValidUsername(username)){
    		TextView alert = (TextView)findViewById(R.id.alert_text);
    		alert.setText("Error: Invalid Username");
    		return;
    	}
    	User user;
    	try {
    		user = User.create(username, password);
    	} catch (Exception e) {
    		TextView alert = (TextView)findViewById(R.id.alert_text);
    		alert.setText(e.getMessage());
    		return;
    	}
    	if (user == null){
    		TextView alert = (TextView)findViewById(R.id.alert_text);
    		alert.setText("Error: Username already taken");
    	} else {
    		setContentView(R.layout.register_qna);
    		findViewById(R.id.finish_register_button).setOnClickListener(this);
    		//startActivity(new Intent(this, LanderActivity.class));
    	}
    }

    protected void loginClicked(){
    	EditText usernameField, passwordField;
    	usernameField = (EditText)findViewById(R.id.username_field);
    	passwordField = (EditText)findViewById(R.id.password_field);
    	String username = usernameField.getText().toString();
    	String password = passwordField.getText().toString();
    	User user = User.login(username, password);
    	if (user==null) {
    		TextView alert = (TextView)findViewById(R.id.alert_text);
    		alert.setText("Error: Username or password incorrect");
    	} else {
    		startActivity(new Intent(this, LanderActivity.class));
    	}
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu); //No Menu on login screen.
        return true;
    }
    
}
