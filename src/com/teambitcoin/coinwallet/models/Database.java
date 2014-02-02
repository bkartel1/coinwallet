package com.teambitcoin.coinwallet.models;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	private static final int DB_VERSION = 3;
	private static final String DB_NAME = "coinwallet.db";
	private static final Database INSTANCE = new Database(null);
	 
	private Database(Context context){
		super(context, DB_NAME, null , DB_VERSION );
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(User.getSQLInitQuery());

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	/**
	 * Currently unthread-safe singleton implementationation for accessing the db.
	 * @return Instance of the Database.
	 */
	protected static Database getInstance(){
		return INSTANCE;
	}
}
