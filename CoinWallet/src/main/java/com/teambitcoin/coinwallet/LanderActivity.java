package com.teambitcoin.coinwallet;

import com.teambitcoin.coinwallet.models.User;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LanderActivity extends Activity implements View.OnClickListener {
	TextView landerLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lander);
		setTitle(User.getLoggedInUser().getUsername());
		
    	Button addrScreenBtn = (Button) findViewById(R.id.addr_screen_btn);
    	addrScreenBtn.setOnClickListener(LanderActivity.this);
	}
	
	@Override
	public void onBackPressed() { 
//		   User.logout();
//		   super.onBackPressed();
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addr_screen_btn:
			addrScreenBtnClicked();
			break;
		}
	}
	
    public void addrScreenBtnClicked() {
    	startActivity(new Intent("com.teambitcoin.coinwallet.AddressScreen"));
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lander, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.action_settings:
	    	startActivity(new Intent(this, EditAccount.class));
	    	return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
	}
}
