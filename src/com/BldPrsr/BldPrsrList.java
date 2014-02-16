package com.BldPrsr;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class BldPrsrList extends Activity {

	/**
	 * 
	 */
	private static Context context;
	private static String TAG = "BldPrsrList";
	private static String SubTag;

	public static final String PREFS_NAME = "BldPrsrFile";
	private static final String PREF_USERNAME = "username";
	private static String username;
	private String	txtReport;
	
	private String	startDate;
	private String	endDate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
	}

	@Override
	protected void onResume() {

		super.onResume();
		setContentView(R.layout.list);
		SubTag = "onResume(): ";

		List<BldPrsrBasicData> lstData = null;
		String sql = null;

		AdView adView = (AdView) findViewById(R.id.adDataAnalysis);
	    AdRequest adRequest = new AdRequest.Builder()
			.addTestDevice("1C9D5807CADB9259EB3804DDC582DC3C")
			.addTestDevice("5AECA86F6A4E6EB1C1B6907DDFB5086D")
			.build();
	    // Load the adView with the ad request.
	    adView.loadAd(adRequest);
		final SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		Button Snd2Doc = (Button) findViewById(R.id.Snd2Doc);
		Snd2Doc.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String docEmail = mySharedPreferences.getString("DocEmail", "");
				// Send email to the doctor
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL, new String[]{docEmail});
				i.putExtra(Intent.EXTRA_SUBJECT, "Blood pressure measurements (" + startDate +
						"-" + endDate + ")");
				i.putExtra(Intent.EXTRA_TEXT, txtReport);
				try {
				    startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		Button cancelB = (Button) findViewById(R.id.cncl);
		cancelB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		BldPrsrLogger.i(TAG, SubTag + "ListRes()");
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		username = pref.getString(PREF_USERNAME, null);

		startDate = pref.getString("startdate", "");
		endDate = pref.getString("enddate", "");
		this.setTitle("User: " + username);

		MyDbHelper db = new MyDbHelper(this);
		if (startDate.equals("") && endDate.equals("")) {
			lstData = db.getAllData();
		} else {
			if (!startDate.equals("")) {
				sql = "WHERE mDate > \"" + startDate + "\"";
			}
			if (!endDate.equals("") && sql.equals("")) {
				sql = "WHERE mDate < \"" + endDate + "\"";
			}
			if (!endDate.equals("") && !sql.equals("")) {
				sql += " AND mDate < \"" + endDate + "\"";
			}
			sql += ";";
			lstData = db.getData(sql);
		}

		txtReport = "";
		TableLayout tbl = (TableLayout) findViewById(R.id.tblList);
		for (BldPrsrBasicData bd : lstData) {
			final TableRow tblRow = new TableRow(this);

			TextView dateF = new TextView(this);
			TextView sysField = new TextView(this);
			TextView diaField = new TextView(this);
			TextView plsField = new TextView(this);
			TextView dbId = new TextView(this);
			ImageView delImage = new ImageView(this);

			dbId.setText(bd.getId());
			dbId.setVisibility(0);
			dateF.setText(bd.getmDate());
			dateF.setPadding(0, 0, 60, 0);
			sysField.setText(bd.getsPrsr());
			sysField.setPadding(0, 0, 70, 0);
			diaField.setText(bd.getdPrsr());
			diaField.setPadding(0, 0, 70, 0);
			plsField.setText(bd.getPulse());
			plsField.setPadding(0, 0, 70, 0);

			txtReport += (bd.getmDate() + ": Pressure: (" +
							bd.getsPrsr() + "/" +
							bd.getdPrsr() + "), Pulse: " +
							bd.getPulse() + "\n");
			tblRow.addView(dateF);
			tblRow.addView(sysField);
			tblRow.addView(diaField);
			tblRow.addView(plsField);
			delImage.setImageResource(android.R.drawable.ic_delete);
			tblRow.setId(Integer.parseInt(bd.getId()));
			tblRow.addView(delImage);
			tblRow.setClickable(true);

			tblRow.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					Log.d(TAG,
							"setOnClickListener(): Trying to delete record with ID: "
									+ tblRow.getId());
					MyDbHelper tmpDb = new MyDbHelper(context);
					tmpDb.deleteRecord(tblRow.getId());
					finish();
				}
			});
			tbl.addView(tblRow);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView,
	 * android.view.View, int, long)
	 */
	/*
	 * @Override protected void onListItemClick(ListView l, View v, int
	 * position, long id) { // TODO Auto-generated method stub
	 * super.onListItemClick(l, v, position, id);
	 * 
	 * // TODO Auto-generated method stub BldPrsrLogger.i(TAG, SubTag +
	 * "Clicked on position: " + position + ", id: " + id );
	 * super.onListItemClick(l, v, position, id);
	 * 
	 * // Save the entry in the preferences, so the display activity can display
	 * an appropriate record getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
	 * .edit() .putString(PREF_ID, id + "") .commit();
	 * 
	 * Intent iDataEntry = new Intent(this, BldPrsrHandleDetailEntry.class);
	 * 
	 * startActivity(iDataEntry); }
	 */
	/*
	 * @Override public void onCreateContextMenu(ContextMenu menu, View v,
	 * ContextMenuInfo menuInfo) { super.onCreateContextMenu(menu, v, menuInfo);
	 * MenuInflater inflater = getMenuInflater(); BldPrsrLogger.i(TAG, SubTag +
	 * "Creating a context menu"); menu.setHeaderTitle("Menu:");
	 * inflater.inflate(R.menu.detailentrymenu, menu); }
	 * 
	 * public boolean onContextItemSelected(MenuItem item) {
	 * AdapterView.AdapterContextMenuInfo menuInfo =
	 * (AdapterView.AdapterContextMenuInfo) item .getMenuInfo(); switch
	 * (item.getItemId()) { case R.id.delSelection: AdapterContextMenuInfo info
	 * = (AdapterContextMenuInfo) item .getMenuInfo();
	 * 
	 * BldPrsrLogger.i(TAG, SubTag + "Clicked on position: " + info.id);
	 * BldPrsrLogger.i(TAG, SubTag + "list pos:" + menuInfo.position + " id:" +
	 * menuInfo.id); ContentResolver cr = getContentResolver(); String query =
	 * "_ID = '" + menuInfo.id + "'"; Uri tmpUri = Uri
	 * .parse("content://com.BldPrsr.provider.userContentProvider");
	 * 
	 * tmpUri = Uri.withAppendedPath(tmpUri, "bpData"); cr.delete(tmpUri, query,
	 * null); result.requery(); return true; } return
	 * super.onContextItemSelected(item); }
	 */
}
