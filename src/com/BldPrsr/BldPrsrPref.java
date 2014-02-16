/**
 * 
 */
package com.BldPrsr;

import android.os.Bundle;
import android.preference.PreferenceFragment;
/**
 * @author Boris
 *
 */

public class BldPrsrPref extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// final String PREF_FILE_NAME = "PrefFile";
		// SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		
  		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		
	}
}