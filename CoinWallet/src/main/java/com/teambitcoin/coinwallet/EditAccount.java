package com.teambitcoin.coinwallet;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    }
}
