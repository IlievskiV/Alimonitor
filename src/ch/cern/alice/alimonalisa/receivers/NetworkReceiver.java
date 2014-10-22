package ch.cern.alice.alimonalisa.receivers;

import ch.cern.alice.alimonalisa.MainActivity;
import ch.cern.alice.alimonalisa.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {

	// Whether the display should be refreshed.
	public static boolean refreshData = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		// Checks the user prefs and the network connection. Based on the
		// result, decides
		// whether
		// to refresh the display or keep the current display.
		// If the userpref is Wi-Fi only, checks to see if the device has a
		// Wi-Fi connection.
		if (MainActivity.WIFI.equals(MainActivity.sPref) && networkInfo != null
				&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			// If device has its Wi-Fi connection, sets refreshDisplay
			// to true. This causes the display to be refreshed when the user
			// returns to the app.
			refreshData = true;

			// If the setting is ANY network and there is a network connection
			// (which by process of elimination would be mobile), sets
			// refreshDisplay to true.
		} else if (MainActivity.ANY.equals(MainActivity.sPref)
				&& networkInfo != null) {
			refreshData = true;

			// Otherwise, the app can't download content--either because there
			// is no network
			// connection (mobile or Wi-Fi), or because the pref setting is
			// WIFI, and there
			// is no Wi-Fi connection.
			// Sets refreshDisplay to false.
		} else {
			refreshData = false;
		}
	}

}
