package com.teambitcoin.coinwallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.teambitcoin.coinwallet.models.Transaction;
import com.teambitcoin.coinwallet.models.User;

public class ShowTransactions extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_transactions);
		ListView txListView = (ListView) findViewById(R.id.transactions_list);
		List<Transaction> txs = Transaction.retrieveTransactions(
				User.getLoggedInUser().getGUID());
		if(txs.size() == 0){
			((TextView) findViewById(R.id.no_transaction_txt)).setText("No transactions found");
		}else{
			List<Map<String, String>> txEntries = new ArrayList<Map<String, String>>();
			for(Transaction tx : txs){
				Map<String, String> map = new HashMap<String, String>();
				if(tx.getType().equals("sent")){
					map.put("message", "Sent " + tx.getAmount() + " satoshis to:");
				}else{
					map.put("message", "Received " + tx.getAmount() + " satoshis from:");
				}
				map.put("address", tx.getAddress());
				txEntries.add(map);
			}
			SimpleAdapter simpleAdapter = new SimpleAdapter(this, txEntries,
					android.R.layout.simple_list_item_2, new String[] {
							"message", "address" }, new int[] {
							android.R.id.text1, android.R.id.text2 });
			txListView.setAdapter(simpleAdapter);
		}
	}
}