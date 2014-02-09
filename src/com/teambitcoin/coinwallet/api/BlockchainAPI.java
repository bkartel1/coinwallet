package com.teambitcoin.coinwallet.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * This class is used to connect with Blockchain's API.
 * 
 * Example usage:
 * Account newAccount = new BlockchainAPI().createAccount(<username>, <password>);
 * 
 * I decided to keep it as a normal class instead of a class with static methods,
 * because it might be easier to adapt it later if we need to do pooling or keep
 * track of states, etc.
 * 
 * @author FT
 *
 */
public class BlockchainAPI {	
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
			protected Account doInBackground(Void... voids){
				try{
					if(password.length() < 10){
						throw new Exception("ERROR: Password's length must be at least 10!");
					}
					HttpClient httpclient = new DefaultHttpClient();
					HttpGet httpget = new HttpGet("http://blockchain.info/api/v2/create_wallet?api_code=LK75FDss&password="+password);
					String r = EntityUtils.toString(httpclient.execute(httpget).getEntity());
					Log.v("BLOCKCHAIN_API", "Created client");
					JsonObject response = (JsonObject) new JsonParser().parse(r);
					Account account = new Account();
					account.setUsername(username);
					account.setPassword(password);
					account.setGuid(response.get("guid").getAsString());
					Log.v("BLOCKCHAIN_API", account.toString());
					return account;
				}catch(Exception e){
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
	public List<Address> getAddresses(final Account account) throws Exception{
		return new AsyncTask<Void, Void, List<Address>>() {
			@Override
			protected List<Address> doInBackground(Void... voids){
				try{
					if(account.getGuid() == null || account.getGuid().length() <= 0){
						throw new Exception("ERROR: Not a valid GUID!");
					}
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet("http://blockchain.info/"+
												"/merchant/"+account.getGuid()+
												"/list?api_code=LK75FDss&password="+account.getPassword());
					
					JsonObject response = (JsonObject) new JsonParser().parse(client.execute(get).toString());
					
					JsonArray addrs = response.get("addresses").getAsJsonArray();
					List<Address> addresses = new ArrayList<Address>();
					for(int i = 0; i < addrs.size(); i++){
						Address addr = new Address();
						JsonObject addrObj = (JsonObject) addrs.get(i);
						addr.setAddress(addrObj.get("address").getAsString());
						addr.setBalance(addrObj.get("balance").getAsInt());
						addr.setLabel(addrObj.get("label").getAsString());
						addr.setTotalReceived(addrObj.get("total_received").getAsInt());
						
						addresses.add(addr);
					}
					return addresses;
				}catch(Exception e){
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
	 * @param label the label associated with the newly generated address,
	 * 				if null, the label will be a random UUID
	 * @return the new Address that was added
	 * @throws Exception if the account is not valid
	 */
	public Address generateNewAddress(final Account account, final String label) throws Exception {
		return new AsyncTask<Void, Void, Address>() {
			@Override
			protected Address doInBackground(Void... voids){
				try{
					if(account.getGuid() == null || account.getGuid().length() <= 0){
						throw new Exception("ERROR: Not a valid GUID!");
					}
					String label_local = label;
					if(label == null || label.length() == 0){
						label_local = UUID.randomUUID().toString();
					}
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet("http://blockchain.info/"+
												"/merchant/"+account.getGuid()+
												"/new_address?api_code=LK75FDss&password="+account.getPassword()+
												"&label="+label_local);
					
					JsonObject response = (JsonObject) new JsonParser().parse(client.execute(get).toString());
					
					Address address = new Address();
					address.setAddress(response.get("address").getAsString());
					address.setBalance(0);
					address.setLabel(response.get("label").getAsString());
					address.setTotalReceived(0);
					
					return address;
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
		}.execute().get();
		
	}
	
	/**
	 * Will archive the given address that's associated with the account.
	 * 
	 * IMPORTANT: the archived address must be SAVED in the local DB, because
	 * there's no API call that let's us find list of archived addresses.
	 * 
	 * @param account
	 * @param address
	 * @throws Exception if account is not valid or if the archiving fails
	 */
	public void archiveAddress(final Account account, final Address address) throws Exception {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids){
				try{
					if(account.getGuid() == null || account.getGuid().length() <= 0){
						throw new Exception("ERROR: Not a valid GUID!");
					}
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet("http://blockchain.info/"+
												"/merchant/"+account.getGuid()+
												"/archive_address?api_code=LK75FDss&password="+account.getPassword()+
												"&address="+address);
					JsonObject response = (JsonObject) new JsonParser().parse(client.execute(get).toString());
					if(!response.get("archived").getAsString().equals(address.getAddress())){
						throw new Exception("ERROR: couldn't archive!");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				return null;
			}
		};		
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
	public void unarchiveAddress(final Account account, final Address address) throws Exception{
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... voids){
				try{
					if(account.getGuid() == null || account.getGuid().length() <= 0){
						throw new Exception("ERROR: Not a valid GUID!");
					}
					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet("http://blockchain.info/"+
												"/merchant/"+account.getGuid()+
												"/unarchive_address?api_code=LK75FDss&password="+account.getPassword()+
												"&address="+address);
					JsonObject response = (JsonObject) new JsonParser().parse(client.execute(get).toString());
					if(!response.get("active").getAsString().equals(address.getAddress())){
						throw new Exception("ERROR: couldn't unarchive!");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				return null;
			}
		};		
	}
}
