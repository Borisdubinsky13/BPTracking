/**
 * 
 */
package com.BldPrsr;

import android.preference.PreferenceActivity;

/**
 * @author Boris
 *
 */
public class BldPrsrPrefActivity extends PreferenceActivity  {

	@Override
	protected void onResume() {
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new BldPrsrPref()).commit();

		super.onResume();
	}
}
