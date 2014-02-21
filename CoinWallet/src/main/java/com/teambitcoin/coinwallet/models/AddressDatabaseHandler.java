package com.teambitcoin.coinwallet.models;

import java.util.ArrayList;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.teambitcoin.coinwallet.api.Address;

public class AddressDatabaseHandler {
    private static final String DEBUG_TAG = "[DatabaseHandler]";
    private static final String TABLE_NAME = "user_valid_add";
    private static final String GUID_COLUMN_NAME = "guid";
    private static final String USER_GUID_COLUMN_NAME = "user_guid";
    private static final String USER_ADDRESS_COLUMN_NAME = "address";
    private static final String ADDRESS_ARCHIVED_MODE = "is_archived";
    private static final String ADDRESS_BALANCE_NAME = "balance";
    private static final String ADDRESS_LABEL_NAME = "label";
    private static final String ADDRESS_TOTAL_RECEIVED_NAME = "total_received";
    
    public static String getAddresseSQLInitQuery() {
        return "CREATE TABLE " + TABLE_NAME + " (" + GUID_COLUMN_NAME + " TEXT PRIMARY KEY," + USER_GUID_COLUMN_NAME
                + " TEXT NOT NULL," + ADDRESS_ARCHIVED_MODE + " INTEGER NOT NULL," + USER_ADDRESS_COLUMN_NAME
                + " TEXT NOT NULL," + ADDRESS_LABEL_NAME + " TEXT UNIQUE NOT NULL," + ADDRESS_BALANCE_NAME
                + " INTEGER NOT NULL," + ADDRESS_TOTAL_RECEIVED_NAME + " INTEGER NOT NULL);";
    }
    
    public static ArrayList<Address> retrieveAddresses(String userGuid, boolean isArchived) {
        String archiveModeValue;
        if (isArchived)
            archiveModeValue = " 1 ";
        else
            archiveModeValue = " 0 ";
        
        ArrayList<Address> addressList = new ArrayList<Address>();
        
        String[] columns = { USER_ADDRESS_COLUMN_NAME, ADDRESS_LABEL_NAME, ADDRESS_TOTAL_RECEIVED_NAME,
                ADDRESS_BALANCE_NAME };
        
        Cursor cursor = Database.query(TABLE_NAME, columns, USER_GUID_COLUMN_NAME + " = ? AND " + ADDRESS_ARCHIVED_MODE
                + " = ?", new String[] { userGuid, archiveModeValue }, null, null, null);
        
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            
            int count = cursor.getCount();
            
            int addressIndex = cursor.getColumnIndex(USER_ADDRESS_COLUMN_NAME);
            int labelIndex = cursor.getColumnIndex(ADDRESS_LABEL_NAME);
            int totalIndex = cursor.getColumnIndex(ADDRESS_TOTAL_RECEIVED_NAME);
            int balanceIndex = cursor.getColumnIndex(ADDRESS_BALANCE_NAME);
            
            for (int i = 0; i < count; i++) {
                addressList.add(new Address(cursor.getString(addressIndex), cursor.getString(labelIndex), cursor
                        .getInt(balanceIndex), cursor.getInt(totalIndex)));
                cursor.moveToNext();
            }
        }
        
        Log.i(DEBUG_TAG, "query returned: " + addressList.size() + " results.");
        
        return addressList;
    }
    
    public static void changeArchiveMode(String address, boolean isArchived) {
        String archiveModeValue;
        if (isArchived)
            archiveModeValue = " 1 ";
        else
            archiveModeValue = " 0 ";
        
        ContentValues values = new ContentValues();
        values.put(ADDRESS_ARCHIVED_MODE, archiveModeValue);
        
        if (Database.update(TABLE_NAME, values, USER_ADDRESS_COLUMN_NAME + " = ?", new String[] { address }) != 1) {
            Log.i(DEBUG_TAG, "Row NOT successfully updated");
        }
    }
    
    public static void writeNewAddress(int id, String userGuid, Address address, boolean isArchived) {
        String archiveModeValue;
        if (isArchived)
            archiveModeValue = " 1 ";
        else
            archiveModeValue = " 0 ";
        
        ContentValues values = new ContentValues();
        values.put(USER_GUID_COLUMN_NAME, userGuid);
        values.put(GUID_COLUMN_NAME, Integer.toString(id));
        values.put(USER_ADDRESS_COLUMN_NAME, address.getAddress());
        values.put(ADDRESS_TOTAL_RECEIVED_NAME, address.getTotalReceived());
        values.put(ADDRESS_LABEL_NAME, address.getLabel());
        values.put(ADDRESS_ARCHIVED_MODE, archiveModeValue);
        values.put(ADDRESS_BALANCE_NAME, address.getBalance());
        
        if (Database.insert(TABLE_NAME, values) == 1) {
            Log.i(DEBUG_TAG, "Row inserted successfully");
        }
        else {
            Log.i(DEBUG_TAG, "Row NOT inserted successfully");
        }
    }
}
