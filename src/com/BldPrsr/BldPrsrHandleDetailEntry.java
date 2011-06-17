package com.BldPrsr;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class BldPrsrHandleDetailEntry extends Activity 
{
	public static String TAG="BldPrsr";
	public static String SubTag="BldPrsrHandleDetailEntry: ";
	
	public static final String PREFS_NAME = "BldPrsr";
	private static final String PREF_ID = "dataTBL_ID";
	private static final String PREF_USERNAME = "username";
	
	private String idIndex="";
	
	Button dateB;
	Button updB;
	Button delB;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		setContentView(R.layout.detailhandleentry);
		
		SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);   
		String username = pref.getString(PREF_USERNAME, null);
		this.setTitle("User: " + username);
		idIndex = pref.getString(PREF_ID, null);
		
		final Calendar c = Calendar.getInstance();
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
            	new DatePickerDialog(BldPrsrHandleDetailEntry.this, mDateSetListener, mYear, mMonth-1, mDay).show();
            }
        });
 	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() 
    {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		{
			String evYearS = String.format("%04d", year);
			String evMonthS = String.format("%02d", monthOfYear+1);
			String evDayS =  String.format("%02d", dayOfMonth);

			String eventDate = evYearS + "/" + evMonthS + "/" + evDayS;
			dateB.setText(eventDate);
		}
    };    	

}
