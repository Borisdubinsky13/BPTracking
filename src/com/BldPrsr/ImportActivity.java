package com.BldPrsr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

public class ImportActivity extends Activity 
{
	public String TAG="BldPrsr";
	public String SubTag="ImportActivity: ";
	private static String	fnoSDName = "/bldprsr.csv";
	
	public String username;
	public static final String PREFS_NAME = "BldPrsrFile";
	private static final String PREF_USERNAME = "username";
	private void actualDoImport()
	{
		// try opening the myfilename.txt
		BldPrsrLogger.i(TAG, SubTag + "Starting an import");
		try 
		{
			Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
			tmpUri = Uri.withAppendedPath(tmpUri,"bpData");
			ContentResolver cr = getContentResolver();
			BldPrsrLogger.i(TAG, SubTag + "Got URI populated");
			
			// open the file for reading
			File f = new File(Environment.getExternalStorageDirectory()+fnoSDName);
			FileInputStream instream = new FileInputStream(f);
			BldPrsrLogger.i(TAG, SubTag + "File: " + Environment.getExternalStorageDirectory()+fnoSDName);
		    // if file the available for reading
		    if (instream != null) 
		    {
		    	// prepare the file for reading
		    	InputStreamReader inputreader = new InputStreamReader(instream);
		    	BufferedReader buffreader = new BufferedReader(inputreader);
		    	BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): bufferreader has been setup");
		    	SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
		    	String username = pref.getString(PREF_USERNAME, null);

		    	String line;
		    	int	lineNumber = 0;
		    	String	delimeter = null;
		    	
		    	// read every line of the file into the line-variable, on line at the time
		    	while (( line = buffreader.readLine()) != null) 
		    	{
		    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): Reading line #" + lineNumber);
		    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): line: " + line);
		    		int	startExt;
		    		int endExt;
		    		
		    		// Figure out the separation field. First line only is the heading, so the first word is "Date". 
		    		// extract the 5th character and that should be the separation character
		    		if ( lineNumber == 0 )
		    		{
		    			startExt = 4;
		    			endExt = 5;
		    			delimeter = line.substring(startExt, endExt);
		    			lineNumber++;
		    		}
		    		else
		    		{
			    		// parse the line and extract the comma separated fields.
			    		startExt = 0;
			    		endExt = line.indexOf(delimeter);
			    		String	date = line.substring(0, endExt);
			    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): got date: " + date);
			    		String	evYear = line.substring(0,4);
			    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): Got year: " + evYear);
			    		String	evMonth = line.substring(5,7);
			    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): Got month: " + evMonth);
			    		String	evDay = line.substring(8,10);
			    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): Got day: " + evDay);
			    		String	evHour = line.substring(11,13);
			    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): Got hour: " + evHour);
			    		String	evMins = line.substring(14,16);
			    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): Got minutes: " + evMins);
			    		line = line.substring(endExt+1);
			    		endExt = line.indexOf(delimeter);
			    		line = line.substring(endExt+1);
			    		endExt = line.indexOf(delimeter);
			    		String syst = line.substring(0, endExt);
			    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): Got Systolic: " + syst);
			    		line = line.substring(endExt+1);
			    		endExt = line.indexOf(delimeter);
			    		String diast = line.substring(0, endExt);
			    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): Got Diastolic: " + diast);
			    		line = line.substring(endExt+1);
			    		endExt = line.indexOf(delimeter);
			    		String	pulse;
			    		if ( endExt > 0 )
			    			pulse = line.substring(0, endExt);
			    		else
			    			pulse = line;
			    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): Got Pulse: " + pulse);
			    		/*
			    		line = line.substring(endExt+1);
			    		String name = line;
			    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): Got name: " + name);
			    		*/
			    		ContentValues vals = new ContentValues();
			    		BldPrsrLogger.i(TAG, SubTag + "ActualDoImport(): Actual name: " + username);
		            	vals.put("name", username);
		            	vals.put("mDate", evYear + "/" + evMonth + "/" + evDay );
		            	vals.put("mTime",  evHour + ":" + evMins);
		            	vals.put("dPrsr", diast);
		            	vals.put("sPrsr", syst);
		            	vals.put("pulse", pulse);
        			
		    			cr.insert(tmpUri, vals);            	
			    		lineNumber++;
		    		}
		    	}
		    }
		    // close the file again
		    instream.close();
		} 
		catch (Exception e) 
		{
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
	}
	private void doImport()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Importing file: \n/mnt/sdcard/bldprsr.csv\n\nDo you want overwrite existing data?");
		builder.setCancelable(false);
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) 
		    {
				BldPrsrLogger.i(TAG, SubTag + "Keep current database");
				/* Perform actual import */
				actualDoImport();
				
				finish();
		    }
		});
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) 
			{
				BldPrsrLogger.i(TAG, SubTag + "Removing the database");
				ContentResolver cr = getContentResolver();
				BldPrsrLogger.i(TAG, SubTag + "Got content resolver");
				Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
				tmpUri = Uri.withAppendedPath(tmpUri,"bpData");        			
				cr.delete(tmpUri, null, null);
				BldPrsrLogger.i(TAG, SubTag + "Results data is removed");
				/* Perform actual import */
				actualDoImport();
				
				finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	};
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		doImport();
	}

}
