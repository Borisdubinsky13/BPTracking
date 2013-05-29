/**
 * 
 */
package com.BldPrsr;

import java.util.ArrayList;
import java.util.List;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author boris
 * 
 */
public class MyDbHelper extends SQLiteOpenHelper {

	public static String TAG = "MyDbHelper";
	public static String SubTag;

	private static final String DATABASE_NAME = "bldprsr.db";
	private static final int DATABASE_VERSION = 4;

	private static final String BPDATA_TABLE_NAME = "bpData";
	private static final String ID_KEY = "_id";
	private static final String NAME_KEY = "name";
	private static final String MDATE_KEY = "mDate";
	private static final String MTIME_KEY = "mTime";
	private static final String SPRSR_KEY = "sPrsr";
	private static final String DPRSR_KEY = "dPrsr";
	private static final String PULSE_KEY = "pulse";

	private static BackupManager bkpMgm = null;

	public MyDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		bkpMgm = new BackupManager(context);
	}

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public MyDbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 * @param errorHandler
	 */
	public MyDbHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		SubTag = "onCreate(): ";
		BldPrsrLogger.i(TAG, SubTag + "Creating BPdata table");
		Cursor c = db.rawQuery(
				"SELECT name FROM sqlite_master WHERE type='table' AND name='"
						+ BPDATA_TABLE_NAME + "'", null);

		if (c.getCount() == 0) {
			db.execSQL("CREATE TABLE " + BPDATA_TABLE_NAME
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "name TEXT, " + "mDate DATE, " + "mTime DATE, "
					+ "sPrsr TEXT, " + "dPrsr TEXT, " + "pulse TEXT );");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		SubTag = "onUpgrade(): ";
		BldPrsrLogger.i(TAG, SubTag + "starting an upgrade from " + oldVersion
				+ " to " + newVersion);
		/*
		 * This is a first upgrade.
		 */
		try {
			Cursor from;
			String sql = "ALTER TABLE " + BPDATA_TABLE_NAME + " RENAME TO "
					+ BPDATA_TABLE_NAME + "_OLD;";
			BldPrsrLogger.i(TAG, SubTag + "exec sql: " + sql);
			db.execSQL(sql);

			sql = "CREATE TABLE " + BPDATA_TABLE_NAME
					+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "name TEXT, " + "mDate DATE, " + "mTime DATE, "
					+ "sPrsr TEXT, " + "dPrsr TEXT, " + "pulse TEXT" + ");";
			db.execSQL(sql);
			BldPrsrLogger.i(TAG, SubTag + "exec sql: " + sql);
			sql = "SELECT * FROM " + BPDATA_TABLE_NAME + "_OLD;";
			BldPrsrLogger.i(TAG, SubTag + "exec sql: " + sql);
			from = db.rawQuery(sql, null);
			if (from.moveToFirst()) {
				do {
					ContentValues vals = new ContentValues();
					/* Check to make sure that no garbage values */
					if (from.getString(from.getColumnIndex("dPrsr")).length() < 4
							&& from.getString(from.getColumnIndex("sPrsr"))
									.length() < 4
							&& from.getString(from.getColumnIndex("pulse"))
									.length() < 4) {
						vals.put("name",
								from.getString(from.getColumnIndex("name")));
						vals.put("mDate",
								from.getString(from.getColumnIndex("mDate")));
						vals.put("sPrsr",
								from.getString(from.getColumnIndex("dPrsr")));
						vals.put("dPrsr",
								from.getString(from.getColumnIndex("sPrsr")));
						vals.put("pulse",
								from.getString(from.getColumnIndex("pulse")));
						vals.put("mTime", "00:00");
					}
					db.insert(BPDATA_TABLE_NAME, null, vals);
				} while (from.moveToNext());
			}
			sql = "DROP TABLE IF EXISTS " + BPDATA_TABLE_NAME + "_OLD;";
			BldPrsrLogger.i(TAG, SubTag + "exec sql: " + sql);
			db.execSQL(sql);
			bkpMgm.dataChanged();
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
			String sql = "DROP TABLE IF EXISTS " + BPDATA_TABLE_NAME + "_OLD;";
			db.execSQL(sql);
		}
	}

	public void deleteTbl() {
		SubTag = "deleteTbl(): ";
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			String sql = "DROP TABLE IF EXISTS " + BPDATA_TABLE_NAME + ";";
			db.execSQL(sql);
			// Create empty table, so insert would not fail
			onCreate(db);
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
	}

	public void insert(ContentValues values) {
		SubTag = "insert(): ";
		try {
			BldPrsrLogger.i(TAG, SubTag + "Inserting a record");

			SQLiteDatabase db = this.getWritableDatabase();

			db.insert(BPDATA_TABLE_NAME, null, values);
			db.close();
			BldPrsrLogger.i(TAG, SubTag + "Done inserting a record");
			bkpMgm.dataChanged();

		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
	}

	public List<BldPrsrBasicData> getAllData() {
		List<BldPrsrBasicData> dataList = new ArrayList<BldPrsrBasicData>();
		SubTag = "getAllData(): ";
		try {
			String selectQuery = "SELECT * FROM " + BPDATA_TABLE_NAME;
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					BldPrsrBasicData basicData = new BldPrsrBasicData();
					basicData.setId(cursor.getString(cursor
							.getColumnIndex(ID_KEY)));
					basicData.setName(cursor.getString(cursor
							.getColumnIndex(NAME_KEY)));
					basicData.setdPrsr(cursor.getString(cursor
							.getColumnIndex(DPRSR_KEY)));
					basicData.setsPrsr(cursor.getString(cursor
							.getColumnIndex(SPRSR_KEY)));
					basicData.setPulse(cursor.getString(cursor
							.getColumnIndex(PULSE_KEY)));
					basicData.setmDate(cursor.getString(cursor
							.getColumnIndex(MDATE_KEY)));
					basicData.setmTime(cursor.getString(cursor
							.getColumnIndex(MTIME_KEY)));
					// Adding basic data to list
					dataList.add(basicData);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
		return dataList;
	}

	public int getRecordCount() {
		SubTag = "getRecordCount(): ";
		int cnt = 0;
		try {
			String countQuery = "SELECT  * FROM " + BPDATA_TABLE_NAME;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			cnt = cursor.getCount();
			cursor.close();
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
		return cnt;
	}

	public void deleteRecord(BldPrsrBasicData bd) {
		SubTag = "deleteRecord(): ";
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(BPDATA_TABLE_NAME, ID_KEY + " = ?",
					new String[] { String.valueOf(bd.getId()) });
			db.close();
			bkpMgm.dataChanged();
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
	}

	public void deleteRecord(int recId) {
		SubTag = "deleteRecord(): ";
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(BPDATA_TABLE_NAME, ID_KEY + " = ?",
					new String[] { String.valueOf(recId) });
			db.close();
			bkpMgm.dataChanged();
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
	}

	public int updateDataRecord(BldPrsrBasicData bd) {
		SubTag = "updateDataRecord(): ";
		int rc;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ID_KEY, bd.getId());
		values.put(NAME_KEY, bd.getName());
		values.put(SPRSR_KEY, bd.getsPrsr());
		values.put(DPRSR_KEY, bd.getdPrsr());
		values.put(PULSE_KEY, bd.getPulse());
		values.put(MDATE_KEY, bd.getmDate());
		values.put(MTIME_KEY, bd.getmTime());

		// updating row
		rc = db.update(BPDATA_TABLE_NAME, values, ID_KEY + " = ?",
				new String[] { String.valueOf(bd.getId()) });

		bkpMgm.dataChanged();
		return rc;
	}
}
