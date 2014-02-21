package com.teambitcoin.coinwallet.models;

import java.util.ArrayList;
import com.teambitcoin.coinwallet.api.Address;

public class AddressContainer {
    
    private String username;
    private ArrayList<Address> activeAddressList;
    private ArrayList<Address> archivedAddressList;
    
    public AddressContainer(String username) {
        this.username = username;
        
        this.activeAddressList = new ArrayList<Address>();
        this.archivedAddressList = new ArrayList<Address>();
        
        this.populateActiveAddressList();
        this.populateArchivedAddressList();
    }
    
    public void createAddress(Address address) {
        activeAddressList.add(address);
        activeAddressToDatabase(address);
    }
    
    public void archiveAddress(Address address) {
        activeAddressList.remove(address);
        archivedAddressList.add(address);
        switchArchiveModeInDatabase(address, true);
    }
    
    public void unArchiveAddress(Address address) {
        archivedAddressList.remove(address);
        activeAddressList.add(address);
        switchArchiveModeInDatabase(address, false);
    }
    
    public ArrayList<Address> getActiveAddressList() {
        return activeAddressList;
    }
    
    public ArrayList<Address> getArchivedAddressList() {
        return archivedAddressList;
    }
    
    /* Database interactions */
    
    private void populateActiveAddressList() {
        activeAddressList = AddressDatabaseHandler.retrieveAddresses(username, false);
    }
    
    private void populateArchivedAddressList() {
        archivedAddressList = AddressDatabaseHandler.retrieveAddresses(username, true);
    }
    
    private void activeAddressToDatabase(Address address) {
        int newAddressId = activeAddressList.size() + archivedAddressList.size();
        AddressDatabaseHandler.writeNewAddress(newAddressId, username, address, false);
    }
    
    private void switchArchiveModeInDatabase(Address address, boolean isArchived) {
        AddressDatabaseHandler.changeArchiveMode(address.getAddress(), isArchived);
    }
}
