package com.BldPrsr;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class BldPrsrList extends Activity {

	/**
	 * 
	 */
	private static Context context;
	private static String TAG = "BldPrsrList";
	private static String SubTag;

	public static final String PREFS_NAME = "BldPrsrFile";
	private static final String PREF_USERNAME = "username";

	// private static TableRow tblRow = null;

	// private static final String PREF_ID = "dataTBL_ID";

	// private Cursor result;

	// public SimpleCursorAdapter items;

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

		// registerForContextMenu(getListView());

		AdView adView = (AdView) findViewById(R.id.adListRes);
		// Initiate a generic request to load it with an ad
		adView.loadAd(new AdRequest());

		BldPrsrLogger.i(TAG, SubTag + "ListRes()");
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String username = pref.getString(PREF_USERNAME, null);
		this.setTitle("User: " + username);

		MyDbHelper db = new MyDbHelper(this);
		List<BldPrsrBasicData> lstData = db.getAllData();
		TableLayout tbl = (TableLayout) findViewById(R.id.tblList);
		/*
		 * TableRow tblRow = (TableRow) findViewById(R.id.tblRow);
		 * 
		 * TextView dateF = (TextView) findViewById(R.id.dtField); TextView
		 * sysField = (TextView) findViewById(R.id.sysField); TextView diaField
		 * = (TextView) findViewById(R.id.diaField); TextView plsField =
		 * (TextView) findViewById(R.id.plsField);
		 */
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
