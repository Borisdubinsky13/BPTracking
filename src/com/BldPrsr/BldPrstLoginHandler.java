package com.BldPrsr;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BldPrstLoginHandler extends Activity 
{
    /** Called when the activity is first created. */
	public static String TAG="BldPrsr";
	public static String SubTag="BldPrstLoginHandler: ";
	
	// private MyDbAdapter dB = new MyDbAdapter(this);
	
	int duration = Toast.LENGTH_SHORT;
	public static final String PREFS_NAME = "BldPrsrFile";
	private static final String PREF_USERNAME = "username";
	CharSequence text = "No text";
	
	public static String getMd5Hash(String input) 
	{
        try {
        	MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1,messageDigest);
            String md5 = number.toString(16);
           
            while (md5.length() < 32)
            	md5 = "0" + md5;
           
            return md5;
        } 
        catch(NoSuchAlgorithmException e) 
        {
        	BldPrsrLogger.e(TAG, SubTag + e.getMessage());
            return null;
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.preloginmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent iAbout = new Intent(this, AboutHandler.class);
		Intent iSetup = new Intent(this, BldPrsrSetupWin.class);

		// Handle item selection
	    switch (item.getItemId()) 
	    {
	    case R.id.aboutMenu:
	    	BldPrsrLogger.i(TAG, SubTag + "User " + "trying to start ABOUT");
	    	// Intent iAbout = new Intent(new Intent(this, AboutHandler.class));
	        startActivity(iAbout);
	        return true;
	    case R.id.setup:
	    	BldPrsrLogger.i(TAG, SubTag + "User " + "trying to start SETUP");
	        startActivity(iSetup);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	// See if this is running on the emulator
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
			Cursor	result;
			Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
			tmpUri = Uri.withAppendedPath(tmpUri,"bpUsers");
			result = managedQuery(tmpUri, null, null, null, null);
			if ( result.getCount() < 1 )
			{
				// There are no users in the database. Start a setup intent
				Intent iSetup = new Intent(this, BldPrsrSetupWin.class);
				startActivity(iSetup);
			}
	        // See if user is still logged in. When user logges out, the status of the user in status table will change to false.
			tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
			tmpUri = Uri.withAppendedPath(tmpUri,"bpStatus");
			String[] projection = new String[] {
					"_id",
					"name",
					"status"
			};
			BldPrsrLogger.i(TAG, SubTag + "Got URI populated");
			String	query = " status = 'in'";
			result = managedQuery(tmpUri, projection, query, null, null);
			BldPrsrLogger.i(TAG, SubTag + "got " + result.getCount() + " records");
			if ( result.getCount() >= 1 )
			{
				// There is an active user. Skip the login window.
	        	Intent iDataEntry = new Intent(BldPrstLoginHandler.this,BldPrsrMain.class);
	        	
	        	result.moveToFirst();
	            getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
	           	.edit()
	        	.putString(PREF_USERNAME, result.getString(1).toString())
	        	.commit();
	            BldPrsrLogger.i(TAG, SubTag + "Trying to start AfterLogin intent");
		        startActivity(iDataEntry);
		        finish();
			}
    	}
    	catch ( Exception e)
    	{
    		BldPrsrLogger.e(TAG, SubTag + e.getMessage());
    	}
        final Button loginB = (Button)findViewById(R.id.loginB);
        loginB.setOnClickListener(new View.OnClickListener()
        {
        	public boolean isValidUser(String id, String pass)
        	{
        		BldPrsrLogger.i(TAG, SubTag + "User " + id + " is being validated");
        		if ( id.equals("") || pass.equals("") ) 
        		{
        			text = "Please specify user and password!";
        			Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        			toast.show();
        			return false;
        		}
        		else
        		{
        			// Check to make sure id and password are in the database.
        			Cursor	result;
        			int		cnt;
        			String[] projection = new String[] {
        					"name"
        			};
        			String selection = "name = '" + id + "' and passwd = '" + pass + "'";
        			
        			ContentResolver cr = getContentResolver();
         			Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
        			tmpUri = Uri.withAppendedPath(tmpUri,"bpUsers");
        			result = cr.query(tmpUri, projection, selection, null, null);
        			cnt = result.getCount();
        			result.deactivate();
        			
        			if ( cnt > 0 )
        				return true;
        			else
        			{
            			text = "Invalid login information. Try again.";
            			Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            			toast.show();
        				return false;
        			}
        		}
        	}

            public void onClick(View v) 
            {
            	// get password 
            	EditText idPass = (EditText)findViewById(R.id.passwd);
            	// Setup a preference class that would keep the user id
            	EditText idName = (EditText)findViewById(R.id.idName);
            	String md5hash = getMd5Hash(idPass.getText().toString());
                // go to main window
                if ( isValidUser(idName.getText().toString(), md5hash) )
                {
                	// Store current user into a database
        			Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
        			tmpUri = Uri.withAppendedPath(tmpUri,"bpStatus");
                	ContentValues vals = new ContentValues();
                	ContentResolver cr = getContentResolver();
                	vals.put("name", idName.getText().toString());
                	vals.put("status", "in");
                	cr.insert(tmpUri, vals);
                	
                	// store user id in Preferences for everybody to access.
                    getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                	.edit()
                	.putString(PREF_USERNAME, idName.getText().toString())
                	.commit();
                	Intent iDataEntry = new Intent(BldPrstLoginHandler.this,BldPrsrMain.class);
        	        startActivity(iDataEntry);    
                }
            }
        });
    }
}
