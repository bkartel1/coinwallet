package com.teambitcoin.coinwallet;

import java.util.ArrayList;
import java.util.List;

import com.teambitcoin.coinwallet.api.Address;
import com.teambitcoin.coinwallet.models.AddressDatabaseHandler;

public class AddressContainer {	
	
	private String username;
	private List<Address> activeAddressList;
	private List<Address> archivedAddressList;
	
	public AddressContainer(String username){
		this.username = username;
		
		this.activeAddressList = new ArrayList<Address>();
		this.archivedAddressList = new ArrayList<Address>();
		
		this.PopulateActiveAddressList();
		this.PopulateArchivedAddressList();
	}

	public void CreateAddress(Address address){
		activeAddressList.add(address);
		ActiveAddressToDatabase(address);
	}
	
	public void ArchiveAddress(Address address){
		activeAddressList.remove(address);
		archivedAddressList.add(address);
	}
	
	/**
	 * Database interactions 
	 * 
	 */
	public void PopulateActiveAddressList(){
		activeAddressList = AddressDatabaseHandler.RetrieveAddresses(username,false);
	}

	public void PopulateArchivedAddressList(){
		archivedAddressList = AddressDatabaseHandler.RetrieveAddresses(username,true);
	}
	
	public void ActiveAddressToDatabase(Address address){
		int newAddressId = activeAddressList.size() + archivedAddressList.size(); 
		AddressDatabaseHandler.WriteNewAddress(newAddressId,username,address,false);
	}
	
	public void ArchiveAddressToDatabase(Address address){
		int newAddressId = activeAddressList.size() + archivedAddressList.size(); 
		AddressDatabaseHandler.WriteNewAddress(newAddressId,username,address,true);
	}
	
	
	public List<Address> getActiveAddressList() {
		return activeAddressList;
	}


	public List<Address> getArchivedAddressList() {
		return archivedAddressList;
	}

	
}
