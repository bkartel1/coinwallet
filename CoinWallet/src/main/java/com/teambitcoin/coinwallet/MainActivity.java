package com.teambitcoin.coinwallet;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.teambitcoin.coinwallet.models.Database;
import com.teambitcoin.coinwallet.models.User;

public class MainActivity extends Activity implements View.OnClickListener {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // User must not be logged in to view this activity
        if (User.getLoggedInUser() != null) {
            startActivity(new Intent(this, AddressScreen.class));
            return;
        }
        
        // adding the button object
        Button button = (Button) findViewById(R.id.forgot_button);
        button.setOnClickListener(this);
        
        findViewById(R.id.register_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
        Database.initializeDatabase(getApplicationContext());
    }
    
    public void forgotButtonClicked() {
        Context context = getApplicationContext();
        CharSequence text = "Successfully entered forgot password menu";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        startActivity(new Intent(this, ForgotPassword.class));
    }
    
    public void onClick(View v) {
        switch (v.getId()) {
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
        questionField = (EditText) findViewById(R.id.question_field);
        answerField = (EditText) findViewById(R.id.answer_field);
        User.getLoggedInUser().setQnA(questionField.getText().toString(), answerField.getText().toString());
        startActivity(new Intent(this, AddressScreen.class));
    }
    
    protected void registerClicked() {
        EditText usernameField, passwordField;
        usernameField = (EditText) findViewById(R.id.username_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        if (!User.isValidUsername(username)) {
            Toast.makeText(getApplicationContext(), "Error: Please enter a username and a password.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        User user;
        try {
            user = User.create(username, password);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        if (user == null) {
            Toast.makeText(getApplicationContext(), "Error: Username already taken", Toast.LENGTH_SHORT).show();
        }
        else {
            setContentView(R.layout.register_qna);
            findViewById(R.id.finish_register_button).setOnClickListener(this);
        }
    }
    
    protected void loginClicked() {
        EditText usernameField, passwordField;
        usernameField = (EditText) findViewById(R.id.username_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        User user = User.login(username, password);
        if (user == null) {
            Toast.makeText(getApplicationContext(), "Error: Username or password incorrect", Toast.LENGTH_SHORT).show();
        }
        else {
            startActivity(new Intent(this, AddressScreen.class));
        }
    }
}
