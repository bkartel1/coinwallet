package com.teambitcoin.coinwallet;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.teambitcoin.coinwallet.api.BlockchainAPI;
import com.teambitcoin.coinwallet.api.Conversion;
import com.teambitcoin.coinwallet.models.User;

public class EditAccount extends Activity {
    String username;
    String oldPassword;
    String newPassword;
    String guid;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account);
        
        // TextView usernameDispaly; //display username
        // display GUID
        TextView guidDisplay = (TextView) findViewById(R.id.settings_guid_textview);
        
        guidDisplay.setText(User.getLoggedInUser().getGUID());
        final EditText passwordField = (EditText) findViewById(R.id.new_qna_password_field);
        final EditText questionField = (EditText) findViewById(R.id.new_question_field);
        final EditText answerField = (EditText) findViewById(R.id.new_answer_field);
        Button newQnAButton = (Button) findViewById(R.id.change_qna_button);
        
        newQnAButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!User.getLoggedInUser().authenticate(passwordField.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                User.getLoggedInUser().setQnA(questionField.getText().toString(), answerField.getText().toString());
                Toast.makeText(getApplicationContext(), "Security Question & Answer Changed", Toast.LENGTH_LONG).show();
            }
        });
        
        //Setup currency selection
        Spinner currencySelector = (Spinner) findViewById(R.id.currency_selection);
        List<Conversion.Currency> currencies;
        try{
        	currencies = new BlockchainAPI().getFiatRates();
        }catch(Exception e){
        	e.printStackTrace();
        	return;
        }
        final List<String> currencyNames = new ArrayList<String>();
        for(Conversion.Currency c : currencies){
        	currencyNames.add(c.getName());
        }
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_spinner_item, currencyNames);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        currencySelector.setAdapter(currencyAdapter);
        currencySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				User.getLoggedInUser().setCurrency(currencyNames.get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				
			}
		});
    }
}
