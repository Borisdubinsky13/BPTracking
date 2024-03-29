/**
 * 
 */
package com.BldPrsr;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Boris
 * 
 */
public class AboutHandler extends Activity {
	public String TAG = "BldPrsr";
	public String SubTag = "AboutHandler: ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_display);

		final String PREFS_NAME = "gamePnLTrackerFile";
		final String PREF_USERNAME = "username";
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String username = pref.getString(PREF_USERNAME, null);

		this.setTitle("User: " + username);

		final TextView appVersion = (TextView) findViewById(R.id.AppVersion);
		String versionName = "";
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(),
					0).versionName;
		} catch (Exception e) {
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
		}
		appVersion.setText(versionName);

		MyDbHelper db = new MyDbHelper(this);
		String dbVer = String.valueOf(db.version());
		final TextView dbVersion = (TextView) findViewById(R.id.dbVersionId);
		dbVersion.setText(dbVer);

		final Button dispB = (Button) findViewById(R.id.dispRelNotes);
		dispB.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setContentView(R.layout.releasenotes);
			}
		});
	}
}
