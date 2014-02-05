package com.teambitcoin.coinwallet.models;

import java.util.regex.Pattern;

public class User {
	private static final String TABLE_NAME = "users";
	private static final String USERNAME_COLUMN_NAME = "username";
	private static final String GUID_COLUMN_NAME = "guid";
	private static final String SECURITY_QUESTION_COLUMN_NAME = "question";
	private static final String SECURITY_ANSWER_COLUMN_NAME = "answer";
	private static final String PASSWORD_COLUMN_NAME = "password";
	private static final Pattern VALID_USERNAME = Pattern.compile("^((\\d{10})|((\\+\\d)?\\d{3}-\\d{3}-\\d{4})|([A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}))$");

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
}
