package ch.cern.alice.alimonalisa;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.ParserConfigurationException;

import ch.cern.alice.alimonalisa.contentprovider.NotificationContentProvider;
import ch.cern.alice.alimonalisa.receivers.NetworkReceiver;
import ch.cern.alice.alimonalisa.services.DownloadService;
import ch.cern.alice.alimonalisa.services.GenericAccountService;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.audiofx.AcousticEchoCanceler;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import org.xml.sax.SAXException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This is the home screen of the application.
 * 
 * @author Vladimir
 * 
 */
public class MainActivity extends FragmentActivity {

	// string constants
	public static final String WIFI = "Wi-Fi";
	public static final String ANY = "Any";
	public static final String DEFAULT_LANG = "en";
	public static final String ACCOUNT_TYPE = "ch.cern.alice.alimonalisa";
	public static final String ACCOUNT = "default_account";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private Account mAccount;

	// The user's current network preference setting.
	public static String sPref = null;

	// GCM client for checking
	private GCMClientServices gcmClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// mDrawerLayout.setDrawerListener(new DrawerListener() {
		//
		// @Override
		// public void onDrawerStateChanged(int arg0) {
		//
		// }
		//
		// @Override
		// public void onDrawerSlide(View arg0, float arg1) {
		//
		// }
		//
		// /** Called when a drawer has settled in a completely closed state. */
		// @Override
		// public void onDrawerOpened(View arg0) {
		//
		// }
		//
		// @Override
		// public void onDrawerClosed(View arg0) {
		//
		// }
		// });

		// don't forget the adapter and listener for the list

		mAccount = GenericAccountService.GetAccount();
		AccountManager accountManager = (AccountManager) getSystemService(Service.ACCOUNT_SERVICE);
		if (accountManager.addAccountExplicitly(mAccount, null, null)) {
			ContentResolver.setSyncAutomatically(mAccount,
					NotificationContentProvider.AUTHORITY, true);
		}

		gcmClient = new GCMClientServices(this);
		gcmClient.check();
		

		setContentView(R.layout.activity_main);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		FragmentManager fm = getSupportFragmentManager();

		TextView textView = (TextView) fm.findFragmentById(R.id.top_fragment)
				.getView().findViewById(R.id.title);
		textView.setText("Notifications");

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		sPref = sharedPrefs.getString("listPref", WIFI);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Check device for Play Services APK.
		gcmClient.check();
	}

	public static Account createSyncAdapter(Context context) {

		Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);

		AccountManager accountManager = (AccountManager) context
				.getSystemService(ACCOUNT_SERVICE);

		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			return newAccount;
		} else {
			Log.i("AccountTag", "Cannot create account");
			return null;
		}
	}

	public void insert(View view) {
		try {
			insertNotifications();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void insertNotifications() throws ParseException,
			ParserConfigurationException, SAXException, IOException {

		startService(new Intent(this, DownloadService.class));
		CharSequence text = "Inserted";
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}
}
