package com.teambitcoin.coinwallet.models;

import java.util.regex.Pattern;

import android.database.Cursor;
import android.content.ContentValues;

import com.teambitcoin.coinwallet.api.Account;
import com.teambitcoin.coinwallet.api.BlockchainAPI;

public class User {
	private static final String TABLE_NAME = "users";
	private static final String USERNAME_COLUMN_NAME = "username";
	private static final String GUID_COLUMN_NAME = "guid";
	private static final String SECURITY_QUESTION_COLUMN_NAME = "question";
	private static final String SECURITY_ANSWER_COLUMN_NAME = "answer";
	private static final String PASSWORD_COLUMN_NAME = "password";
	private static final Pattern VALID_USERNAME = Pattern.compile("^((\\d{10})|((\\+\\d)?\\d{3}-\\d{3}-\\d{4})|([A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}))$");
	
	private static User LOGGED_IN = null;
	
	private String username;
	private String guid;
	private String password;
	
	protected User(String username, String guid){
		this.username = username;
		this.guid = guid;
	}

	public static User create(String username, String password) throws Exception{
		Cursor cursor = Database.query(TABLE_NAME, new String[]{GUID_COLUMN_NAME} , USERNAME_COLUMN_NAME + "= ?", new String[]{username}, null, null, null);
		if (!cursor.isAfterLast()||!isValidUsername(username)){
			return null;
		}
		Account acc = new BlockchainAPI().createAccount(username, password);
		ContentValues values = new ContentValues();
		values.put(USERNAME_COLUMN_NAME, username);
		values.put(GUID_COLUMN_NAME, acc.getGuid());
		values.put(PASSWORD_COLUMN_NAME, password);
		Database.insert(TABLE_NAME, values);
		User user = new User(username, acc.getGuid()); 
		LOGGED_IN = user;
		return user;
	}
	
	public String getUsername(){
		return username;
	}
	
	private String getDecodedPassword(){
		Cursor cursor = Database.query(TABLE_NAME, new String[]{PASSWORD_COLUMN_NAME} , GUID_COLUMN_NAME + "= ?", new String[]{guid}, null, null, null);
		return cursor.getString(cursor.getColumnIndex(PASSWORD_COLUMN_NAME));
	}
	
	public String recoverPassword(String answer){
		Cursor cursor = Database.query(TABLE_NAME, new String[]{SECURITY_ANSWER_COLUMN_NAME} , GUID_COLUMN_NAME + "= ?", new String[]{guid}, null, null, null);
		String correct = cursor.getString(cursor.getColumnIndex(SECURITY_ANSWER_COLUMN_NAME));
		if (correct == null){
			return null;
		} else if (answer != correct) {
			return null;
		} else {
			return getDecodedPassword();
		}
	}
	
	public String getGUID(){
		return guid;
	}
	
	public static User getUser(String username){
		Cursor cursor = Database.query(TABLE_NAME, new String[]{GUID_COLUMN_NAME} , USERNAME_COLUMN_NAME + "= ?", new String[]{username}, null, null, null);
		if (cursor.isAfterLast()){
			return null;
		} else { 
			return new User(username, cursor.getString(cursor.getColumnIndex(GUID_COLUMN_NAME)));
		}
	}
	
	public String getQuestion(){
		Cursor cursor = Database.query(TABLE_NAME, new String[]{SECURITY_QUESTION_COLUMN_NAME} , GUID_COLUMN_NAME + "= ?", new String[]{guid}, null, null, null);
		return cursor.getString(cursor.getColumnIndex(SECURITY_QUESTION_COLUMN_NAME));
	}
	
	private boolean authenticate(String password){
		if (password==this.getDecodedPassword()){
			this.password=password;
			return true;
		} else {
			return false;
		}
	}
	
	public static User login(String username, String password){
		User user = getUser(username);
		if (user == null){
			return null;
		}
		if (user.authenticate(password)){
			LOGGED_IN = user;
			return user;
		} else {
			return null;
		}
	}
	
	public static void logout(){
		LOGGED_IN = null;
	}
	
	protected Account generateAccount(){
		return new Account(username, password, guid);
	}
	
	protected static String getSQLInitQuery(){
		return "CREATE TABLE " + TABLE_NAME + " (" +
				GUID_COLUMN_NAME + " TEXT PRIMARY KEY," +
				USERNAME_COLUMN_NAME + " TEXT UNIQUE NOT NULL," +
				SECURITY_QUESTION_COLUMN_NAME + " TEXT NOT NULL," +
				SECURITY_ANSWER_COLUMN_NAME + " TEXT NOT NULL," +
				PASSWORD_COLUMN_NAME + " TEXT NOT NULL);" ;
	}
	
	public static boolean isValidUsername(String username){
		return VALID_USERNAME.matcher(username).matches();
	}
	
	public static User getLoggedInUser(){
		return LOGGED_IN;
	}
	
}
