/**
 * 
 */
package com.BldPrsr;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * @author Boris
 *
 */
public class BldPrsrTrackerProvider extends ContentProvider 
{
	public static String TAG="BldPrsr";
	public static String SubTag="BldPrsrTrackerProvider: ";

	private static final String DATABASE_NAME = "bldprsr.db";
	private static final int DATABASE_VERSION = 2;
	
	private static final String BPUSER_TABLE_NAME = "bpUsers";
	private static final String BPDATA_TABLE_NAME = "bpData";
	private static final String BPSTATUS_TABLE_NAME = "bpStatus";

	private	static	HashMap<String, String> BPUSER_PROJECTION_MAP;
	private	static	HashMap<String, String> BPDATA_PROJECTION_MAP;
	private	static	HashMap<String, String> BPSTAT_PROJECTION_MAP;
	
	public static final String AUTHORITY = 
		"com.BldPrsr.provider.userContentProvider";

	private static final int BPUSER = 1;
	private static final int BPDATA = 2;
	private static final int BPSTATUS = 3;
	
	private static final UriMatcher sURIMatcher = buildUriMatcher();
	
	public static String getMd5Hash(String input) 
	{
        try {
        	MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1,messageDigest);
            String md5 = number.toString(16);
           
            while (md5.length() < 32)
            	md5 = "0" + md5;
           
            return md5;
        } catch(NoSuchAlgorithmException e) 
        {
        	BldPrsrLogger.e(TAG, SubTag + e.getMessage());
            return null;
        }
	}
	
	private static UriMatcher buildUriMatcher() 
	{
		UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
		matcher.addURI(AUTHORITY, "bpUsers", BPUSER);
		matcher.addURI(AUTHORITY, "bpData", BPDATA);
		matcher.addURI(AUTHORITY, "bpStatus", BPSTATUS);
		return matcher;
	}
	
	private static class DbAdapter extends SQLiteOpenHelper
	{
		public DbAdapter(Context context) 
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			BldPrsrLogger.i(TAG, SubTag + "Creating Users table");
			try 
			{
				Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" +
						BPUSER_TABLE_NAME + "'", null);
				if (c.getCount()==0) 
				{
					db.execSQL("CREATE TABLE " + BPUSER_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
								"name TEXT UNIQUE, " +
								"passwd TEXT, " +
								"firstName TEXT, " +
								"lastName TEXT, " +
								"gender TEXT, " +
								"age TEXT, " +
								"email TEXT);");				
					}
			
				BldPrsrLogger.i(TAG, SubTag + "Creating BPdata table");
				c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"  +
						BPDATA_TABLE_NAME + "'", null);

				if (c.getCount()==0) 
				{
					db.execSQL("CREATE TABLE " + BPDATA_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
							"name TEXT, " +
							"mDate DATE, " +
							"sPrsr TEXT, " +
							"dPrsr TEXT, " +
							"pulse TEXT" +
 							");");
				}

				BldPrsrLogger.i(TAG, SubTag + "Creating BPStatus status table");
				c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='"  +
						BPSTATUS_TABLE_NAME + "'", null);
				if (c.getCount()==0) 
				{
					db.execSQL("CREATE TABLE " + BPSTATUS_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
							"name TEXT UNIQUE, " +
							"status TEXT);");
				}			
			}
			catch (Exception e)
			{
				BldPrsrLogger.e(TAG, SubTag + e.getMessage());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			BldPrsrLogger.i(TAG , SubTag + "starting an upgrade from " + oldVersion + " to " + newVersion);
			// First rename the table to <table>_OLD
			/* 
			 * This is a first upgrade.
			 */
			try
			{
				Cursor from;
				String sql = "ALTER TABLE " + BPDATA_TABLE_NAME + " RENAME TO " + BPDATA_TABLE_NAME + "_OLD;";
				BldPrsrLogger.i(TAG, SubTag + "exec sql: " + sql);
				db.execSQL(sql);
				
				sql = "CREATE TABLE " + BPDATA_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"name TEXT, " +
						"mDate DATE, " +
						"sPrsr TEXT, " +
						"dPrsr TEXT, " +
						"pulse TEXT" +
							");";
				db.execSQL(sql);
				BldPrsrLogger.i(TAG, SubTag + "exec sql: " + sql);
				sql = "SELECT * FROM " + BPDATA_TABLE_NAME + "_OLD;";
				BldPrsrLogger.i(TAG, SubTag + "exec sql: " + sql);
				from = db.rawQuery(sql,null);
				if ( from.moveToFirst() )
				{
					do
					{
						ContentValues vals = new ContentValues();

						vals.put("name", from.getString(from.getColumnIndex("name")));
						vals.put("mDate", from.getString(from.getColumnIndex("mDate")));
						vals.put("sPrsr", from.getString(from.getColumnIndex("dPrsr")));
						vals.put("dPrsr", from.getString(from.getColumnIndex("sPrsr")));
						vals.put("pulse", from.getString(from.getColumnIndex("pulse")));

						db.insert(BPDATA_TABLE_NAME,null,vals);
					} while ( from.moveToNext() );
				}
				sql = "DROP TABLE IF EXISTS " + BPDATA_TABLE_NAME + "_OLD;";
				BldPrsrLogger.i(TAG, SubTag + "exec sql: " + sql);
				db.execSQL(sql);
			}
			catch (Exception e)
			{
				BldPrsrLogger.e(TAG, SubTag + e.getMessage());
				String sql = "DROP TABLE IF EXISTS " + BPDATA_TABLE_NAME + "_OLD;";
				db.execSQL(sql);
			}
		}
	}

	private DbAdapter dbHelper;

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) 
	{
	      SQLiteDatabase db = dbHelper.getWritableDatabase();
	      BldPrsrLogger.i(TAG, SubTag + "Deleting SQL: " + where);
	      int count;
			switch (sURIMatcher.match(uri)) 
			{
	      	case BPUSER:
	      		count = db.delete(BPUSER_TABLE_NAME, where, whereArgs);
	      		break;
	      	case BPDATA:
	      		count = db.delete(BPDATA_TABLE_NAME, where, whereArgs);
	      		break;
	      	case BPSTATUS:
	      		count = db.delete(BPSTATUS_TABLE_NAME, where, whereArgs);
	      		break;
	      	default:
	      		throw new IllegalArgumentException("Unknown URI " + uri);
	      }
	      getContext().getContentResolver().notifyChange(uri, null);
	      return count;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) 
	{
		switch (sURIMatcher.match(uri)) 
		{
			case BPUSER:
				return "vnd.BldPrsr.cursor.item/bpUsers";
			case BPDATA:
				return "vnd.BldPrsr.cursor.dir/bpData";
	      	case BPSTATUS:
	      		return "vnd.BldPrsr.cursor.dir/bpStatus";
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) 
	{
		BldPrsrLogger.i(TAG, SubTag + "Inserting a record");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (sURIMatcher.match(uri)) 
		{
			case BPUSER:
            	db.insert(BPUSER_TABLE_NAME,null,values);
            	db.close();
				break;
			case BPDATA:
            	db.insert(BPDATA_TABLE_NAME,null,values);
            	db.close();
				break;
			case BPSTATUS:
				db.insert(BPSTATUS_TABLE_NAME ,null,values);
            	db.close();
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		BldPrsrLogger.i(TAG, SubTag + "Done inserting a record");
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() 
	{
		dbHelper = new DbAdapter(getContext());
		BldPrsrLogger.i(TAG, SubTag + "Created dbHelper");	
		
		return true;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) 
	{
		Cursor	result;
    	
		try
		{
			String	sqlStm = "SELECT ";
			// final DbAdapter dba = new DbAdapter(getContext());
			SQLiteDatabase dba = dbHelper.getReadableDatabase();
			BldPrsrLogger.i(TAG, SubTag + "Started Query");
			switch (sURIMatcher.match(uri)) 
			{
				case BPUSER:
					BldPrsrLogger.i(TAG, SubTag + "building query for USER table");
					sqlStm += "name FROM ";
					sqlStm += BPUSER_TABLE_NAME;
					if ( selection != null )
					{
						sqlStm += " WHERE ";
						sqlStm += selection;
					}
					BldPrsrLogger.i(TAG, SubTag + "sql: " + sqlStm);
					break;
				case BPDATA:
					BldPrsrLogger.i(TAG, SubTag + "building query for DATA table");

					sqlStm += "_id,name,mDate,dPrsr,sPrsr,pulse FROM ";
					sqlStm += BPDATA_TABLE_NAME;
					if ( selection != null )
					{
						sqlStm += " WHERE ";
						sqlStm += selection;
					}
					sqlStm += " order by mDate asc";
					BldPrsrLogger.i(TAG, SubTag + "sql: " + sqlStm);
					break;
				case BPSTATUS:
					BldPrsrLogger.i(TAG, SubTag + "building query for STATUS table");
					sqlStm += "_id,name,status FROM ";
					sqlStm += BPSTATUS_TABLE_NAME;
					if ( selection != null )
					{
						sqlStm += " WHERE ";
						sqlStm += selection;
					}
					BldPrsrLogger.i(TAG, SubTag + "sql: " + sqlStm);
					break;
				default:
					BldPrsrLogger.i(TAG, SubTag + "Unknown request");
					throw new IllegalArgumentException("Unknown URI " + uri);
			}
			BldPrsrLogger.i(TAG, SubTag + "Trying to execute a query.");
			// result = qb.query(db, projection, selection, selectionArgs, null, null, null);
			result = dba.rawQuery(sqlStm, null);
			BldPrsrLogger.i(TAG, SubTag + "Returning query result");
		}
		catch (Exception e)
		{
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
			result = null;
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) 
	{
		int count = 0;
		try
		{
			BldPrsrLogger.i(TAG, SubTag + "Started....");
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			getContext().getContentResolver().notifyChange(uri, null);	  	
			
			switch (sURIMatcher.match(uri)) 
			{
		    case BPUSER:
	            count = db.update(
	            			BPUSER_TABLE_NAME, 
	            			values,
	            			selection, 
	            			selectionArgs);
		    	break;
		    case BPDATA:
		    	BldPrsrLogger.i(TAG, SubTag + "Updating " + BPDATA_TABLE_NAME);
		    	
	            count = db.update(
	            			BPDATA_TABLE_NAME, 
	            			values,
	            			selection, 
	            			selectionArgs);
		      		break;
		      	case BPSTATUS:
		            count = db.update(
		            		BPSTATUS_TABLE_NAME, 
	            			values,
	            			selection, 
	            			selectionArgs);
		      		break;
		      	default:
		      		throw new IllegalArgumentException("Unknown URI " + uri);
		      }
		      getContext().getContentResolver().notifyChange(uri, null);
		      BldPrsrLogger.i(TAG, SubTag + "Ended....");
		}
		catch (Exception e)
		{
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
		return count;
	}
	
	static
	{
		BPUSER_PROJECTION_MAP = new HashMap<String,String>();
		BPUSER_PROJECTION_MAP.put(BPUSER_TABLE_NAME, "name" );
		BPUSER_PROJECTION_MAP.put(BPUSER_TABLE_NAME, "fname" );
		BPUSER_PROJECTION_MAP.put(BPUSER_TABLE_NAME, "lname" );
		BPUSER_PROJECTION_MAP.put(BPUSER_TABLE_NAME, "email" );
		BPUSER_PROJECTION_MAP.put(BPUSER_TABLE_NAME, "passwd");
		BPUSER_PROJECTION_MAP.put(BPUSER_TABLE_NAME, "gender");
		BPUSER_PROJECTION_MAP.put(BPUSER_TABLE_NAME, "age");

		BPDATA_PROJECTION_MAP = new HashMap<String,String>();
		BPDATA_PROJECTION_MAP.put(BPDATA_TABLE_NAME, "name" );
		BPDATA_PROJECTION_MAP.put(BPDATA_TABLE_NAME, "mDate" );
		BPDATA_PROJECTION_MAP.put(BPDATA_TABLE_NAME, "dPrsr");
		BPDATA_PROJECTION_MAP.put(BPDATA_TABLE_NAME, "sPrsr");
		BPDATA_PROJECTION_MAP.put(BPDATA_TABLE_NAME, "pulse");
		
		BPSTAT_PROJECTION_MAP = new HashMap<String,String>();
		BPSTAT_PROJECTION_MAP.put(BPSTATUS_TABLE_NAME, "name");
		BPSTAT_PROJECTION_MAP.put(BPSTATUS_TABLE_NAME, "status");
	}
}
