/**
 * 
 */
package com.BldPrsr;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Boris
 *
 */
public class BldPrsrSetupWin extends Activity 
{
	public static String TAG="BldPrsr";
	public static String SubTag="setupHandler: ";

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
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
        } catch(NoSuchAlgorithmException e) {
        	BldPrsrLogger.e(TAG, SubTag + e.getMessage());
            return null;
        }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setupscr);
		final Button addB = (Button)findViewById(R.id.add);
		final Button cancelB = (Button)findViewById(R.id.clear);

        addB.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) 
            {
            	
            	ContentValues	vals = new ContentValues();
            	EditText nm = (EditText)findViewById(R.id.nameSup);
            	EditText fn = (EditText)findViewById(R.id.fstName);
            	EditText ln = (EditText)findViewById(R.id.lastName);
            	EditText em = (EditText)findViewById(R.id.emailSup);
            	EditText pss = (EditText)findViewById(R.id.passSup);
            	EditText gndr = (EditText)findViewById(R.id.gender);
            	EditText age = (EditText)findViewById(R.id.age);
            	String md5hash = getMd5Hash(pss.getText().toString());
            	
            	BldPrsrLogger.i(TAG, SubTag + "Add button is clicked");
            	BldPrsrLogger.i(TAG, SubTag + "ID: " + nm.getText().toString());
            	BldPrsrLogger.i(TAG, SubTag + "First name: " + fn.getText().toString());
            	BldPrsrLogger.i(TAG, SubTag + "Last Name: " + ln.getText().toString());
            	BldPrsrLogger.i(TAG, SubTag + "e-mail: " + em.getText().toString());
            	BldPrsrLogger.i(TAG, SubTag + "Gender: " + gndr.getText().toString());
            	BldPrsrLogger.i(TAG, SubTag + "age: " + age.getText().toString());
            	BldPrsrLogger.i(TAG, SubTag + "Pass: " + md5hash);
            	vals.put("name", nm.getText().toString());
            	vals.put("email", em.getText().toString());
            	vals.put("passwd", md5hash);
            	vals.put("firstName", fn.getText().toString());
            	vals.put("lastName", ln.getText().toString());
            	vals.put("gender", gndr.getText().toString());
            	vals.put("age", age.getText().toString());

    			ContentResolver cr = getContentResolver();
    			BldPrsrLogger.i(TAG, SubTag + "Got content resolver");
    			Uri	tmpUri = Uri.parse("content://com.BldPrsr.provider.userContentProvider");
    			tmpUri = Uri.withAppendedPath(tmpUri,"bpUsers");
    			BldPrsrLogger.i(TAG, SubTag + "Got URI populated");        			
    			cr.insert(tmpUri, vals);

            	finish();
            }
        });
        cancelB.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) 
            {
            	BldPrsrLogger.i(TAG, SubTag + "Cancel button is clicked");
            	finish();
            }
        });

	}
}
