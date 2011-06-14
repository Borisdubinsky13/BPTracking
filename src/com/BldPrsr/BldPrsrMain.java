package com.BldPrsr;

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
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.backup.BackupManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

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

    BackupManager mBackupManager;
	
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
		Intent iAddUser = new Intent(this, BldPrsrSetupWin.class);

		// Handle item selection
	    switch (item.getItemId()) 
	    {
	    case R.id.AddUser:
	    	BldPrsrLogger.i(TAG, SubTag + "User " + "trying to start AddUser");
	        startActivity(iAddUser);
	        return true;	
	    case R.id.List:
	    	BldPrsrLogger.i(TAG, SubTag + "User " + "trying to start List");
	        startActivity(iList);
	        return true;	
	    case R.id.About:
	    	BldPrsrLogger.i(TAG, SubTag + "User " + "trying to start ABOUT");
	        startActivity(iAbout);
	        return true;
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
        
        mBackupManager = new BackupManager(this);
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
            	vals.put("dPrsr", dPr.getText().toString());
            	vals.put("sPrsr", sPr.getText().toString());
            	vals.put("pulse", pulse.getText().toString());

    			ContentResolver cr = getContentResolver();
    			Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
    			tmpUri = Uri.withAppendedPath(tmpUri,"bpData");
    			cr.insert(tmpUri, vals);            	
                
    			mBackupManager.dataChanged();
    			
                /* Refresh the view that has the chart */
    			XYSeries diastolic = new XYValueSeries("Diastolic");
    			XYSeries systolic = new XYValueSeries("Systolic");
    			XYSeries sPulse = new XYValueSeries("Pulse");

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
    							dValue = Integer.parseInt(value);
    						diastolic.add(cnt, dValue);
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
    							dValue = Integer.parseInt(value);
							if ( dValue > max )
								max = dValue;
							if ( dValue < min )
								min = dValue;
    						systolic.add(cnt, dValue);
    						
    						value = result.getString(result.getColumnIndex("pulse"));
    						BldPrsrLogger.i(TAG, SubTag + "pulse: " + value);
    						if ( value.equals("") )
    							dValue = 0;
    						else
    							dValue = Integer.parseInt(value);
							if ( dValue > max )
								max = dValue;
							if ( dValue < min )
								min = dValue;
    						sPulse.add(cnt, dValue);
    						
    						cnt ++;
 
    					} while (result.moveToNext());
    				}
    			}
    			XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    			mDataset.addSeries(diastolic);
    			mDataset.addSeries(systolic);
    			mDataset.addSeries(sPulse);
    			
    		    int[] colors = new int[] { Color.CYAN, Color.RED, Color.YELLOW };
    		    PointStyle[] styles = new PointStyle[] { PointStyle.SQUARE, PointStyle.DIAMOND, PointStyle.CIRCLE};

    		    XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    		    mRenderer.setAxisTitleTextSize(16);
    		    mRenderer.setChartTitleTextSize(20);
    		    mRenderer.setLabelsTextSize(15);
    		    mRenderer.setLegendTextSize(15);
    		    mRenderer.setPointSize(5f);
    		    mRenderer.setXLabels(0);
    		    mRenderer.setShowGrid(true);
    		    mRenderer.setDisplayChartValues(true);
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
    		    for ( int k = 0; k < length; k++) 
    		    {
    		    	XYSeriesRenderer r = new XYSeriesRenderer();
    		    	r.setColor(colors[k]);
    		    	r.setPointStyle(styles[k]);
    		    	mRenderer.addSeriesRenderer(r);
    		    }
    		    length = mRenderer.getSeriesRendererCount();
    		    for (int k = 0; k < length; k++) 
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
						iValue = Integer.parseInt(value);
					diastolic.add(cnt, iValue);
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
						iValue = Integer.parseInt(value);
					if ( iValue > max )
						max = iValue;
					if ( iValue < min )
						min = iValue;
					systolic.add(cnt, iValue);
					
					value = result.getString(result.getColumnIndex("pulse"));
					BldPrsrLogger.i(TAG, SubTag + "pulse: " + value);
					if ( value.equals("") )
						iValue = 0;
					else
						iValue = Integer.parseInt(value);
					if ( iValue > max )
						max = iValue;
					if ( iValue < min )
						min = iValue;
					sPulse.add(cnt, iValue);
					
					cnt ++;

				} while (result.moveToNext());
			}
		}
		XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
		mDataset.addSeries(diastolic);
		mDataset.addSeries(systolic);
		mDataset.addSeries(sPulse);
		
	    int[] colors = new int[] { Color.CYAN, Color.RED, Color.YELLOW };
	    PointStyle[] styles = new PointStyle[] { PointStyle.SQUARE, PointStyle.DIAMOND, PointStyle.CIRCLE};

	    XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	    mRenderer.setAxisTitleTextSize(16);
	    mRenderer.setChartTitleTextSize(20);
	    mRenderer.setLabelsTextSize(15);
	    mRenderer.setLegendTextSize(15);
	    mRenderer.setPointSize(5f);
	    mRenderer.setXLabels(0);
	    mRenderer.setShowGrid(true);
	    mRenderer.setDisplayChartValues(true);
	    mRenderer.setChartTitle("");
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
	    for ( int k = 0; k < length; k++) 
	    {
	    	XYSeriesRenderer r = new XYSeriesRenderer();
	    	r.setColor(colors[k]);
	    	r.setPointStyle(styles[k]);
	    	mRenderer.addSeriesRenderer(r);
	    }
	    length = mRenderer.getSeriesRendererCount();
	    for (int k = 0; k < length; k++) 
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