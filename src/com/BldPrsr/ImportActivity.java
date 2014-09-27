package com.BldPrsr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ImportActivity extends Activity {

	private Context context;

	public String TAG = "BldPrsr";
	public String SubTag = "ImportActivity: ";
	private static String fnoSDName = "/bldprsr.csv";

	public String username;
	public static final String PREFS_NAME = "BldPrsrFile";
	private static final String PREF_USERNAME = "username";

	private FileInputStream instream = null;
	private ProgressDialog progressDialog;
	private int percentDone;
	private long currentCount = 0;
	private Handler mHandler = new Handler();

	private void actualDoImport(View v) {
		SubTag = "actualDoImport(): ";
		Log.i(TAG, SubTag + "Starting an import");

		// try opening the myfilename.txt
		// open the file for reading
		String fName = Environment.getExternalStorageDirectory() + fnoSDName;
		File f = new File(fName);
		final long fSize = f.length();

		try {
			instream = new FileInputStream(f);
		} catch (Exception e1) {
			Log.e("com.BldPrsr", e1.getMessage());
			Toast.makeText(getApplicationContext(), fName + "is not found",
					Toast.LENGTH_LONG).show();
			return;
		}
		progressDialog = new ProgressDialog(v.getContext());
		progressDialog.setCancelable(true);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgress(0);
		progressDialog.setMessage("Importing "
				+ Environment.getExternalStorageDirectory() + fnoSDName
				+ " ...");
		progressDialog.setMax((int) fSize);
		progressDialog.show();
		Log.i(TAG, SubTag + "Process Dialog has been setup!");

		// final MyDbHelper db = new MyDbHelper(context);

		try {
			new Thread(new Runnable() {
				public void run() {
					int curPercent = 0;

					MyDbHelper db = new MyDbHelper(context);
					SharedPreferences pref = getSharedPreferences(PREFS_NAME,
							MODE_PRIVATE);
					String username = pref.getString(PREF_USERNAME, null);

					String line;
					int lineNumber = 0;
					String delimeter = null;

					// prepare the file for reading
					InputStreamReader inputreader = new InputStreamReader(
							instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					// read every line of the file into the line-variable,
					// on line at the time
					try {
						while ((line = buffreader.readLine()) != null) {
							int startExt;
							int endExt;

							currentCount += line.length();
							// Figure out the separation field. First line
							// only is the heading, so the first word is "Date".
							// extract the 5th character and that should be
							// the separation character
							if (lineNumber == 0) {
								startExt = 4;
								endExt = 5;
								delimeter = line.substring(startExt, endExt);
								lineNumber++;
							} else {
								// parse the line and extract the comma
								// separated fields.
								startExt = 0;
								endExt = line.indexOf(delimeter);
								// String date = line.substring(0, endExt);
								String evYear = line.substring(0, 4);
								String evMonth = line.substring(5, 7);
								String evDay = line.substring(8, 10);
								String evHour = line.substring(11, 13);
								String evMins = line.substring(14, 16);
								line = line.substring(endExt + 1);
								endExt = line.indexOf(delimeter);
								line = line.substring(endExt + 1);
								endExt = line.indexOf(delimeter);
								String syst = line.substring(0, endExt);

								line = line.substring(endExt + 1);
								endExt = line.indexOf(delimeter);
								String diast = line.substring(0, endExt);
								line = line.substring(endExt + 1);
								endExt = line.indexOf(delimeter);
								String pulse;
								if (endExt > 0)
									pulse = line.substring(0, endExt);
								else
									pulse = line;
								/*
								 * line = line.substring(endExt+1); String name
								 * = line; Log.i(TAG, SubTag +
								 * "ActualDoImport(): Got name: " + name);
								 */
								ContentValues vals = new ContentValues();
								vals.put("name", username);
								vals.put("mDate", evYear + "-" + evMonth + "-"
										+ evDay);
								vals.put("mTime", evHour + ":" + evMins);

								// Systolic value is always lower then diastolic
								Integer s = Integer.parseInt(syst);
								Integer d = Integer.parseInt(diast);
								if (s > d) {
									int t = d;
									d = s;
									s = t;
								}
								vals.put("sPrsr", s.toString());
								vals.put("dPrsr", d.toString());

								vals.put("pulse", pulse);
								vals.put("mDay", evDay);
								vals.put("mMonth", evMonth);
								vals.put("mYear", evYear);

								db.insert(vals);

								Log.i(TAG, SubTag + "Inserting a record");

								lineNumber++;

								// Update the progress bar if needed
								percentDone = (int) (currentCount * 100 / fSize);
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
						}
						instream.close();
						progressDialog.dismiss();
						Log.i(TAG, SubTag + "About to finish the Activity");
						finish();

					} catch (Exception e1) {
						Log.e("com.BldPrsr", e1.getMessage());
					}
				}
			}).start();
		} catch (Exception e1) {
			Log.e("com.BldPrsr", e1.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SubTag = "onCreate(): ";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.importdb);
		context = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		SubTag = "onResume(): ";
		super.onResume();
		setContentView(R.layout.importdb);

		Button nButton = (Button) findViewById(R.id.NoButton);
		nButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, SubTag + "Keep current database");
				/* Perform actual import */
				actualDoImport(v);
			}
		});

		Button yButton = (Button) findViewById(R.id.YesButton);
		yButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.i(TAG, SubTag + "Removing the database");
				MyDbHelper db = new MyDbHelper(context);
				db.deleteTbl();
				/* Perform actual import */
				Log.i(TAG, SubTag + "Starting an import");
				actualDoImport(v);
			}
		});
		return;
	}

}
