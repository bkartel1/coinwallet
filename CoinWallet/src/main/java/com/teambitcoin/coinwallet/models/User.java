package com.teambitcoin.coinwallet.models;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.util.Base64;
import android.database.Cursor;
import android.content.ContentValues;

import com.teambitcoin.coinwallet.api.Account;
import com.teambitcoin.coinwallet.api.BlockchainAPI;

/**
 * 
 * @author Michael Williams
 * 
 */
public class User {
    private static final String TABLE_NAME = "users";
    private static final String USERNAME_COLUMN_NAME = "username";
    private static final String GUID_COLUMN_NAME = "guid";
    private static final String SECURITY_QUESTION_COLUMN_NAME = "question";
    private static final String SECURITY_ANSWER_COLUMN_NAME = "answer";
    private static final String PASSWORD_COLUMN_NAME = "password";
    private static final String ACCOUNT_BALANCE = "account_balance";
    private static final Pattern VALID_USERNAME = 
        Pattern.compile("^((\\d{10})|((\\+\\d{1,2}-)?\\d{3}-\\d{3}-\\d{4})|([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,7}))$");
    
    private static DESKeySpec keySpec;
    private static SecretKeyFactory keyFactory;
    private static SecretKey key;
    
    private static User LOGGED_IN = null;
    
    private String username;
    private String guid;
    private String password;
    private String currency;
    private int accountBalance;
    
    private User(String username, String guid, int accountBalance) {
        this.username = username;
        this.guid = guid;
        this.currency = "CAD";
        this.accountBalance = accountBalance;
    }    

    public int getAccountBalance(){
    	return accountBalance;
    }
    
    public void setAccountBalance(int balance) {
    	this.accountBalance = balance;
        if (this.equals(LOGGED_IN)) {
            ContentValues values = new ContentValues();
            values.put(ACCOUNT_BALANCE, balance);
            Database.update(TABLE_NAME, values, GUID_COLUMN_NAME + "= ?", new String[] { guid });
        }
    }
    
    public String getCurrency(){
        return currency;
    }

    public void setCurrency(String currency){
        this.currency = currency;
    }
    /**
     * Backwards compatability wrapper for create, which simply fills the new
     * question and answer fields with null. Logs in new user.
     * 
     * @param username
     * @param password
     * @return New User
     * @throws Exception
     */
    public static User create(String username, String password) throws Exception {
        return create(username, password, null, null);
    }
    
    /**
     * Sets the security question and answer on the user, providing it is
     * currently logged in.
     * 
     * @param question
     * @param answer
     */
    public void setQnA(String question, String answer) {
        if (this.equals(LOGGED_IN)) {
            ContentValues values = new ContentValues();
            values.put(SECURITY_QUESTION_COLUMN_NAME, question);
            values.put(SECURITY_ANSWER_COLUMN_NAME, answer);
            Database.update(TABLE_NAME, values, GUID_COLUMN_NAME + "= ?", new String[] { guid });
        }
    }
    
    /**
     * Production wrapper for the create method, sets dummy status to false, as
     * we actually want to create new accounts on blockchain.info in the app,
     * but not in our unit tests.
     * 
     * @param username
     * @param password
     * @param question
     * @param answer
     * @return Created User
     * @throws Exception
     */
    public static User create(String username, String password, String question, String answer) throws Exception {
        return create(username, password, question, answer, false);
    }
    
    private static void initCrypt() {
        try {
            if (keySpec == null) {
                keySpec = new DESKeySpec("YourSecr".getBytes("UTF8"));
            }
            if (keyFactory == null) {
                keyFactory = SecretKeyFactory.getInstance("DES");
            }
            if (key == null) {
                key = keyFactory.generateSecret(keySpec);
            }
        }
        catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param username
     *            Must be either a phone numnber (of form xxxxxxxxxx,
     *            xxx-xxx-xxx or +xx-xxx-xxx-xxxx) or and email address
     * @param password
     *            Must be at least 10 characters long
     * @param question
     *            Secret Question used to recover forgotten password
     * @param answer
     *            The Answer to the Secret question.
     * @param dummy
     *            if true, doesn't use the API to create a new user on
     *            Blockchain.info.
     * @return Newly created user.
     * @throws Exception
     */
    public static User create(String username, String password, String question, String answer, boolean dummy)
            throws Exception {
        Cursor cursor = Database.query(TABLE_NAME, new String[] { GUID_COLUMN_NAME }, USERNAME_COLUMN_NAME + "= ?",
                new String[] { username }, null, null, null);
        if (!cursor.isAfterLast() || !isValidUsername(username)) {
            return null;
        }
        Account acc;
        User user = new User(username, null, 0);
        if (!dummy) {
            acc = new BlockchainAPI().createAccount(username, password);
        }
        else {
            acc = new Account(username, password, username);
        }
        ContentValues values = new ContentValues();
        values.put(USERNAME_COLUMN_NAME, username);
        values.put(GUID_COLUMN_NAME, acc.getGuid());
        values.put(PASSWORD_COLUMN_NAME, encodePassword(password));
        values.put(SECURITY_QUESTION_COLUMN_NAME, question);
        values.put(SECURITY_ANSWER_COLUMN_NAME, answer);
        values.put(ACCOUNT_BALANCE, 0);
        Database.insert(TABLE_NAME, values);
        user.guid = acc.getGuid();
        user.password = password;
        LOGGED_IN = user;
        return user;
    }
    
    /**
     * username getter
     * 
     * @return user's username.
     */
    public String getUsername() {
        return username;
    }
    
    private String getDecodedPassword() {
        Cursor cursor = Database.query(TABLE_NAME, new String[] { PASSWORD_COLUMN_NAME }, GUID_COLUMN_NAME + "= ?",
                new String[] { guid }, null, null, null);
        cursor.moveToNext();
        String password = cursor.getString(cursor.getColumnIndex(PASSWORD_COLUMN_NAME));
        return decryptPassword(password);
    }
    
    private static String decryptPassword(String password) {
        initCrypt();
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            password = new String(cipher.doFinal(android.util.Base64.decode(password, Base64.URL_SAFE)));
        }
        catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return password;
    }
    
    private static String encodePassword(String password) {
        initCrypt();
        try {
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return android.util.Base64.encodeToString(cipher.doFinal(password.getBytes("UTF8")), Base64.URL_SAFE);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        catch (BadPaddingException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Method for recovering a user's password in case they forget it.
     * 
     * @param answer
     *            Answer to the user's secret Question.
     * @return user's password
     */
    public String recoverPassword(String answer) {
        Cursor cursor = Database.query(TABLE_NAME, new String[] { SECURITY_ANSWER_COLUMN_NAME }, GUID_COLUMN_NAME
                + "= ?", new String[] { guid }, null, null, null);
        cursor.moveToNext();
        String correct = cursor.getString(cursor.getColumnIndex(SECURITY_ANSWER_COLUMN_NAME));
        if (correct == null) {
            return null;
        }
        else if (answer != correct) {
            return null;
        }
        else {
            return getDecodedPassword();
        }
    }
    
    /**
     * guid getter.
     * 
     * @return user's GUID.
     */
    public String getGUID() {
        return guid;
    }
    
    /**
     * Factory for retrieving a user from the database.
     * 
     * @param username
     * @return user with given username.
     */
    public static User getUser(String username) {
        Cursor cursor = Database.query(TABLE_NAME, new String[] { GUID_COLUMN_NAME, ACCOUNT_BALANCE }, USERNAME_COLUMN_NAME + "= ?",
                new String[] { username }, null, null, null);
        if (cursor.isAfterLast()) {
            return null;
        }
        else {
            cursor.moveToNext();
            return new User(username, 
            				cursor.getString(cursor.getColumnIndex(GUID_COLUMN_NAME)),
            				cursor.getInt(cursor.getColumnIndex(ACCOUNT_BALANCE)));
        }
    }
    
    /**
     * Method for retreaving a user's secret question, in case of forgetting
     * their password.
     * 
     * @return user's secret question.
     */
    public String getQuestion() {
        Cursor cursor = Database.query(TABLE_NAME, new String[] { SECURITY_QUESTION_COLUMN_NAME }, GUID_COLUMN_NAME
                + "= ?", new String[] { guid }, null, null, null);
        cursor.moveToNext();
        return cursor.getString(cursor.getColumnIndex(SECURITY_QUESTION_COLUMN_NAME));
    }
    
    /**
     * Checks if a password is correct for the user. Used when one needs
     * password authorisation for something.
     * 
     * @param password
     * @return
     */
    public boolean authenticate(String password) {
        String realPassword = this.getDecodedPassword();
        if (password.equals(realPassword)) {
            this.password = password;
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Factory for producing a logged-in user.
     * 
     * @param username
     * @param password
     * @return Newly logged-in user, or null is the case that the credentials were not valid.
     */
    public static User login(String username, String password) {
        User user = getUser(username);
        if (user == null) {
            return null;
        }
        if (user.authenticate(password)) {
            LOGGED_IN = user;
            return user;
        }
        else {
            return null;
        }
    }
    
    /**
     * Logs the current user out.
     */
    public static void logout() {
        LOGGED_IN = null;
    }
    
    /**
     * Method for retrieving an Account object for use with the Blockchain API.
     * 
     * @return {@link Account} object.
     */
    protected Account generateAccount() {
        return new Account(username, password, guid);
    }
    
    /**
     * Database creation query generation function.
     * 
     * @return SQL query for database initialisation.
     */
    protected static String getSQLInitQuery() {
        return "CREATE TABLE " + TABLE_NAME + " (" + 
        			GUID_COLUMN_NAME + " TEXT PRIMARY KEY," + 
        			USERNAME_COLUMN_NAME + " TEXT UNIQUE NOT NULL," + 
        			SECURITY_QUESTION_COLUMN_NAME + " TEXT," + 
        			SECURITY_ANSWER_COLUMN_NAME + " TEXT," + 
        			PASSWORD_COLUMN_NAME + " TEXT NOT NULL," + 
        			ACCOUNT_BALANCE + " INTEGER NOT NULL DEFAULT 0);";
    }
    
    /**
     * Function for checking if a given username is valid through the power of
     * RegEx.
     * 
     * @param username
     * @return whether the given username is valid or not.
     */
    public static boolean isValidUsername(String username) {
        return VALID_USERNAME.matcher(username).matches();
    }
    
    /**
     * Method of obtaining user data on the current user.
     * 
     * @return Currently logged-in user.
     */
    public static User getLoggedInUser() {
        return LOGGED_IN;
    }
    
}
