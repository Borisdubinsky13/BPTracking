/**
 *
 */
package com.BldPrsr;

import android.app.Application;
import android.content.Context;

/**
 * @author boris
 */
public class BldPrsr extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        BldPrsr.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return BldPrsr.context;
    }
}
