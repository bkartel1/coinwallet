package com.teambitcoin.coinwallet;

import com.teambitcoin.coinwallet.api.BlockchainAPI;
import com.teambitcoin.coinwallet.models.User;
import com.teambitcoin.coinwallet.models.UserWrapper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class SendBitcoins extends Activity {
	private static final String[] BaseAddresses = {
		"14C8BUdJfH2Lmgyap3eu286fMRwTXwUjeZ",
		"17gGADVK7b9wXQkmcC9NZ2JWjcWi537oWp",
		"116EraDcGrwq9Ahgp8Pnq8muNG5i4BQzdp",
		"12dV4keu8Adzce2mqMmoghgCdmvPcpoJ14"
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_bitcoins);
        
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(this, 
        		android.R.layout.simple_list_item_1, BaseAddresses);
        AutoCompleteTextView btcAddressTxtView = (AutoCompleteTextView) findViewById(R.id.btc_address);
        btcAddressTxtView.setAdapter(autoCompleteAdapter);
	}
	
	public void sendBitcoins(View view){
		String btcAddress = ((EditText)findViewById(R.id.btc_address)).getText().toString();
		String amountStr = ((EditText)findViewById(R.id.amount_in_satoshis)).getText().toString();
		int amount = 0;
		if(amountStr != null && !amountStr.equals("")){
			amount = Integer.valueOf(amountStr);
		}
		if(!isValidAddress(btcAddress)){
			Toast.makeText(this, "Please enter a valid bitcoin address", Toast.LENGTH_SHORT).show();
		}else{
			if(amount <= 0){
				Toast.makeText(this, "Please enter a valid amount of satoshis", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "Sending "+amount+" satoshis to "+btcAddress+"!", Toast.LENGTH_SHORT).show();
				try {
					new BlockchainAPI().sendPayment(new UserWrapper().getUserAccount(User.getLoggedInUser()), 
													btcAddress, amount);
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(this, "Transfer failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
				}
				startActivity(new Intent(this, AddressScreen.class));
			}
		}
		
	}
	
	private boolean isValidAddress(String btcAddress){
		if(btcAddress.length() >= 25 && btcAddress.length() <= 34 && btcAddress.startsWith("1")){
			return true;
		}else{
			return false;
		}
	}
}