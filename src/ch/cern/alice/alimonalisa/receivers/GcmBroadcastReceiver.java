package ch.cern.alice.alimonalisa.receivers;

import ch.cern.alice.alimonalisa.services.GenericAccountService;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class GcmBroadcastReceiver extends BroadcastReceiver {

	// Constants
	// Content provider authority
	public static final String AUTHORITY = "ch.cern.alice.alimonalisa.contentprovider";
	// Account type
	public static final String ACCOUNT_TYPE = "ch.cern.alice.alimonalisa";
	// Account
	public static final String ACCOUNT = "default_account";

	// Incoming Intent key for extended data
	// public static final String KEY_SYNC_REQUEST =
	// "com.example.android.datasync.KEY_SYNC_REQUEST";

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get a GCM object instance
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

		// Get the type of GCM message
		String messageType = gcm.getMessageType(intent);

		Toast toast = Toast.makeText(context, "Message received",
				Toast.LENGTH_LONG);
		toast.show();
		ContentResolver.requestSync(GenericAccountService.GetAccount(),
				AUTHORITY, new Bundle());

	}

}
