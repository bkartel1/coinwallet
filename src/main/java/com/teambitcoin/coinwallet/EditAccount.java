package main.java.com.teambitcoin.coinwallet;

import com.teambitcoin.coinwallet.R;

import main.java.com.teambitcoin.coinwallet.api.*;
import main.java.com.teambitcoin.coinwallet.models.*;
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
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_account);
    	
    	//TextView usernameDispaly; //display username
    	TextView guidDisplay = (TextView) findViewById(R.id.settings_guid_textview);	  //display GUID
    	guidDisplay.setText(User.getLoggedInUser().getGUID());	
    	
    	final EditText passwordField = (EditText) findViewById(R.id.new_qna_password_field);
    	final EditText questionField = (EditText) findViewById(R.id.new_question_field);
    	final EditText answerField = (EditText) findViewById(R.id.new_answer_field);
		
    	
	
	Button newQnAButton = (Button) findViewById(R.id.change_qna_button);
	
	newQnAButton.setOnClickListener(new View.OnClickListener() {

		public void onClick(View v) {
			if(!User.getLoggedInUser().authenticate(passwordField.getText().toString())){
				Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
				return;
			}
			User.getLoggedInUser().setQnA(questionField.getText().toString(), answerField.getText().toString());
			Toast.makeText(getApplicationContext(), "Security Question & Answer Changed", Toast.LENGTH_LONG).show();
		}

			
	});

}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}
