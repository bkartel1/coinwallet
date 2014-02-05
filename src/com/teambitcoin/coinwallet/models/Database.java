package com.teambitcoin.coinwallet.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.locks.ReentrantLock;

public class Database extends SQLiteOpenHelper {

	private static final int DB_VERSION = 3;
	private static final String DB_NAME = "coinwallet.db";
	private static final Database INSTANCE = new Database(null);
	 
	private static final ReentrantLock lock = new ReentrantLock();
	
	private Database(Context context){
		super(context, DB_NAME, null , DB_VERSION );
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(User.getSQLInitQuery());

	}
	
	protected static void execWritableSQL(String sql){
		lock.lock();
		try{
			SQLiteDatabase db = INSTANCE.getWritableDatabase();
			db.execSQL(sql);
		} finally {
			lock.unlock();
		}
	}
	
	protected static Cursor execReadableSQL(String sql){
		lock.lock();
		Cursor result;
		try{
			SQLiteDatabase db = INSTANCE.getReadableDatabase();
			result = db.rawQuery(sql, null);
		} finally {
			lock.unlock();
		}
		return result;
	}
	
	protected static Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
		lock.lock();
		Cursor result;
		try{
			SQLiteDatabase db = INSTANCE.getReadableDatabase();
			result = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		} finally {
			lock.unlock();
		}
		return result;
	}
	
	protected static Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit){
		lock.lock();
		Cursor result;
		try{
			SQLiteDatabase db = INSTANCE.getReadableDatabase();
			result = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		} finally {
			lock.unlock();
		}
		return result;
	}
	
	protected static int delete(String table, String selection, String[] selectionArgs){
		lock.lock();
		int result;
		try {
			SQLiteDatabase db = INSTANCE.getWritableDatabase();
			result = db.delete(table, selection, selectionArgs);
		} finally {
			lock.unlock();
		}
		return result;
	}
	
	protected static int update(String table, ContentValues values, String whereClause, String[] whereArgs){
		lock.lock();
		int result;
		try {
			SQLiteDatabase db = INSTANCE.getWritableDatabase();
			result = db.update(table, values, whereClause, whereArgs);
		} finally {
			lock.unlock();
		}
		return result;
	}
	
	protected static long insert(String table, ContentValues values){
		lock.lock();
		long result;
		try {
			SQLiteDatabase db = INSTANCE.getWritableDatabase();
			result = db.insert(table, null, values);
		} finally {
			lock.unlock();
		}
		return result;
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	
	public boolean isLocked(){
		return lock.isLocked();
	}

	/**
	 * Currently unthread-safe singleton implementationation for accessing the db.
	 * @return Instance of the Database.
	 */
	@Deprecated
	protected static Database getInstance(){
		return INSTANCE;
	}
}
