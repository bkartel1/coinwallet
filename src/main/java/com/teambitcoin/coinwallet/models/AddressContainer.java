package main.java.com.teambitcoin.coinwallet.models;

import java.util.ArrayList;
import java.util.List;

import main.java.com.teambitcoin.coinwallet.api.Address;

public class AddressContainer {	
	
	private String username;
	private ArrayList<Address> activeAddressList;
	private ArrayList<Address> archivedAddressList;
	
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
		SwitchArchiveModeInDatabase(address,true);
	}
	
	public void UnArchiveAddress(Address address){
		archivedAddressList.remove(address);
		activeAddressList.add(address);
		SwitchArchiveModeInDatabase(address,false);
	}
	
	public ArrayList<Address> getActiveAddressList() {
		return activeAddressList;
	}


	public ArrayList<Address> getArchivedAddressList() {
		return archivedAddressList;
	}
	
	/**
	 * Database interactions 
	 * 
	 */
	private void PopulateActiveAddressList(){
		activeAddressList = AddressDatabaseHandler.RetrieveAddresses(username,false);
	}

	private void PopulateArchivedAddressList(){
		archivedAddressList = AddressDatabaseHandler.RetrieveAddresses(username,true);
	}
	
	private void ActiveAddressToDatabase(Address address){
		int newAddressId = activeAddressList.size() + archivedAddressList.size(); 
		AddressDatabaseHandler.WriteNewAddress(newAddressId,username,address,false);
	}
	
	private void SwitchArchiveModeInDatabase(Address address,boolean isArchived){
		AddressDatabaseHandler.ChangeArchiveMode(address.getAddress(), isArchived);
	}
}
