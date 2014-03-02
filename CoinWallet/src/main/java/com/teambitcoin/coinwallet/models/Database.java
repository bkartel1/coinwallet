package com.teambitcoin.coinwallet.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.locks.ReentrantLock;


/**
 * Thread-safe wrapper class for the database, with access generally restricted
 * to members of the model package.
 * 
 * @author Michael Williams
 * 
 */
public class Database extends SQLiteOpenHelper {
    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "coinwallet.db";
    private static Database INSTANCE;
    
    private static final ReentrantLock lock = new ReentrantLock();
    
    public static void initializeDatabase(Context context) {
        if (INSTANCE == null) {
            lock.lock();
            try {
                INSTANCE = new Database(context);
            }
            finally {
                lock.unlock();
            }
        }
    }
    
    private Database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(User.getSQLInitQuery());
        db.execSQL(AddressDatabaseHandler.getAddresseSQLInitQuery());
        db.execSQL(Transaction.getSQLInitQuery());
    }
    
    /**
     * Performs raw SQL on writable version of the database. Rather than using
     * this, insert, update or delete should be used instead.
     * 
     * @param sql
     */
    protected static void execWritableSQL(String sql) {
        lock.lock();
        try {
            SQLiteDatabase db = INSTANCE.getWritableDatabase();
            db.execSQL(sql);
        }
        finally {
            lock.unlock();
        }
    }
    
    /**
     * Performs raw SQL on read-only version of the database. Rather than using
     * this, query should be used instead.
     * 
     * @param sql
     * @return Cursor for navigating the result
     */
    protected static Cursor execReadableSQL(String sql) {
        lock.lock();
        Cursor result;
        try {
            SQLiteDatabase db = INSTANCE.getReadableDatabase();
            result = db.rawQuery(sql, null);
        }
        finally {
            lock.unlock();
        }
        return result;
    }
    
    /**
     * Wrapper for SQLiteDatabase's query method. This is the preferred way to
     * access data from the database.
     * 
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     * @see android.database.sqlite.SQLiteDatabase
     */
    protected static Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
            String groupBy, String having, String orderBy) {
        lock.lock();
        Cursor result;
        try {
            SQLiteDatabase db = INSTANCE.getReadableDatabase();
            result = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        }
        finally {
            lock.unlock();
        }
        return result;
    }
    
    /**
     * Wrapper for SQLiteDatabase's query method. This is the preferred way to
     * access data from the database.
     * 
     * @param table
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     * @see android.database.sqlite.SQLiteDatabase
     */
    protected static Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
            String groupBy, String having, String orderBy, String limit) {
        lock.lock();
        Cursor result;
        try {
            SQLiteDatabase db = INSTANCE.getReadableDatabase();
            result = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        }
        finally {
            lock.unlock();
        }
        return result;
    }
    
    /**
     * Wrapper for SQLiteDatabase's delete method.
     * 
     * @param table
     * @param selection
     * @param selectionArgs
     * @return Number of rows deleted if selection is passed. To remove all
     *         rows, and still get a deletion count, pass "1" for selection.
     * @see android.database.sqlite.SQLiteDatabase
     */
    protected static int delete(String table, String selection, String[] selectionArgs) {
        lock.lock();
        int result;
        try {
            SQLiteDatabase db = INSTANCE.getWritableDatabase();
            result = db.delete(table, selection, selectionArgs);
        }
        finally {
            lock.unlock();
        }
        return result;
    }
    
    /**
     * Wrapper for SQLiteDatabase's update method.
     * 
     * @param table
     * @param values
     * @param whereClause
     * @param whereArgs
     * @return Number of rows affected.
     * @see android.database.sqlite.SQLiteDatabase
     */
    protected static int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        lock.lock();
        int result;
        try {
            SQLiteDatabase db = INSTANCE.getWritableDatabase();
            result = db.update(table, values, whereClause, whereArgs);
        }
        finally {
            lock.unlock();
        }
        return result;
    }
    
    /**
     * Wrapper for SQLiteDatabase's insert method.
     * 
     * @param table
     * @param values
     * @return row ID of inserted row, or -1 if an error occured
     * @see android.database.sqlite.SQLiteDatabase
     */
    protected static long insert(String table, ContentValues values) {
        lock.lock();
        long result;
        try {
            SQLiteDatabase db = INSTANCE.getWritableDatabase();
            result = db.insert(table, null, values);
        }
        finally {
            lock.unlock();
        }
        return result;
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        
    }
    
    public boolean isLocked() {
        return lock.isLocked();
    }
    
    /**
     * Currently unthread-safe singleton implementation for accessing the
     * db. Deprecated in preference for thread-safe methods.
     * 
     * @return Instance of the Database.
     * @deprecated Use the thread-safe Static methods which access the db
     *             instead of providing the access details.
     */
    @Deprecated
    protected static Database getInstance() {
        return INSTANCE;
    }
}
