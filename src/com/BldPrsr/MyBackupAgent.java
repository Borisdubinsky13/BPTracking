package com.BldPrsr;

import java.io.File;
import java.io.IOException;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class MyBackupAgent extends BackupAgentHelper {

	private static final String DB_NAME = "bldprsr.db";
	public String TAG = "MyBackupAgent";
	public String SubTag;
	static final String FILE_HELPER_KEY = "bldprsr.db";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.app.backup.BackupAgentHelper#onBackup(android.os.ParcelFileDescriptor
	 * , android.app.backup.BackupDataOutput, android.os.ParcelFileDescriptor)
	 */
	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
			ParcelFileDescriptor newState) throws IOException {
		SubTag = "onBackup(): ";
		Log.i(TAG, SubTag + "OnBackup()...");
		super.onBackup(oldState, data, newState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.backup.BackupAgentHelper#onRestore(android.app.backup.
	 * BackupDataInput, int, android.os.ParcelFileDescriptor)
	 */
	@Override
	public void onRestore(BackupDataInput data, int appVersionCode,
			ParcelFileDescriptor newState) throws IOException {
		SubTag = "onRestore(): ";
		Log.i(TAG, SubTag + "OnRestore()...");
		super.onRestore(data, appVersionCode, newState);
	}

	@Override
	public void onCreate() {
		SubTag = "onCreate(): ";
		Log.i(TAG, SubTag + "OnCreate()...");
		FileBackupHelper helper = new FileBackupHelper(this, DB_NAME);
		addHelper(FILE_HELPER_KEY, helper);
	}

	@Override
	public File getFilesDir() {
		SubTag = "getFilesDir(): ";
		File path = getDatabasePath(DB_NAME);
		Log.i(TAG, SubTag + "The path for DB: " + path.getParentFile());
		return path.getParentFile();
	}

}
