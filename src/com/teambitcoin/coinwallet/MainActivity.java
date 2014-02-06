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
import com.teambitcoin.coinwallet.models.User;

public class MainActivity extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        //adding the button object
    	Button button = (Button) findViewById(R.id.button1);
    	button.setOnClickListener(this);
    	
    	// button to go to address screen (temporary until actual layout is implemented)
    	Button addrScreenBtn = (Button) findViewById(R.id.addr_screen_btn);
    	addrScreenBtn.setOnClickListener(this);
    	
    	
    }
    public void forgotButtonClicked(){
    	Context context = getApplicationContext();
    	CharSequence text = "Successfully entered forgot password menu";
    	int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
    	
    	startActivity(new Intent("com.teambitcoin.coinwallet.ForgotPassword"));
    }
    
    public void clickAddrScreenBtn() {
    	startActivity(new Intent("com.teambitcoin.coinwallet.AddressScreen"));
    }
    
    public void onClick(View v) {
    	switch (v.getId()){
    	case R.id.button1:
    		forgotButtonClicked();
    	case R.id.login_button:
    		loginClicked();
    	break;
    	// TODO: temporary only; remove this later
		case R.id.addr_screen_btn:
			clickAddrScreenBtn();
		break;
    	}
    	
	}
    
    protected void registerClicked(){
    	EditText usernameField, passwordField;
    	Context context = getApplicationContext();
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
    		//TODO: Enter Logged in page here...
    	}
    }

    protected void loginClicked(){
    	EditText usernameField, passwordField;
    	Context context = getApplicationContext();
    	usernameField = (EditText)findViewById(R.id.username_field);
    	passwordField = (EditText)findViewById(R.id.password_field);
    	String username = usernameField.getText().toString();
    	String password = passwordField.getText().toString();
    	User user = User.login(username, password);
    	if (user==null) {
    		TextView alert = (TextView)findViewById(R.id.alert_text);
    		alert.setText("Error: Username or password incorrect");
    	} else {
    		//TODO: Load logged in page here...
    	}
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
