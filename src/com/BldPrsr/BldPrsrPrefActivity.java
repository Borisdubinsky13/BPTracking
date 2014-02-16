/**
 * 
 */
package com.BldPrsr;

import android.app.Activity;

/**
 * @author Boris
 *
 */
public class BldPrsrPrefActivity extends Activity {

	@Override
	protected void onResume() {
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new BldPrsrPref()).commit();
		super.onResume();
	}

}
