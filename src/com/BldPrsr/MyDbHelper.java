/**
 * 
 */
package com.BldPrsr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
	private static final int DATABASE_VERSION = 9;

	private static final String BPDATA_TABLE_NAME = "bpData";
	private static final String ID_KEY = "_id";
	private static final String NAME_KEY = "name";
	private static final String MDATE_KEY = "mDate";
	private static final String MDATE_DAY_KEY = "mDay";
	private static final String MDATE_MONTH_KEY = "mMonth";
	private static final String MDATE_YEAR_KEY = "mYear";
	private static final String MTIME_KEY = "mTime";
	private static final String MTIME_MINS_KEY = "mYear";
	private static final String MTIME_HOURS_KEY = "mYear";
	private static final String SPRSR_KEY = "sPrsr";
	private static final String DPRSR_KEY = "dPrsr";
	private static final String PULSE_KEY = "pulse";

	private static BackupManager bkpMgm = null;

	public MyDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		bkpMgm = new BackupManager(context);
	}

	public int version() {
		return DATABASE_VERSION;
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
					+ "mHour TEXT," + "mMins TEXT," + "mDay TEXT, "
					+ "mMonth TEXT, " + "mYear TEXT, " + "sPrsr TEXT, "
					+ "dPrsr TEXT, " + "pulse TEXT );");
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
		String newTmStr = "00:00";
		String mDate;
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
					+ "name TEXT, " + "mDate DATE, " + "mDay TEXT, "
					+ "mMonth TEXT, " + "mYear TEXT, " + "mTime DATE, "
					+ "mHour TEXT," + "mMins TEXT," + "sPrsr TEXT, "
					+ "dPrsr TEXT, " + "pulse TEXT" + ");";
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
						mDate = from.getString(from.getColumnIndex("mDate"));
						// Split up date into day, month, year
						BldPrsrLogger.i(TAG, SubTag + "Date: " + mDate);
						// Get the month
						String Year = mDate.substring(0, 4);
						vals.put("mYear", Year);
						String Month = mDate.substring(5, 7);
						vals.put("mMonth", Month);
						String Day = mDate.substring(8, 10);
						vals.put("mDay", Day);

						// Systolic value is always lower then diastolic
						Integer s = Integer.parseInt(from.getString(from
								.getColumnIndex("sPrsr")));
						Integer d = Integer.parseInt(from.getString(from
								.getColumnIndex("dPrsr")));
						if (s < d) {
							int t = d;
							d = s;
							s = t;
						}
						vals.put("sPrsr", s.toString());
						vals.put("dPrsr", d.toString());

						vals.put("pulse",
								from.getString(from.getColumnIndex("pulse")));
						if (oldVersion >= 4) {
							newTmStr = from.getString(from
									.getColumnIndex("mTime"));
						} else {
							newTmStr = "00:00";
						}
						vals.put("mTime", newTmStr);
						mDate = Year.toString() + "-" + Month.toString() + "-"
								+ Day.toString();
						vals.put("mDate", mDate);
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

	public List<BldPrsrBasicData> getCurrentMonthData() {
		List<BldPrsrBasicData> dataList = new ArrayList<BldPrsrBasicData>();
		SubTag = "getCurrentMonthData(): ";
		// Get current month
		Calendar c = Calendar.getInstance();
		Integer year = c.get(Calendar.YEAR);
		Integer month = c.get(Calendar.MONTH);
		String m = String.format(Locale.getDefault(), "%02d", month);
		String selectQuery = "SELECT * FROM " + BPDATA_TABLE_NAME
				+ " WHERE mYear = '" + year.toString() + "' AND mMonth = '" + m
				+ "';";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				BldPrsrBasicData basicData = new BldPrsrBasicData();
				basicData
						.setId(cursor.getString(cursor.getColumnIndex(ID_KEY)));
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

				// Starting with v5 of DB
				basicData.setmDay(cursor.getString(cursor
						.getColumnIndex(MDATE_DAY_KEY)));
				basicData.setmMonth(cursor.getString(cursor
						.getColumnIndex(MDATE_MONTH_KEY)));
				basicData.setmYear(cursor.getString(cursor
						.getColumnIndex(MDATE_YEAR_KEY)));
				basicData.setmHours(cursor.getString(cursor
						.getColumnIndex(MTIME_HOURS_KEY)));
				basicData.setmMinutes(cursor.getString(cursor
						.getColumnIndex(MTIME_MINS_KEY)));

				// Adding basic data to list
				dataList.add(basicData);
			} while (cursor.moveToNext());
		}
		try {
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
		return dataList;
	}

	public List<BldPrsrBasicData> getLast30EntriesData() {
		List<BldPrsrBasicData> dataList = new ArrayList<BldPrsrBasicData>();
		SubTag = "getLast30EntriesData(): ";

		String selectQuery = "SELECT * FROM " + BPDATA_TABLE_NAME;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Which entry should we start with
		int startEntry = cursor.getCount() - 30;
		if (startEntry < 0)
			startEntry = 0;
		BldPrsrLogger.e(TAG, SubTag + "Starting with record number "
				+ startEntry);
		// looping through all rows and adding to list
		if (cursor.move(startEntry)) {
			do {
				BldPrsrBasicData basicData = new BldPrsrBasicData();
				basicData
						.setId(cursor.getString(cursor.getColumnIndex(ID_KEY)));
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

				// Starting with v5 of DB
				basicData.setmDay(cursor.getString(cursor
						.getColumnIndex(MDATE_DAY_KEY)));
				basicData.setmMonth(cursor.getString(cursor
						.getColumnIndex(MDATE_MONTH_KEY)));
				basicData.setmYear(cursor.getString(cursor
						.getColumnIndex(MDATE_YEAR_KEY)));
				basicData.setmHours(cursor.getString(cursor
						.getColumnIndex(MTIME_HOURS_KEY)));
				basicData.setmMinutes(cursor.getString(cursor
						.getColumnIndex(MTIME_MINS_KEY)));

				// Adding basic data to list
				dataList.add(basicData);
			} while (cursor.moveToNext());
		}
		try {
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
		return dataList;
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

					// Starting with v5 of DB
					basicData.setmDay(cursor.getString(cursor
							.getColumnIndex(MDATE_DAY_KEY)));
					basicData.setmMonth(cursor.getString(cursor
							.getColumnIndex(MDATE_MONTH_KEY)));
					basicData.setmYear(cursor.getString(cursor
							.getColumnIndex(MDATE_YEAR_KEY)));
					basicData.setmHours(cursor.getString(cursor
							.getColumnIndex(MTIME_HOURS_KEY)));
					basicData.setmMinutes(cursor.getString(cursor
							.getColumnIndex(MTIME_MINS_KEY)));

					// Adding basic data to list
					dataList.add(basicData);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
		return dataList;
	}

	public List<BldPrsrBasicData> getData(String sql) {
		List<BldPrsrBasicData> dataList = new ArrayList<BldPrsrBasicData>();
		SubTag = "getAllData(): ";
		try {
			String selectQuery = "SELECT * FROM " + BPDATA_TABLE_NAME + " "
					+ sql;
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

					// Starting with v5 of DB
					basicData.setmDay(cursor.getString(cursor
							.getColumnIndex(MDATE_DAY_KEY)));
					basicData.setmMonth(cursor.getString(cursor
							.getColumnIndex(MDATE_MONTH_KEY)));
					basicData.setmYear(cursor.getString(cursor
							.getColumnIndex(MDATE_YEAR_KEY)));
					basicData.setmHours(cursor.getString(cursor
							.getColumnIndex(MTIME_HOURS_KEY)));
					basicData.setmMinutes(cursor.getString(cursor
							.getColumnIndex(MTIME_MINS_KEY)));

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
		// Starting with v5 of DB
		values.put(MDATE_DAY_KEY, bd.getmDay());
		values.put(MDATE_MONTH_KEY, bd.getmMonth());
		values.put(MDATE_YEAR_KEY, bd.getmYear());
		values.put(MTIME_HOURS_KEY, bd.getmHours());
		values.put(MTIME_MINS_KEY, bd.getmMinutes());

		// updating row
		rc = db.update(BPDATA_TABLE_NAME, values, ID_KEY + " = ?",
				new String[] { String.valueOf(bd.getId()) });

		bkpMgm.dataChanged();
		return rc;
	}
}
