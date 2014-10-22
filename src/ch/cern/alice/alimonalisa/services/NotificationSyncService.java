package ch.cern.alice.alimonalisa.services;

import ch.cern.alice.alimonalisa.adapters.NotificationSyncAdapter;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationSyncService extends Service {

	// Storage for an instance of the sync adapter
	private static NotificationSyncAdapter sSyncAdapter = null;
	// Object to use as a thread-safe lock
	private static final Object sSyncAdapterLock = new Object();

	@Override
	public void onCreate() {
		synchronized (sSyncAdapterLock) {
			sSyncAdapter = new NotificationSyncAdapter(getApplicationContext(),
					true);
		}
	}

	/**
	 * Return an object that allows the system to invoke the sync adapter.
	 * 
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return sSyncAdapter.getSyncAdapterBinder();
	}

}
