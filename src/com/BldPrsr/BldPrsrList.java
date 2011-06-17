package com.BldPrsr;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
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
	public static String SubTag="BldPrsrList: ";
		
	public static final String PREFS_NAME = "BldPrsrFile";
	private static final String PREF_USERNAME = "username";	
	
	private static final String PREF_ID = "dataTBL_ID";
	
	public SimpleCursorAdapter  items;
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
    		/*
   			tmpStr = "Systolic: " + cursor.getString(cursor.getColumnIndex("dPrsr"));
   			tmpStr += ", Diastolic: " + cursor.getString(cursor.getColumnIndex("sPrsr"));
   			tmpStr += ", Pulse: " + cursor.getString(cursor.getColumnIndex("pulse"));
   			*/

    		switch ( columnIndex )
    		{
    		case 0:
    			tmpStr = cursor.getString(cursor.getColumnIndex("_id")) + ":";
    			break;
    		case 3:
    			tmpStr = cursor.getString(cursor.getColumnIndex("dPrsr")) + ":";
    			break;
    		case 4:
    			tmpStr = cursor.getString(cursor.getColumnIndex("mDate")) + " : ";
    			tmpStr += cursor.getString(cursor.getColumnIndex("sPrsr")) + ":";
    			break;
    		case 5:
    			tmpStr = cursor.getString(cursor.getColumnIndex("pulse"));
    			break;
    		default:
    			BldPrsrLogger.i(TAG, SubTag + "Unknown columnIndex" + columnIndex);
    			tmpStr = "";
    			return false;
    		}
    		tv.setText(tmpStr);
    		return true;
    	}
    }

	
	@Override
	protected void onResume()
	{
		super.onResume();
		setContentView(R.layout.list);
		
		registerForContextMenu(getListView());
		
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
		
		String[] columns = new String[] { "_id", "sPrsr", "dPrsr", "pulse" };
		int[] to = new int[] { R.id.sID, R.id.sValue, R.id.dValue, R.id.pulseValue };
		
		BldPrsrLogger.i(TAG, SubTag + "Everything is ready for the adapter. # of records: " + result.getCount());
		items = new SimpleCursorAdapter(this, R.layout.listnumbers, result, columns, to);
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
	
	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) 
	{
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		// TODO Auto-generated method stub
		BldPrsrLogger.i(TAG, SubTag + "Clicked on position: " + position + ", id: " + id );
		super.onListItemClick(l, v, position, id);
		
		// Save the entry in the preferences, so the display activity can display an appropriate record
        getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
       	.edit()
    	.putString(PREF_ID, id + "")
    	.commit();

    	Intent iDataEntry = new Intent(this, BldPrsrHandleDetailEntry.class);

        startActivity(iDataEntry);
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        BldPrsrLogger.i(TAG, SubTag + "Creating a context menu");
        menu.setHeaderTitle("Menu:");
        inflater.inflate(R.menu.detailentrymenu, menu);
    }

	public boolean onContextItemSelected(MenuItem item) 
	{
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	    switch(item.getItemId()) 
	    {
	    case R.id.delSelection:
	        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	        
	        BldPrsrLogger.i(TAG, SubTag + "Clicked on position: " + info.id );
	        // To get the id of the clicked item in the list use menuInfo.id
	        BldPrsrLogger.i(TAG, SubTag + "list pos:"+menuInfo.position+" id:"+menuInfo.id);
	    	ContentResolver cr = getContentResolver();
	    	String	query = "_ID = '" + menuInfo.id + "'";
	    	Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
	    	 
	    	tmpUri = Uri.withAppendedPath(tmpUri,"bpData");
	    	cr.delete(tmpUri, query, null);
	        return true;
	    case R.id.updSelection:
			// Save the entry in the preferences, so the display activity can display an appropriate record
	        getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
	       		.edit()
	       		.putString(PREF_ID, menuInfo.id + "")
	       		.commit();

	    	Intent iDataEntry = new Intent(this, BldPrsrHandleDetailEntry.class);

	        startActivity(iDataEntry);
	    	return true;
	    }
	    return super.onContextItemSelected(item);
	}
}
