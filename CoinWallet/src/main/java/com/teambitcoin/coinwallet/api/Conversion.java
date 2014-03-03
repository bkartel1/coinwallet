package com.teambitcoin.coinwallet.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.stream.JsonReader;

public class Conversion {
	private static final String[] API_URL = {"blockchain.info", "/ticker"};
	private static URL URL_CACHE;
	private static final int HASHMAP_STARTING_SIZE = 19;

	private HashMap<String,Currency> supportedCurrencies;

	public Conversion() throws IOException {
		supportedCurrencies = new HashMap<String, Currency>(HASHMAP_STARTING_SIZE);
		HttpsURLConnection connection = getNewConnection();
		JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream()));
		try{
			reader.beginObject();
			while (reader.hasNext()){
				parseCurrency(reader);
			}
			reader.endObject();
		} finally {
			reader.close();
		}
	}

	private void parseCurrency(JsonReader reader) throws IOException {
		String name = reader.nextName();
		String symbol = "";
		double rate = 0;

		reader.beginObject();
		while (reader.hasNext()){
			String entry = reader.nextName();
			if(entry.equals("15m")) {
					rate = reader.nextDouble();
			} else if(entry.equals("symbol")) {
					symbol = reader.nextString();
			} else {
					reader.skipValue();
			}
		}
		supportedCurrencies.put(name, new Currency(name, rate, symbol));
		reader.endObject();
	}

	private static URL genURL(String host, String file){
		try {
			return new URL("https", host, file);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	private static HttpsURLConnection getNewConnection(){
		if(URL_CACHE==null){
			URL_CACHE = genURL(API_URL[0], API_URL[1]);
		}
		try {
			return (HttpsURLConnection) URL_CACHE.openConnection();
		} catch (Exception e) {
			return null;
		}
	}

	public double toBTC(double amount, String currency) throws Exception{
		if (!supportedCurrencies.containsKey(currency)){
			throw new Exception("Invalid Currency");
		}
                return toBTC(amount, supportedCurrencies.get(currency));
        }

	public double toBTC(double amount, Currency currency){
		double rate = currency.getConversionRate();
		return amount/rate;
	}

        public double toMoney(double amount, String currency) throws Exception{
		if (!supportedCurrencies.containsKey(currency)){
			throw new Exception("Invalid Currency");
		}
                return toMoney(amount, supportedCurrencies.get(currency));
        }

	public double toMoney(double amount, Currency currency){
		double rate = currency.getConversionRate();
		return amount*rate;
	}

	protected void addCurrency(Currency currency) {
		supportedCurrencies.put(currency.getName(), currency);
	}

	public String[] getCurrencyList() {
		String[] list = supportedCurrencies.keySet().toArray(new String[0]);
		return list;
	}

	protected static class Currency {
		private String name;
		private double conversionRate;
		private String symbol;

		public Currency(String name, double rate, String symbol){
			this.name = name;
			this.conversionRate = rate;
			this.symbol = symbol;
		}

		public double getConversionRate(){
			return conversionRate;
		}

		public String getName(){
			return name;
		}

		public String getSymbol(){
			return symbol;
		}
	}
}
