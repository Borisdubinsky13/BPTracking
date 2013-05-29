package com.BldPrsr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class BldPrsrMain extends Activity {
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */

	private Context context;
	/** Called when the activity is first created. */
	public static final String PREFS_NAME = "BldPrsrFile";
	private static final String PREF_USERNAME = "username";

	public static String TAG = "BldPrsrMain";
	public static String SubTag = "BldPrsrMain: ";
	String[] projection = new String[] { "_id", "name", "mDate", "dPrsr",
			"sPrsr", "pulse" };

	private XYMultipleSeriesRenderer mRenderer = null;

	/* Refresh the view that has the chart */
	private XYSeries diastolic = new XYValueSeries("Diastolic");
	private XYSeries systolic = new XYValueSeries("Systolic");
	private XYSeries sPulse = new XYValueSeries("Pulse");

	private XYSeries diasDef = new XYValueSeries("Base Diastolic (80)");
	private XYSeries systDef = new XYValueSeries("Base Systolic (120)");
	private XYSeries pulsDef = new XYValueSeries("Base Pulse (65)");

	/* Create the series for the trend line */
	private BldPrsrTrendline diasTrend = new BldPrsrTrendline("Dias. Trend");
	private BldPrsrTrendline systTrend = new BldPrsrTrendline("Syst. Trend");
	private BldPrsrTrendline pulseTrend = new BldPrsrTrendline("Pulse Trend");

	private final int[] colors = new int[] { Color.CYAN, Color.RED,
			Color.YELLOW, Color.CYAN, Color.RED, Color.YELLOW };
	private final PointStyle[] styles = new PointStyle[] { PointStyle.SQUARE,
			PointStyle.DIAMOND, PointStyle.CIRCLE, PointStyle.SQUARE,
			PointStyle.DIAMOND, PointStyle.CIRCLE };

	private Handler mHandler = new Handler();
	private ProgressDialog progressDialog;
	private int currentCount;

	String eventDate;
	Button dateB;
	Button addB;
	String username;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent iAbout = new Intent(this, AboutHandler.class);
		Intent iList = new Intent(this, BldPrsrList.class);
		// Intent iAddUser = new Intent(this, BldPrsrSetupWin.class);

		// Handle item selection
		switch (item.getItemId()) {
		/*
		 * case R.id.AddUser: BldPrsrLogger.i(TAG, SubTag + "User " +
		 * "trying to start AddUser"); startActivity(iAddUser); return true;
		 */
		case R.id.List:
			BldPrsrLogger.i(TAG, SubTag + "User " + "trying to start List");
			startActivity(iList);
			return true;
		case R.id.About:
			BldPrsrLogger.i(TAG, SubTag + "User " + "trying to start ABOUT");
			startActivity(iAbout);
			return true;
		case R.id.importDB:
			BldPrsrLogger.i(TAG, SubTag + "trying to import data");
			Intent importAct = new Intent(this, ImportActivity.class);
			startActivity(importAct);
			return true;
		case R.id.Export:
			try {
				int curPercent = 0;

				currentCount = 0;
				progressDialog = new ProgressDialog(context);
				progressDialog.setCancelable(true);
				progressDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setProgress(0);
				progressDialog.show();
				BldPrsrLogger.i(TAG, SubTag + "Process Dialog has been setup!");

				String fnoSDName = "/bldprsr.csv";
				String fname = Environment.getExternalStorageDirectory()
						+ fnoSDName;
				BldPrsrLogger.i(TAG, SubTag + "User "
						+ "Trying to export data to CSV. File: " + fname);
				progressDialog.setMessage("Importing "
						+ Environment.getExternalStorageDirectory() + fname
						+ " ...");

				MyDbHelper db = new MyDbHelper(context);
				List<BldPrsrBasicData> dataList = new ArrayList<BldPrsrBasicData>();
				dataList = db.getAllData();
				int recCount = db.getRecordCount();
				progressDialog.setMax((int) recCount);
				progressDialog.show();
				File myFile = new File(fname);
				myFile.createNewFile();
				FileOutputStream fOut = new FileOutputStream(myFile);
				BufferedOutputStream bos = new BufferedOutputStream(fOut);

				String query = "name = '" + username + "'";
				BldPrsrLogger.i(TAG, SubTag + "Query: " + query);

				BldPrsrLogger.i(TAG, SubTag + "there are " + recCount
						+ " records");
				String strOut = "Date,Time,Systolic,Diastolic,Pulse,Name\n";
				bos.write(strOut.getBytes());

				for (BldPrsrBasicData bd : dataList) {
					String nameStr = bd.getName();
					String mDateStr = bd.getmDate();
					String mTimeStr = bd.getmTime();
					String sPrsrStr = String.format("%03d",
							Integer.parseInt(bd.getsPrsr()));
					String dPrsrStr = String.format("%03d",
							Integer.parseInt(bd.getdPrsr()));
					String pulse = String.format("%03d",
							Integer.parseInt(bd.getPulse()));

					strOut = mDateStr + "," + mTimeStr + "," + sPrsrStr + ","
							+ dPrsrStr + "," + pulse + "," + nameStr + "\n";
					bos.write(strOut.getBytes());
					// Update the progress bar if needed
					currentCount++;
					int percentDone = (int) (currentCount * 100 / recCount);
					if (curPercent < percentDone) {
						curPercent = percentDone;

						if (percentDone < 100) {
							percentDone++;
							// Update the progress bar
							mHandler.post(new Runnable() {
								public void run() {
									progressDialog
											.setProgress((int) currentCount);
								}
							});
						}
					}

				}
				bos.close();
				progressDialog.dismiss();

				int duration = Toast.LENGTH_LONG;
				String text = "Data has been exported into " + fname;
				Toast toast = Toast.makeText(getApplicationContext(), text,
						duration);
				toast.show();
			} catch (Exception e) {
				BldPrsrLogger.e(TAG, SubTag + e.getMessage());
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		context = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bldprsmain);

		String androidId = Settings.Secure.getString(this.getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);
		if (androidId == null || androidId.equals("9774d56d682e549c")) {
			// We are running on the emulator. Debugging should be ON.
			BldPrsrLogger.e(TAG, SubTag
					+ "Enabeling VERBOSE debugging. androidID = " + androidId);
			BldPrsrLogger.enableLogging(Log.VERBOSE);
		} else {
			// We are running on a phone. Debugging should be OFF.
			BldPrsrLogger.e(TAG, SubTag
					+ "Enabeling ERRORS only debugging. androidID = "
					+ androidId);
			BldPrsrLogger.enableLogging(Log.ERROR);
		}
		BldPrsrLogger.enableLogging(Log.VERBOSE);
		BldPrsrLogger.i(TAG, SubTag + " Enter onCreate() ");

		// First check if there are any users. If not, then switch to setup
		// window to add first user.
		try {
			Account[] accounts = AccountManager.get(this).getAccountsByType(
					"com.google");
			BldPrsrLogger
					.i(TAG, SubTag + "Got account list. Number of entries: "
							+ accounts.length);
			if (accounts.length <= 0) {
				int duration = Toast.LENGTH_SHORT;
				String text = "Primary account is not setup. Please setup google account first.";
				Toast toast = Toast.makeText(getApplicationContext(), text,
						duration);
				toast.show();
			} else {
				String username = accounts[0].name;
				BldPrsrLogger.i(TAG, SubTag + "My email id that I want: "
						+ username);

				getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
						.putString(PREF_USERNAME, username).commit();

				// update database to make sure that all entries have username =
				// primary google account.
				// See if there are any data that has name other then current
				// username
				/*
				 * Uri tmpUri =
				 * Uri.parse("content://com.BldPrsr.provider.userContentProvider"
				 * ); tmpUri = Uri.withAppendedPath(tmpUri,"bpData");
				 * 
				 * ContentValues vals = new ContentValues(); ContentResolver cr
				 * = getContentResolver(); vals.put("name", username);
				 * cr.update(tmpUri, vals, "name != '" + username + "'", null);
				 */
			}
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
	}

	public void displayCharts() {
		SubTag = "displayCharts(): ";

		XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
		String query = null;
		int cnt = 0;
		int min = 0, max = 0;
		int k = 0;
		XYSeriesRenderer r;

		query = "name = '" + username + "'";
		BldPrsrLogger.i(TAG, SubTag + "Query: " + query);

		List<BldPrsrBasicData> dataList = new ArrayList<BldPrsrBasicData>();
		MyDbHelper db = new MyDbHelper(context);
		dataList = db.getAllData();
		int recCount = db.getRecordCount();
		// Cursor result = managedQuery(tmpUri, projection, query, null, null);
		BldPrsrLogger.i(TAG, SubTag + "there are " + recCount + " records");
		for (BldPrsrBasicData bd : dataList) {
			BldPrsrLogger.i(TAG, SubTag + "got result back from provider");

			int dValue;
			try {
				dValue = Integer.parseInt(bd.getdPrsr());
			} catch (Exception e) {
				BldPrsrLogger.e(TAG, SubTag + e.getMessage());
				dValue = 0;
			}

			diastolic.add(cnt, dValue);
			diasDef.add(cnt, 80);
			diasTrend.addXY(cnt, dValue);
			if (cnt == 0)
				min = max = dValue;
			else {
				if (dValue > max)
					max = dValue;
				if (dValue < min)
					min = dValue;
			}
			BldPrsrLogger.i(TAG, SubTag + "dPrsr: " + dValue);

			try {
				dValue = Integer.parseInt(bd.getsPrsr());
			} catch (Exception e) {
				BldPrsrLogger.e(TAG, SubTag + e.getMessage());
				dValue = 0;
			}
			if (dValue > max)
				max = dValue;
			if (dValue < min)
				min = dValue;
			systolic.add(cnt, dValue);
			systDef.add(cnt, 120);
			systTrend.addXY(cnt, dValue);
			BldPrsrLogger.i(TAG, SubTag + "sPrsr: " + dValue);

			try {
				dValue = Integer.parseInt(bd.getPulse());
			} catch (Exception e) {
				BldPrsrLogger.e(TAG, SubTag + e.getMessage());
				dValue = 0;
			}
			if (dValue > max)
				max = dValue;
			if (dValue < min)
				min = dValue;
			sPulse.add(cnt, dValue);
			pulsDef.add(cnt, 70);
			pulseTrend.addXY(cnt, dValue);
			BldPrsrLogger.i(TAG, SubTag + "Pulse: " + dValue);
			cnt++;
		}

		mDataset.addSeries(diastolic);
		mDataset.addSeries(systolic);
		mDataset.addSeries(sPulse);
		mDataset.addSeries(diasTrend.getTheTrend());
		mDataset.addSeries(systTrend.getTheTrend());
		mDataset.addSeries(pulseTrend.getTheTrend());

		if (mRenderer == null) {
			mRenderer = new XYMultipleSeriesRenderer();
			mRenderer.setAxisTitleTextSize(16);
			mRenderer.setChartTitleTextSize(20);
			mRenderer.setLabelsTextSize(15);
			mRenderer.setLegendTextSize(15);
			mRenderer.setPointSize(5f);
			mRenderer.setXLabels(0);
			mRenderer.setShowGrid(true);
			mRenderer.setDisplayChartValues(false);
			mRenderer.setChartTitle("");
			mRenderer.setXTitle("Date");
			mRenderer.setYTitle("");
			mRenderer.setXAxisMin(0);
			mRenderer.setAxesColor(Color.WHITE);
			mRenderer.setLabelsColor(Color.WHITE);

			mRenderer.setXAxisMax(cnt);
			mRenderer.setYAxisMin(min - 5);
			mRenderer.setYAxisMax(max);

			r = new XYSeriesRenderer();
			r.setColor(colors[k]);
			r.setPointStyle(styles[k]);
			r.setLineWidth(5);
			r.setFillPoints(true);
			mRenderer.addSeriesRenderer(r);
			k++;

			r = new XYSeriesRenderer();
			r.setColor(colors[k]);
			r.setPointStyle(styles[k]);
			r.setLineWidth(5);
			r.setFillPoints(true);
			mRenderer.addSeriesRenderer(r);
			k++;

			r = new XYSeriesRenderer();
			r.setColor(colors[k]);
			r.setPointStyle(styles[k]);
			r.setLineWidth(5);
			r.setFillPoints(true);
			mRenderer.addSeriesRenderer(r);
			k++;

			r = new XYSeriesRenderer();
			r.setColor(colors[k]);
			r.setPointStyle(styles[k]);
			r.setLineWidth(5);
			r.setFillPoints(true);
			mRenderer.addSeriesRenderer(r);
			k++;

			r = new XYSeriesRenderer();
			r.setColor(colors[k]);
			r.setPointStyle(styles[k]);
			r.setLineWidth(5);
			r.setFillPoints(true);
			mRenderer.addSeriesRenderer(r);
			k++;

			r = new XYSeriesRenderer();
			r.setColor(colors[k]);
			r.setPointStyle(styles[k]);
			r.setLineWidth(5);
			r.setFillPoints(true);
			mRenderer.addSeriesRenderer(r);
		}

		BldPrsrLogger.i(TAG, SubTag + "(Add) mRenderer and mDataset are set");
		GraphicalView mChartView;
		try {
			LinearLayout layout = (LinearLayout) findViewById(R.id.aChart);
			BldPrsrLogger.i(TAG,
					SubTag + "mDataset count: " + mDataset.getSeriesCount());
			BldPrsrLogger.i(
					TAG,
					SubTag + "mRenderer count: "
							+ mRenderer.getSeriesRendererCount());
			mChartView = ChartFactory.getLineChartView(BldPrsrMain.this,
					mDataset, mRenderer);
			layout.removeAllViews();
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
	}

	@Override
	protected void onResume() {
		SubTag = "onResume(): ";
		Log.d(TAG, SubTag + "Start...");

		super.onResume();
		setContentView(R.layout.bldprsmain);

		AdView adView = (AdView) findViewById(R.id.adDataAnalysis);
		adView.loadAd(new AdRequest());

		final Calendar c = Calendar.getInstance();

		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		username = pref.getString(PREF_USERNAME, null);

		this.setTitle("User: " + username);

		/* Get current date */
		final int mYear = c.get(Calendar.YEAR);
		final int mMonth = c.get(Calendar.MONTH) + 1;
		final int mDay = c.get(Calendar.DAY_OF_MONTH);

		String evYearS = String.format("%04d", mYear);
		String evMonthS = String.format("%02d", mMonth);
		String evDayS = String.format("%02d", mDay);
		String dateStr = evYearS + "/" + evMonthS + "/" + evDayS;

		final int mHour = c.get(Calendar.HOUR_OF_DAY);
		final int mMinutes = c.get(Calendar.MINUTE);
		String evHour = String.format("%02d", mHour);
		String evMinutes = String.format("%02d", mMinutes);

		Log.d(TAG, SubTag + "Refreshing the chart....");
		displayCharts();

		dateB = (Button) findViewById(R.id.dateButton);
		dateB.setText(dateStr);
		dateB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BldPrsrLogger.i(TAG, SubTag + "DATE button is clicked");
				new DatePickerDialog(BldPrsrMain.this, mDateSetListener, mYear,
						mMonth - 1, mDay).show();
			}
		});

		addB = (Button) findViewById(R.id.Addprsr);
		addB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				BldPrsrLogger.i(TAG, SubTag + "Adding record to the DB");

				EditText sPr = (EditText) findViewById(R.id.SPressure);
				EditText dPr = (EditText) findViewById(R.id.DPressure);
				EditText pulse = (EditText) findViewById(R.id.pulse);

				// Do a little validation here
				String s = sPr.getText().toString();
				String d = dPr.getText().toString();
				String p = pulse.getText().toString();
				if (s.length() <= 3 && d.length() <= 3 && p.length() <= 3) {
					ContentValues vals = new ContentValues();
					vals.put("name", username);
					vals.put("mDate", dateB.getText().toString());
					vals.put("mTime", "00:00");
					vals.put("dPrsr", dPr.getText().toString());
					vals.put("sPrsr", sPr.getText().toString());
					vals.put("pulse", pulse.getText().toString());

					MyDbHelper db = new MyDbHelper(context);
					db.insert(vals);

					displayCharts();
				} else {
					Toast.makeText(getApplicationContext(),
							"Invalid entries. Please fix it!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		displayCharts();
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			String evYearS = String.format("%04d", year);
			String evMonthS = String.format("%02d", monthOfYear + 1);
			String evDayS = String.format("%02d", dayOfMonth);

			eventDate = evYearS + "/" + evMonthS + "/" + evDayS;
			dateB.setText(eventDate);
		}
	};
}