/**
 * 
 */
package com.BldPrsr;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

/**
 * @author boris
 * 
 */
public class BldPrsrFilterScreen extends Activity {

	private Button reportB;
	private Button StartDateB;
	private Button EndDateB;
	protected String evYearS;
	protected String evMonthS;
	protected String evDayS;
	String startSearchDate = null;
	String endSearchDate = null;
	final Calendar c = Calendar.getInstance();
	private boolean sDatePreset;
	private boolean eDatePreset;

	public static final String PREFS_NAME = "BldPrsrFile";

	/**
	 * 
	 */
	public BldPrsrFilterScreen() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.filterscreen);
		sDatePreset = false;
		eDatePreset = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {

		final int mYearS = c.get(Calendar.YEAR);
		final int mMonthS = c.get(Calendar.MONTH) + 1;
		final int mDayS = c.get(Calendar.DAY_OF_MONTH);
		final int mYearE = c.get(Calendar.YEAR);
		final int mMonthE = c.get(Calendar.MONTH) + 1;
		final int mDayE = c.get(Calendar.DAY_OF_MONTH);
		startSearchDate = null;
		sDatePreset = false;
		endSearchDate = null;
		eDatePreset = false;
		
		super.onResume();
		setContentView(R.layout.filterscreen);

		StartDateB = (Button) findViewById(R.id.startDateButton);
		StartDateB.setText("Start Date");
		StartDateB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				new DatePickerDialog(BldPrsrFilterScreen.this,
						mStartDateSetListener, mYearS, mMonthS - 1, mDayS)
						.show();
				sDatePreset = true;
			}
		});

		EndDateB = (Button) findViewById(R.id.endDateButton);
		EndDateB.setText("End Date");
		EndDateB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new DatePickerDialog(BldPrsrFilterScreen.this,
						mEndDateSetListener, mYearE, mMonthE - 1, mDayE).show();
				eDatePreset = true;
			}
		});

		reportB = (Button) findViewById(R.id.report);
		reportB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (sDatePreset == false)
					startSearchDate = "";
				if (eDatePreset == false) 
					endSearchDate = "";
				
				getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
							.putString("startdate", startSearchDate).commit();
				getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
							.putString("enddate", endSearchDate).commit();

				Intent iViewRes = new Intent(BldPrsrFilterScreen.this,
						BldPrsrList.class);
				startActivity(iViewRes);
			}
		});
	}

	private DatePickerDialog.OnDateSetListener mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			evYearS = String.format("%04d", year);
			evMonthS = String.format("%02d", monthOfYear + 1);
			evDayS = String.format("%02d", dayOfMonth);
			String dateStr = evYearS + "/" + evMonthS + "/" + evDayS;
			StartDateB.setText(dateStr);
			startSearchDate = evYearS + "/" + evMonthS + "/" + evDayS;
		}
	};
	private DatePickerDialog.OnDateSetListener mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			evYearS = String.format("%04d", year);
			evMonthS = String.format("%02d", monthOfYear + 1);
			evDayS = String.format("%02d", dayOfMonth);
			String dateStr = evYearS + "/" + evMonthS + "/" + evDayS;
			EndDateB.setText(dateStr);
			endSearchDate = evYearS + "/" + evMonthS + "/" + evDayS;

		}
	};
}
