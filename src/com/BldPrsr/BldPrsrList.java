package com.BldPrsr;
import java.util.ArrayList;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.*;

public class BldPrsrList extends ListActivity 
{
	/**
	 * 
	 */
	public static String TAG="BldPrsr";
	public static String SubTag="BldPrsrMain: ";
		
	public static final String PREFS_NAME = "BldPrsrFile";
	private static final String PREF_USERNAME = "username";	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}

    class ShowViewBinder implements SimpleCursorAdapter.ViewBinder 
    {
    	public boolean setViewValue(View view, Cursor cursor, int columnIndex) 
    	{
    		TextView tv = (TextView) view;
    		String	tmpStr;
    		
    		BldPrsrLogger.i(TAG, SubTag + "Processing column: " + columnIndex);

   			tmpStr = "Systolic: " + cursor.getString(cursor.getColumnIndex("dPrsr"));
   			tmpStr += ", Diastolic: " + cursor.getString(cursor.getColumnIndex("sPrsr"));
   			tmpStr += ", Pulse: " + cursor.getString(cursor.getColumnIndex("pulse"));
    		BldPrsrLogger.i(TAG, SubTag + "String: " + tmpStr);
    		tv.setText(tmpStr);
    		return true;
    	}
    }

	
	@Override
	protected void onResume()
	{
		super.onResume();
		setContentView(R.layout.list);

		AdView	adView = (AdView)findViewById(R.id.adListRes);
		// Initiate a generic request to load it with an ad
	    adView.loadAd(new AdRequest());
	        
	    BldPrsrLogger.i(TAG, SubTag + "ListRes()"); 
    	SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);   
    	String username = pref.getString(PREF_USERNAME, null);
		this.setTitle("User: " + username);
		String query = "name = '" + username + "'";

		Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
		tmpUri = Uri.withAppendedPath(tmpUri,"bpData");
		String[] projection = new String[] {
				"_id",
				"sPrsr",
				"dPrsr",
				"pulse"
		};
		Cursor result = getContentResolver().query(tmpUri, projection, query, null, null);
		startManagingCursor(result);
		
		String[] columns = new String[] { "sPrsr", "dPrsr", "pulse" };
		int[] to = new int[] { R.id.sValue }; // , R.id.dValue, R.id.pulseValue };
		
		BldPrsrLogger.i(TAG, SubTag + "Everything is ready for the adapter. # of records: " + result.getCount());
		SimpleCursorAdapter  items = new SimpleCursorAdapter(this, R.layout.listnumbers, result, columns, to);
		BldPrsrLogger.i(TAG, SubTag + "Items information: " + items.getCount() + " entries");
        if ( items.getCount() > 0 )
        {
        	items.setViewBinder(new ShowViewBinder());
        	this.setListAdapter(items);
        }
        else
        {
        	int duration = Toast.LENGTH_LONG;
        	String text;
        	text = "There are no entries for the current user";
			Toast toast = Toast.makeText(getApplicationContext(), text, duration);
			toast.show();
			finish();
        }
	

	}
}
