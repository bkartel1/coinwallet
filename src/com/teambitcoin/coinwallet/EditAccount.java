package com.teambitcoin.coinwallet;

import com.teambitcoin.coinwallet.api.*;
import com.teambitcoin.coinwallet.models.*;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
//import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.inputmethod.*;

public class EditAccount extends Activity implements View.OnClickListener{
	String username;
	String oldPassword;
	String newPassword;
	String guid;
	
	Account account;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_account);
	
		//Button addrScreenBtn = (Button) findViewById(R.id.currency_type);
    	
    	guid = account.getGuid();
    	oldPassword = account.getPassword();
    	username = account.getUsername();
    	
    	TextView usernameDispaly; //display username
    	TextView guidDisplay;	  //display GUID
    
    	final EditText editText1 = (EditText) findViewById(R.id.old_password_field);
    	EditText editText2 = (EditText) findViewById(R.id.new_password_field);
		
    	editText1.setOnEditorActionListener(new OnEditorActionListener() {
    		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    			boolean entered;
    			String temp;
    			temp = editText1.getText().toString();
    			
    			if (actionId == EditorInfo.IME_ACTION_DONE) {
    			//TODO
    			}
    				
    		}
    	});
    	
	
	Button button = (Button) findViewById(R.id.change_password);
	
	button.setOnClickListener(new View.OnClickListener() {

		public void onClick(View v) {
			//TODO Check password
			
		}

			
	});

}
	public void checkPassword(String password){
		
			if (password == oldPassword){
				//set newPassword for text2;
		}
	}
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	}
}
