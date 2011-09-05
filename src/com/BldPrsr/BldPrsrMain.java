package com.BldPrsr;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

public class BldPrsrMain extends Activity 
{
    /* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	/** Called when the activity is first created. */
	public static final String PREFS_NAME = "BldPrsrFile";
	private static final String PREF_USERNAME = "username";	
	
	public static String TAG="BldPrsr";
	public static String SubTag="BldPrsrMain: ";
	String[] projection = new String[] {
			"_id",
			"name",
			"mDate",
			"dPrsr",
			"sPrsr",
			"pulse"
	};
	
	String eventDate;
	Button dateB;
	Button addB;
	String username;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mainmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent iAbout = new Intent(this, AboutHandler.class);
		Intent iList = new Intent(this, BldPrsrList.class);
		// Intent iAddUser = new Intent(this, BldPrsrSetupWin.class);

		// Handle item selection
	    switch (item.getItemId()) 
	    {
/*
	    case R.id.AddUser:
	    	BldPrsrLogger.i(TAG, SubTag + "User " + "trying to start AddUser");
	        startActivity(iAddUser);
	        return true;
*/
	    case R.id.List:
	    	BldPrsrLogger.i(TAG, SubTag + "User " + "trying to start List");
	        startActivity(iList);
	        return true;	
	    case R.id.About:
	    	BldPrsrLogger.i(TAG, SubTag + "User " + "trying to start ABOUT");
	        startActivity(iAbout);
	        return true;
/*
	    case R.id.Logout:
	    	BldPrsrLogger.i(TAG, SubTag + "User " + "trying to Logout");
   			Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
			tmpUri = Uri.withAppendedPath(tmpUri,"bpStatus");

        	String	query = "name = '" + username+ "'";
        	ContentResolver cr = getContentResolver();
        	cr.delete(tmpUri, query, null);
        	
        	// store user id in Preferences for everybody to access.
            getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
        	.edit()
        	.putString(PREF_USERNAME, "")
        	.commit();
        	// Intent iLogin = new Intent(this,loginHandler.class);
            BldPrsrLogger.i(TAG, SubTag + "trying to logout");
	        finish();
	        return true;
*/	     
	    case R.id.importDB:
	    	BldPrsrLogger.i(TAG, SubTag + "trying to import data");
	    	Intent importAct = new Intent(this, ImportActivity.class);
	    	startActivity(importAct);
	    	return true;
	    case R.id.Export:
	    	try
	    	{
	    		String fnoSDName = "/bldprsr.csv";
	    		String	fname = Environment.getExternalStorageDirectory()+fnoSDName;
	    		BldPrsrLogger.i(TAG, SubTag + "User " + "Trying to export data to CSV. File: " + fname);
		    	File myFile = new File( fname );
				myFile.createNewFile();
				FileOutputStream fOut =  new FileOutputStream(myFile);
				BufferedOutputStream bos = new BufferedOutputStream( fOut );
				
	   			Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
				tmpUri = Uri.withAppendedPath(tmpUri,"bpData");
				
				String query = "name = '" + username + "'";
				BldPrsrLogger.i(TAG, SubTag + "Query: " + query);
				Cursor result = managedQuery(tmpUri, projection, query, null, null);
    			BldPrsrLogger.i(TAG, SubTag + "there are " + result.getCount() + " records" );
    			if ( result.getCount() > 0 )
    			{
    				if ( result.moveToFirst() )
    				{
    					String	strOut = "Date,Time,Systolic,Diastolic,Pulse,Name\n";	
    					bos.write(strOut.getBytes());

    					do
    					{
    						String	nameStr = result.getString(result.getColumnIndex("name"));
    						String	mDateStr = result.getString(result.getColumnIndex("mDate"));
    						String	mTimeStr = result.getString(result.getColumnIndex("mTime"));
    						String	sPrsrStr = String.format("%03d",Integer.parseInt(result.getString(result.getColumnIndex("sPrsr"))));
    						String	dPrsrStr = String.format("%03d",Integer.parseInt(result.getString(result.getColumnIndex("dPrsr"))));
    						String	pulse = String.format("%03d",Integer.parseInt(result.getString(result.getColumnIndex("pulse"))));
    						
    						strOut = mDateStr + "," + mTimeStr + "," + sPrsrStr + "," + dPrsrStr + "," + pulse + "," + nameStr  + "\n";	
    						bos.write(strOut.getBytes());
    					} while (result.moveToNext());
    				}
    				result.close();
    				bos.close();

    				int duration = Toast.LENGTH_LONG;
        			String text = "Data has been exported into " + fname;
        			Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        			toast.show();
    			}
    			else
    			{
    				int duration = Toast.LENGTH_SHORT;
        			String text = "No data to export";
        			Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        			toast.show();
    			}
            
	    	}
	    	catch (Exception e)
	    	{
	    		BldPrsrLogger.e(TAG, SubTag + e.getMessage());
	    	}
	    	
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }

	}

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bldprsmain);
        
    	String androidId = Settings.Secure.getString 
		(this.getContentResolver(), 
	 	android.provider.Settings.Secure.ANDROID_ID);
    	if ( androidId == null || androidId.equals("9774d56d682e549c"))
		{
			// We are running on the emulator. Debugging should be ON.
			BldPrsrLogger.e(TAG, SubTag + "Enabeling VERBOSE debugging. androidID = " + androidId);
			BldPrsrLogger.enableLogging(Log.VERBOSE);
		}
		else
		{
			// We are running on a phone. Debugging should be OFF.
			BldPrsrLogger.e(TAG, SubTag + "Enabeling ERRORS only debugging. androidID = " + androidId);
			BldPrsrLogger.enableLogging(Log.ERROR);
		}
		 
		BldPrsrLogger.i(TAG, SubTag + " Enter onCreate() ");
	
		// First check if there are any users. If not, then switch to setup window to add first user.
		try
		{
	        Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
	        BldPrsrLogger.i(TAG, SubTag + "Got account list. Number of entries: " + accounts.length);
	        if ( accounts.length <= 0 )
	        {
	        	int duration = Toast.LENGTH_SHORT;
				String text = "Primary account is not setup. Please setup google account first.";
				Toast toast = Toast.makeText(getApplicationContext(), text, duration);
				toast.show();
	        }
	        else
	        {
		        String username = accounts[0].name; 
		        BldPrsrLogger.i(TAG, SubTag + "My email id that i want: " + username); 
		 
		        getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
		    	.edit()
		    	.putString(PREF_USERNAME, username)
		    	.commit();
		        
		        // update database to make sure that all entries have username = primary google account.
		        // See if there are any data that has name other then current username
		        
	   			Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
    			tmpUri = Uri.withAppendedPath(tmpUri,"bpData");
    			
    			// Update the table with the new id
            	ContentValues vals = new ContentValues();
            	ContentResolver cr = getContentResolver();
            	vals.put("name", username);
            	cr.update(tmpUri, vals, "name != '" + username + "'", null);
	        }
		}
		catch (Exception e)
		{
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
    }
    
	@Override
	protected void onResume() 
	{
		super.onResume();
        setContentView(R.layout.bldprsmain);
        
		AdView	adView = (AdView)findViewById(R.id.adDataAnalysis);
	    adView.loadAd(new AdRequest());
	    
	    final Calendar c = Calendar.getInstance();
	    
    	SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);   
    	username = pref.getString(PREF_USERNAME, null);

    	this.setTitle("User: " + username);

	    /* Get current date */
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH) + 1;
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        
		String evYearS = String.format("%04d",mYear);
		String evMonthS = String.format("%02d", mMonth);
		String evDayS = String.format("%02d",mDay);
		String dateStr = evYearS + "/" + evMonthS + "/" + evDayS;
		
        final int mHour = c.get(Calendar.HOUR_OF_DAY);
        final int mMinutes = c.get(Calendar.MINUTE);
		String evHour = String.format("%02d", mHour);
		String evMinutes = String.format("%02d", mMinutes);
		final String timeStr = evHour + ":" + evMinutes;
		
        dateB = (Button)findViewById(R.id.dateButton);
    	dateB.setText( dateStr );
    	dateB.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) 
            {
            	BldPrsrLogger.i(TAG, SubTag + "DATE button is clicked");
            	new DatePickerDialog(BldPrsrMain.this, mDateSetListener, mYear, mMonth-1, mDay).show();
            }
        });
    	
    	addB = (Button) findViewById(R.id.Addprsr);
        addB.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) 
            {
            	ContentValues vals = new ContentValues();
            	
            	BldPrsrLogger.i(TAG, SubTag + "Adding record to the DB");
            	
        	    EditText sPr = (EditText)findViewById(R.id.SPressure);
        	    EditText dPr = (EditText)findViewById(R.id.DPressure);
        	    EditText pulse = (EditText)findViewById(R.id.pulse);
        	    
            	vals.put("name", username);
            	vals.put("mDate", dateB.getText().toString());
            	vals.put("mTime",  timeStr);
            	vals.put("dPrsr", dPr.getText().toString());
            	vals.put("sPrsr", sPr.getText().toString());
            	vals.put("pulse", pulse.getText().toString());

    			ContentResolver cr = getContentResolver();
    			Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
    			tmpUri = Uri.withAppendedPath(tmpUri,"bpData");
    			cr.insert(tmpUri, vals);            	
                
                /* Refresh the view that has the chart */
    			XYSeries diastolic = new XYValueSeries("Diastolic");
    			XYSeries systolic = new XYValueSeries("Systolic");
    			XYSeries sPulse = new XYValueSeries("Pulse");
    			
    			XYSeries diasDef = new XYValueSeries("Base Diastolic (80)");
    			XYSeries systDef = new XYValueSeries("Base Systolic (120)");
    			XYSeries pulsDef = new XYValueSeries("Base Pulse (65)");

    			/* Create the series for the trend line */
    			BldPrsrTrendline diasTrend = new BldPrsrTrendline("Dias. Trend");
    			BldPrsrTrendline systTrend = new BldPrsrTrendline("Syst. Trend");
    			BldPrsrTrendline pulseTrend = new BldPrsrTrendline("Pulse Trend");
     			
    			String	query = null;
				int  cnt = 0;
				int	min=0,max=0;
				
    			query = "name = '" + username + "'";
    			BldPrsrLogger.i(TAG, SubTag + "Query: " + query);
    			Cursor result = managedQuery(tmpUri, projection, query, null, null);
    			BldPrsrLogger.i(TAG, SubTag + "there are " + result.getCount() + " records" );
    			if ( result.getCount() > 0 )
    			{
    				if ( result.moveToFirst() )
    				{
    					BldPrsrLogger.i(TAG, SubTag + "got result back from provider");
    					String value;
    					do
    					{
    						int dValue;
    			
    						value = result.getString(result.getColumnIndex("dPrsr"));
    						BldPrsrLogger.i(TAG, SubTag + "dPrsr: " + value);
    						if ( value.equals("") )
    							dValue = 0;
    						else
    						{
    							try
    							{
    								dValue = Integer.parseInt(value);
    							}
    							catch (Exception e)
    							{
    								BldPrsrLogger.e(TAG, SubTag + e.getMessage());
    								dValue = 0;
    							}
    						}
    						diastolic.add(cnt, dValue);
    						diasDef.add(cnt, 80);
    						diasTrend.addXY(cnt, dValue);
    						if ( cnt == 0 )
    							min = max = dValue;
    						else
    						{
    							if ( dValue > max )
    								max = dValue;
    							if ( dValue < min )
    								min = dValue;
    						}
    						
    						value = result.getString(result.getColumnIndex("sPrsr"));
    						BldPrsrLogger.i(TAG, SubTag + "sPrsr: " + value);
    						if ( value.equals("") )
    							dValue = 0;
    						else
    						{
    							try
    							{
    								dValue = Integer.parseInt(value);
    							}
	  							catch (Exception e)
								{
									BldPrsrLogger.e(TAG, SubTag + e.getMessage());
									dValue = 0;
								}
    						}
    						
							if ( dValue > max )
								max = dValue;
							if ( dValue < min )
								min = dValue;
    						systolic.add(cnt, dValue);
    						systDef.add(cnt, 120);
    						systTrend.addXY(cnt, dValue);
    						
    						value = result.getString(result.getColumnIndex("pulse"));
    						BldPrsrLogger.i(TAG, SubTag + "pulse: " + value);
    						if ( value.equals("") )
    							dValue = 0;
    						else
    						{
    							try
    							{
    								dValue = Integer.parseInt(value);
    							}
	  							catch (Exception e)
								{
									BldPrsrLogger.e(TAG, SubTag + e.getMessage());
									dValue = 0;
								}
    						}
							if ( dValue > max )
								max = dValue;
							if ( dValue < min )
								min = dValue;
    						sPulse.add(cnt, dValue);
    						pulseTrend.addXY(cnt, dValue);
    						cnt ++;
 
    					} while (result.moveToNext());
    				}
    			}
    			XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    			mDataset.addSeries(diastolic);
    			mDataset.addSeries(systolic);
    			mDataset.addSeries(sPulse);
    			
    			mDataset.addSeries(diasTrend.getTheTrend());
    			mDataset.addSeries(systTrend.getTheTrend());
    			mDataset.addSeries(pulseTrend.getTheTrend());
   			
    			int[] colors = new int[] { Color.CYAN, Color.RED, Color.YELLOW, Color.CYAN, Color.RED, Color.YELLOW };
    		    PointStyle[] styles = new PointStyle[] { PointStyle.SQUARE, PointStyle.DIAMOND, PointStyle.CIRCLE };

    		    XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
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
    		    mRenderer.setXAxisMax(cnt);
    		    mRenderer.setYAxisMin(min-5);
    		    mRenderer.setYAxisMax(max);
    		    mRenderer.setAxesColor(Color.WHITE);
    		    mRenderer.setLabelsColor(Color.WHITE);
    		    
    		    // mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
    		    int length = colors.length;

    		    int	k;
    		    for ( k = 0; k < 3; k++) 
    		    {
    		    	XYSeriesRenderer r = new XYSeriesRenderer();
    		    	r.setColor(colors[k]);
    	    		r.setPointStyle(styles[k]);
    	    		r.setLineWidth(5);
    		    	mRenderer.addSeriesRenderer(r);
    		    }
    		    
    		    for ( ; k < length; k++) 
    		    {
    		    	XYSeriesRenderer r = new XYSeriesRenderer();
    		    	r.setColor(colors[k]);
    		    	r.setLineWidth(1);
    		    	mRenderer.addSeriesRenderer(r);
    		    }
    		    length = mRenderer.getSeriesRendererCount();
    		    for (k = 0; k < length; k++) 
    		    {
    		      ((XYSeriesRenderer) mRenderer.getSeriesRendererAt(k)).setFillPoints(true);
    		    }
    		    BldPrsrLogger.i(TAG, SubTag + "(Add) mRenderer and mDataset are set");
    		    GraphicalView mChartView;
				try 
				{
					LinearLayout layout = (LinearLayout) findViewById(R.id.aChart);
					BldPrsrLogger.i(TAG, SubTag + "mDataset count: " + mDataset.getSeriesCount());
					mChartView = ChartFactory.getLineChartView(BldPrsrMain.this, mDataset, mRenderer);
					layout.removeAllViews();
					layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				}
				catch (Exception e)
				{
					BldPrsrLogger.e(TAG, SubTag + e.getMessage());
				}
            }
        });
        /* Refresh the view that has the chart */
		XYSeries diastolic = new XYValueSeries("Diastolic");
		XYSeries systolic = new XYValueSeries("Systolic");
		XYSeries sPulse = new XYValueSeries("Pulse");

		BldPrsrTrendline diasTrend = new BldPrsrTrendline("Dias. Trend");
		BldPrsrTrendline systTrend = new BldPrsrTrendline("Syst. Trend");
		BldPrsrTrendline pulseTrend = new BldPrsrTrendline("Pulse Trend");
		
		String	query = null;
		int  cnt = 0;
		int	min=0,max=0;
		
		query = "name = '" + username + "'";
		BldPrsrLogger.i(TAG, SubTag + "Query: " + query);
		Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
		tmpUri = Uri.withAppendedPath(tmpUri,"bpData");

		Cursor result = managedQuery(tmpUri, projection, query, null, null);
		BldPrsrLogger.i(TAG, SubTag + "there are " + result.getCount() + " records" );
		if ( result.getCount() > 0 )
		{
			if ( result.moveToFirst() )
			{
				BldPrsrLogger.i(TAG, SubTag + "got result back from provider");
				String value;
				do
				{
					int iValue;
		
					value = result.getString(result.getColumnIndex("dPrsr"));
					BldPrsrLogger.i(TAG, SubTag + "dPrsr: " + value);
					if ( value.equals("") )
						iValue = 0;
					else
					{
						try
						{
							iValue = Integer.parseInt(value);
						}
						catch (Exception e)
						{
							BldPrsrLogger.e(TAG, SubTag + e.getMessage());
							iValue = 0;
						}
					}
					diastolic.add(cnt, iValue);
					diasTrend.addXY(cnt,iValue);
					if ( cnt == 0 )
						min = max = iValue;
					else
					{
						if ( iValue > max )
							max = iValue;
						if ( iValue < min )
							min = iValue;
					}
					
					value = result.getString(result.getColumnIndex("sPrsr"));
					BldPrsrLogger.i(TAG, SubTag + "sPrsr: " + value);
					if ( value.equals("") )
						iValue = 0;
					else
					{
						try
						{
							iValue = Integer.parseInt(value);
						}
						catch (Exception e)
						{
							BldPrsrLogger.e(TAG, SubTag + e.getMessage());
							iValue = 0;
						}
					}
					if ( iValue > max )
						max = iValue;
					if ( iValue < min )
						min = iValue;
					systolic.add(cnt, iValue);
					systTrend.addXY(cnt,iValue);
					
					value = result.getString(result.getColumnIndex("pulse"));
					BldPrsrLogger.i(TAG, SubTag + "pulse: " + value);
					if ( value.equals("") )
						iValue = 0;
					else
					{
						try
						{
							iValue = Integer.parseInt(value);
						}
						catch (Exception e)
						{
							BldPrsrLogger.e(TAG, SubTag + e.getMessage());
							iValue = 0;
						}

					}
					if ( iValue > max )
						max = iValue;
					if ( iValue < min )
						min = iValue;
					sPulse.add(cnt, iValue);
					pulseTrend.addXY(cnt,iValue);
					cnt ++;

				} while (result.moveToNext());
			}
		}
		XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
		mDataset.addSeries(diastolic);
		mDataset.addSeries(systolic);
		mDataset.addSeries(sPulse);

		mDataset.addSeries(diasTrend.getTheTrend());
		mDataset.addSeries(systTrend.getTheTrend());
		mDataset.addSeries(pulseTrend.getTheTrend());
		
	    int[] colors = new int[] { Color.CYAN, Color.RED, Color.YELLOW, Color.CYAN, Color.RED, Color.YELLOW };
	    PointStyle[] styles = new PointStyle[] { PointStyle.SQUARE, PointStyle.DIAMOND, PointStyle.CIRCLE };

	    XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	    mRenderer.setAxisTitleTextSize(16);
	    mRenderer.setChartTitleTextSize(20);
	    mRenderer.setLabelsTextSize(15);
	    mRenderer.setLegendTextSize(15);
	    mRenderer.setPointSize(5f);
	    mRenderer.setXLabels(0);
	    mRenderer.setShowGrid(true);
	    mRenderer.setDisplayChartValues(false);
	    mRenderer.setChartTitle("Blood Pressure/Pulse");
	    mRenderer.setXTitle("Date");
	    mRenderer.setYTitle("");
	    mRenderer.setXAxisMin(0);
	    mRenderer.setXAxisMax(cnt);
	    mRenderer.setYAxisMin(min - 5);
	    mRenderer.setYAxisMax(max);
	    mRenderer.setAxesColor(Color.WHITE);
	    mRenderer.setLabelsColor(Color.WHITE);
	    
	    // mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
	    int length = colors.length;
	    int	k;
	    for ( k = 0; k < 3; k++) 
	    {
	    	XYSeriesRenderer r = new XYSeriesRenderer();
	    	r.setColor(colors[k]);
    		r.setPointStyle(styles[k]);
    		r.setLineWidth(5);
	    	mRenderer.addSeriesRenderer(r);
	    }
	    
	    for ( ; k < length; k++) 
	    {
	    	XYSeriesRenderer r = new XYSeriesRenderer();
	    	r.setColor(colors[k]);
	    	r.setLineWidth(1);
	    	mRenderer.addSeriesRenderer(r);
	    }
	    	    
	    length = mRenderer.getSeriesRendererCount();
	    for (k = 0; k < length; k++) 
	    {
	      ((XYSeriesRenderer) mRenderer.getSeriesRendererAt(k)).setFillPoints(true);
	    }
	    BldPrsrLogger.i(TAG, SubTag + "mRenderer and mDataset are set");
	    GraphicalView mChartView;
		try 
		{
			LinearLayout layout = (LinearLayout) findViewById(R.id.aChart);
			BldPrsrLogger.i(TAG, SubTag + "mDataset count: " + mDataset.getSeriesCount());
			mChartView = ChartFactory.getLineChartView(BldPrsrMain.this, mDataset, mRenderer);
			layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}
		catch (Exception e)
		{
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
    }
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() 
    {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		{
			String evYearS = String.format("%04d", year);
			String evMonthS = String.format("%02d", monthOfYear+1);
			String evDayS =  String.format("%02d", dayOfMonth);

			eventDate = evYearS + "/" + evMonthS + "/" + evDayS;
			dateB.setText(eventDate);
		}
    };    	
}