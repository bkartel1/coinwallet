package com.teambitcoin.coinwallet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.inputmethod.*;
import com.teambitcoin.coinwallet.models.*;

public class Logout extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //chnage to main?????
        
        //Button button = (Button) findViewById(R.id.logout_button); //Add Logout button
        //button.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {

            }
        })	;
    }
	
    public void onClick(View v) {
    	User.logout();
    	startActivity(new Intent(this, MainActivity.class));
    }
}