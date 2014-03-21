package com.teambitcoin.coinwallet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teambitcoin.coinwallet.api.Conversion;
import com.teambitcoin.coinwallet.api.Conversion.Currency;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ShowExchangeRates extends Activity {
	private Conversion exchangeRates;
	private double amount = 1.0;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_exchange_rates);
		final EditText inputText = (EditText) findViewById(R.id.input_exchange_from_amount);
		inputText.setText("1");
		
		Button button = (Button) findViewById(R.id.button_go);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try{
					amount = Double.parseDouble(inputText.getText().toString());
					getExchangeRates();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		getExchangeRates();
	}
	private void getExchangeRates() {
		ListView listView = (ListView) findViewById(R.id.exchange_rates_list);
		TextView errorView = (TextView) findViewById(R.id.title_exchange_rates);
		try {
			exchangeRates = new Conversion();
			if(exchangeRates.getCurrencies().isEmpty()) {
				errorView.setText("N/A");
			}
			
		} catch (Exception e) {
			errorView.setText("N/A");
		}
		List<Map<String, String>> entries = new ArrayList<Map<String, String>>();
		for(Currency exchangeRate : exchangeRates.getCurrencies()){
			Map<String, String> map = new HashMap<String, String>();
			map.put("symbol", exchangeRate.getName() + "(" + exchangeRate.getSymbol() + ")");
			map.put("rate", Double.toString(amount * exchangeRate.getConversionRate()));
			entries.add(map);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, entries,
				android.R.layout.simple_list_item_2, new String[] {
						"rate", "symbol" }, new int[] {
						android.R.id.text1, android.R.id.text2 });
		listView.setAdapter(simpleAdapter);
		
	}

}
