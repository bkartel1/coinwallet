package com.teambitcoin.coinwallet;

//import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
//import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.inputmethod.*;
import com.teambitcoin.coinwallet.models.*;

public class SendBitcoins extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_bitcoins);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}
	
	public void sendBitcoins(View view){
		Toast.makeText(this, "SENDING BITCOINS!!!", Toast.LENGTH_SHORT).show();
	}
}