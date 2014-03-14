package com.teambitcoin.coinwallet.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Conversion {
	private static HashMap<String, Currency> supportedCurrencies = null;
	private static List<Currency> currencies;
	public Conversion() throws IOException {
		if(supportedCurrencies == null){
			//List<Currency> currencies;
			try {
				currencies = new BlockchainAPI().getFiatRates();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			supportedCurrencies = new HashMap<String, Conversion.Currency>();
			for(Currency c : currencies){
				supportedCurrencies.put(c.getName(), c);
			}
		}
	}
	
	public List<Currency> getCurrencies(){
		return currencies;
	}

	public double toBTC(double amount, String currency) throws Exception {
		if (!supportedCurrencies.containsKey(currency)) {
			throw new Exception("Invalid Currency");
		}
		return toBTC(amount, supportedCurrencies.get(currency));
	}

	public double toBTC(double amount, Currency currency) {
		double rate = currency.getConversionRate();
		return amount / rate;
	}

	public double toMoney(double amount, String currency) throws Exception {
		if (!supportedCurrencies.containsKey(currency)) {
			throw new Exception("Invalid Currency");
		}
		return toMoney(amount, supportedCurrencies.get(currency));
	}

	public double toMoney(double amount, Currency currency) {
		double rate = currency.getConversionRate();
		return amount * rate;
	}

	protected void addCurrency(Currency currency) {
		supportedCurrencies.put(currency.getName(), currency);
	}

	public String[] getCurrencyList() {
		String[] list = supportedCurrencies.keySet().toArray(new String[0]);
		return list;
	}

	public static class Currency {
		private String name;
		private double conversionRate;
		private String symbol;

		public Currency(String name, double rate, String symbol) {
			this.name = name;
			this.conversionRate = rate;
			this.symbol = symbol;
		}

		public double getConversionRate() {
			return conversionRate;
		}

		public String getName() {
			return name;
		}

		public String getSymbol() {
			return symbol;
		}
	}
}
