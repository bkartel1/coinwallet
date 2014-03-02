package com.teambitcoin.coinwallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.teambitcoin.coinwallet.api.BlockchainAPI;
import com.teambitcoin.coinwallet.models.User;
import com.teambitcoin.coinwallet.models.UserWrapper;

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
        
        ((TextView) findViewById(R.id.balance_in_send_coins))
        	.setText("Balance: "+User.getLoggedInUser().getAccountBalance()+" satoshis");
	}
	
	public void sendBitcoins(View view){
		final Context that = this; 
		final String btcAddress = ((EditText)findViewById(R.id.btc_address)).getText().toString();
		final User user = User.getLoggedInUser();
		String amountStr = ((EditText)findViewById(R.id.amount_in_satoshis)).getText().toString();
		int amountTemp = 0;
		if(amountStr != null && !amountStr.equals("")){
			amountTemp = Integer.valueOf(amountStr);
		}
		final int amount = amountTemp;
		if(!isValidAddress(btcAddress)){
			Toast.makeText(this, "Please enter a valid bitcoin address", Toast.LENGTH_SHORT).show();
		}else{
			if(amount <= 0){
				Toast.makeText(this, "Please enter a valid amount of satoshis", Toast.LENGTH_SHORT).show();
			}else if(amount > user.getAccountBalance()){
				Toast.makeText(this, "Insufficient balance to make this transaction", Toast.LENGTH_SHORT).show();
			}else{
				new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Transaction Confirmation")
					.setMessage("Sending "+amount+" satoshis to "+btcAddress+"")
					.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(that, ""+amount+" satoshis to "+btcAddress+" are sent!", Toast.LENGTH_SHORT).show();
							try {
								new BlockchainAPI().sendPayment(new UserWrapper().getUserAccount(User.getLoggedInUser()), 
																btcAddress, amount);
								user.setAccountBalance(user.getAccountBalance() - amount);
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(that, "Transfer failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
							}
							startActivity(new Intent(that, AddressScreen.class));
						}
					})
					.setNegativeButton("Cancel", null)
					.show();
			}
		}
		
	}
	
	public void cancelSend(View view){
		finish();
	}
	
	private boolean isValidAddress(String btcAddress){
		if(btcAddress.length() >= 25 && btcAddress.length() <= 34 && btcAddress.startsWith("1")){
			return true;
		}else{
			return false;
		}
	}
}