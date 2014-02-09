package com.teambitcoin.coinwallet;

import com.teambitcoin.coinwallet.models.User;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class LanderActivity extends Activity {
	TextView landerLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lander);
		landerLabel = (TextView)findViewById(R.id.lander_label);
		landerLabel.setText("Welcome " + User.getLoggedInUser().getUsername());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lander, menu);
		return true;
	}

}
