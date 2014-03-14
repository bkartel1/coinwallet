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
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ShowExchangeRates extends Activity {
	private Conversion exchangeRates;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_exchange_rates);
		getExchangeRates();
	}
	private void getExchangeRates() {
		ListView listView = (ListView) findViewById(R.id.exchange_rates_list);
		try {
			exchangeRates = new Conversion();
		} catch (IOException e) {
			e.printStackTrace();
			//TODO: error flow later
		}
		List<Map<String, String>> entries = new ArrayList<Map<String, String>>();
		for(Currency exchangeRate : exchangeRates.getCurrencies()){
			Map<String, String> map = new HashMap<String, String>();
			map.put("symbol", exchangeRate.getName() + "(" + exchangeRate.getSymbol() + ")");
			map.put("rate", Double.toString(exchangeRate.getConversionRate()));
			entries.add(map);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, entries,
				android.R.layout.simple_list_item_2, new String[] {
						"rate", "symbol" }, new int[] {
						android.R.id.text1, android.R.id.text2 });
		listView.setAdapter(simpleAdapter);
		
	}

}
