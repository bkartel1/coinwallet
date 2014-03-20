package com.teambitcoin.coinwallet.api;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teambitcoin.coinwallet.models.Transaction;
import com.teambitcoin.coinwallet.models.User;

/**
 * This class is used to connect with Blockchain's API.
 * 
 * Example usage: Account newAccount = new BlockchainAPI().createAccount(<username>, <password>);
 * 
 * I decided to keep it as a normal class instead of a class with static methods, because it might be easier to adapt it
 * later if we need to do pooling or keep track of states, etc.
 * 
 * @author FT
 * 
 */
public class BlockchainAPI {
	private static boolean isTesting = true; //to be used during demo since we
									//don't want to use real bitcoins for testing payments
    
    /**
     * Creates a new Account with a single Bitcoin address in it.
     * 
     * @param username username associated with that account
     * @param password password of the user, must be at least 10 characters long
     * @return a new Account if the creation was successful, null otherwise
     * @throws Exception in case there was a problem creating the Account
     */
    public Account createAccount(final String username, final String password) throws Exception {
        return new AsyncTask<Void, Void, Account>() {
            @Override
            protected Account doInBackground(Void... voids) {
                try {
                    if (password.length() < 10) {
                        throw new Exception("ERROR: Password's length must be at least 10!");
                    }
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(
                            "http://blockchain.info/api/v2/create_wallet?api_code=LK75FDss&password=" + password);
                    String r = EntityUtils.toString(httpclient.execute(httpget).getEntity());
                    Log.v("BLOCKCHAIN_API", "Created client");
                    JsonObject response = (JsonObject) new JsonParser().parse(r);
                    Account account = new Account();
                    account.setUsername(username);
                    account.setPassword(password);
                    account.setGuid(response.get("guid").getAsString());
                    Log.v("BLOCKCHAIN_API", account.toString());
                    return account;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.execute().get();
    }
    
    /**
     * Gets all the addresses related to an account.
     * 
     * Example usage:
     * 
     * List<Address> addresses = new BlockchainAPI().getAddresses(new Account(<username>,<pass>,<guid>));
     * 
     * @param account account for which the addresses are wanted
     * @return a list of addresses
     * @throws Exception if the supplied account is not valid
     */
    public List<Address> getAddresses(final Account account) throws Exception {
        return new AsyncTask<Void, Void, List<Address>>() {
            @Override
            protected List<Address> doInBackground(Void... voids) {
                try {
                    if (account.getGuid() == null || account.getGuid().length() <= 0) {
                        throw new Exception("ERROR: Not a valid GUID!");
                    }
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet("http://blockchain.info/" + "/merchant/" + account.getGuid()
                            + "/list?api_code=LK75FDss&password=" + account.getPassword());
                    String r = EntityUtils.toString(client.execute(get).getEntity());
                    JsonObject response = (JsonObject) new JsonParser().parse(r);
                    
                    JsonArray addrs = response.get("addresses").getAsJsonArray();
                    List<Address> addresses = new ArrayList<Address>();
                    for (int i = 0; i < addrs.size(); i++) {
                        Address addr = new Address();
                        JsonObject addrObj = (JsonObject) addrs.get(i);
                        addr.setAddress(addrObj.get("address").getAsString());
                        addr.setBalance(addrObj.get("balance").getAsInt());
                        addr.setLabel(addrObj.get("label").getAsString());
                        addr.setTotalReceived(addrObj.get("total_received").getAsInt());
                        
                        addresses.add(addr);
                    }
                    return addresses;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.execute().get();
    }
    
    /**
     * Adds a new bitcoin address to the account.
     * 
     * @param account account to which the new address should be added
     * @param label the label associated with the newly generated address, if null, the label will be a random UUID
     * @return the new Address that was added
     * @throws Exception if the account is not valid
     */
    public Address generateNewAddress(final Account account, final String label) throws Exception {
        return new AsyncTask<Void, Void, Address>() {
            @Override
            protected Address doInBackground(Void... voids) {
                try {
                    if (account.getGuid() == null || account.getGuid().length() <= 0) {
                        throw new Exception("ERROR: Not a valid GUID!");
                    }
                    String label_local = label;
                    if (label == null || label.length() == 0) {
                        label_local = UUID.randomUUID().toString();
                    }
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet("http://blockchain.info/" + "/merchant/" + account.getGuid()
                            + "/new_address?api_code=LK75FDss&password=" + account.getPassword() + "&label="
                            + URLEncoder.encode(label_local, "UTF-8"));
                    String r = EntityUtils.toString(client.execute(get).getEntity());
                    JsonObject response = (JsonObject) new JsonParser().parse(r);
                    
                    Address address = new Address();
                    address.setAddress(response.get("address").getAsString());
                    address.setBalance(0);
                    address.setLabel(response.get("label").getAsString());
                    address.setTotalReceived(0);
                    
                    return address;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.execute().get();
        
    }
    
    /**
     * Will archive the given address that's associated with the account.
     * 
     * IMPORTANT: the archived address must be SAVED in the local DB, because there's no API call that let's us find
     * list of archived addresses.
     * 
     * @param account
     * @param address
     * @throws Exception if account is not valid or if the archiving fails
     */
    public void archiveAddress(final Account account, final Address address) throws Exception {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (account.getGuid() == null || account.getGuid().length() <= 0) {
                        throw new Exception("ERROR: Not a valid GUID!");
                    }
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet("http://blockchain.info/" + "/merchant/" + account.getGuid()
                            + "/archive_address?api_code=LK75FDss&password=" + account.getPassword() + "&address="
                            + address);
                    String r = EntityUtils.toString(client.execute(get).getEntity());
                    JsonObject response = (JsonObject) new JsonParser().parse(r);
                    if (!response.get("archived").getAsString().equals(address.getAddress())) {
                        throw new Exception("ERROR: couldn't archive!");
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
    
    /**
     * Will unarchive the given address that's associated with the account.
     * 
     * IMPORTANT: the archived address must be REMOVED in the local DB.
     * 
     * @param account
     * @param address
     * @throws Exception if unarchiving fails
     */
    public void unarchiveAddress(final Account account, final Address address) throws Exception {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    if (account.getGuid() == null || account.getGuid().length() <= 0) {
                        throw new Exception("ERROR: Not a valid GUID!");
                    }
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet("http://blockchain.info/" + "/merchant/" + account.getGuid()
                            + "/unarchive_address?api_code=LK75FDss&password=" + account.getPassword() + "&address="
                            + address);
                    String r = EntityUtils.toString(client.execute(get).getEntity());
                    JsonObject response = (JsonObject) new JsonParser().parse(r);
                    if (!response.get("active").getAsString().equals(address.getAddress())) {
                        throw new Exception("ERROR: couldn't unarchive!");
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
    
    /**
     * Will send a payment to the given address.
     * 
     * @param account the user account from which the money is sent
     * @param address to which the money is being sent
     * @param amountInSatoshis the amount of money to send in satoshis (1BTC=100000000Satoshis)
     * @throws Exception if payment fails
     */
    public Payment sendPayment(final Account account, final String address, final int amountInSatoshis)
            throws Exception {
    	if(isTesting){
    		User user = User.getLoggedInUser();
    		Transaction tx = new Transaction("sent", user.getGUID(), address, amountInSatoshis);
    		Transaction.insertTransaction(tx);
    		return new Payment();
    	}else{
	        return new AsyncTask<Void, Void, Payment>() {
	            @Override
	            protected Payment doInBackground(Void... voids) {
	                try {
	                    if (account.getGuid() == null || account.getGuid().length() <= 0) {
	                        throw new Exception("ERROR: Not a valid GUID!");
	                    }
	                    HttpClient client = new DefaultHttpClient();
	                    HttpGet get = new HttpGet("http://blockchain.info/" + "/merchant/" + account.getGuid()
	                            + "/payment?api_code=LK75FDss" + "&password=" + account.getPassword() + "&to=" + address
	                            + "&amount=" + amountInSatoshis);
	                    String r = EntityUtils.toString(client.execute(get).getEntity());
	                    JsonObject response = (JsonObject) new JsonParser().parse(r);
	                    if (response.get("error") != null) {
	                        throw new Exception("ERROR: couldn't send payment, " + response.get("error").getAsString()
	                                + "!");
	                    }
	                    Payment payment = new Payment();
	                    payment.setMessage(response.get("message").getAsString());
	                    payment.setTxHash(response.get("tx_hash").getAsString());
	                    payment.setNotice(response.get("notice").getAsString());
	                    return payment;
	                }
	                catch (Exception e) {
	                    e.printStackTrace();
	                }
	                return null;
	            }
	        }.execute().get();
    	}
    }
    
    /**
     * @throws ExecutionException 
     * @throws Exception 
     */
    public List<Conversion.Currency> getFiatRates() throws Exception {
        return new AsyncTask<Void, Void, List<Conversion.Currency>>() {
            @Override
            protected List<Conversion.Currency> doInBackground(Void... voids) {
            	List<Conversion.Currency> rates = new ArrayList<Conversion.Currency>();
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet("http://blockchain.info/ticker");
                    String r = EntityUtils.toString(client.execute(get).getEntity());
                    JsonObject response = (JsonObject) new JsonParser().parse(r);
                    for(Entry<String, JsonElement> currency : response.entrySet()){
                    	String name = currency.getKey();
                    	double rate = ((JsonObject) currency.getValue()).get("15m").getAsDouble();
                    	String symbol = ((JsonObject) currency.getValue()).get("symbol").getAsString();
			double last = ((JsonObject) currency.getValue()).get("last").getAsDouble();
			double buy = ((JsonObject) currency.getValue()).get("buy").getAsDouble();
			double sell = ((JsonObject) currency.getValue()).get("sell").getAsDouble();
			double day = ((JsonObject) currency.getValue()).get("24h").getAsDouble();
                    	rates.add(new Conversion.Currency(name, rate, symbol, last, buy, sell, day));
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return rates;
            }
        }.execute().get();
    }
}
