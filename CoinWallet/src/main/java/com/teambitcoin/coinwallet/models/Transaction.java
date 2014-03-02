package com.teambitcoin.coinwallet.models;

import android.content.ContentValues;

public class Transaction {
	private static final String TABLE_NAME = "transactions";
    private static final String TX_ID = "tx_id";
    private static final String TYPE = "tx_type";
    private static final String USER_GUID = "user_guid";
    private static final String ADDRESS = "address";
    private static final String AMOUNT = "amount";
    
    private String type;
    private String userGuid;
    private String address;
    private int amount;
        
    public Transaction(String type, String user_guid, String address, int amount) {
		super();
		this.type = type;
		this.userGuid = user_guid;
		this.address = address;
		this.amount = amount;
	}
    
    public static void insertTransaction(Transaction transaction){
    	ContentValues values = new ContentValues();
        values.put(TYPE, transaction.getType());
        values.put(USER_GUID, transaction.getUserGuid());
        values.put(ADDRESS, transaction.getAddress());
        values.put(AMOUNT, transaction.getAmount());
        Database.insert(TABLE_NAME, values);
    }

	protected static String getSQLInitQuery() {
        return "CREATE TABLE " + TABLE_NAME + " (" + 
        			TX_ID + " INTEGER NOT NULL PRIMARY KEY," + 
        			USER_GUID + " TEXT NOT NULL," + 
        			TYPE + " TEXT," + 
        			ADDRESS + " TEXT," + 
        			AMOUNT + " INTEGER NOT NULL DEFAULT 0," +
        			"FOREIGN KEY (user_guid) REFERENCES users(guid));";
    }

	public String getType() {
		return type;
	}

	public String getUserGuid() {
		return userGuid;
	}

	public String getAddress() {
		return address;
	}

	public int getAmount() {
		return amount;
	}
}
