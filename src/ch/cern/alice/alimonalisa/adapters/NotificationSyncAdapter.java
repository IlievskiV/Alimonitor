package ch.cern.alice.alimonalisa.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import ch.cern.alice.alimonalisa.CommonConstants;
import ch.cern.alice.alimonalisa.MainActivity;
import ch.cern.alice.alimonalisa.R;
import ch.cern.alice.alimonalisa.contentprovider.ContentProviderUtils;
import ch.cern.alice.alimonalisa.contentprovider.NotificationContentProvider;
import ch.cern.alice.alimonalisa.database.NotificationDbOpenHelper;
import ch.cern.alice.alimonalisa.model.Notification;
import ch.cern.alice.alimonalisa.model.Notification.Category;
import ch.cern.alice.alimonalisa.receivers.NetworkReceiver;
import ch.cern.alice.alimonalisa.services.NotificationService;
import ch.cern.alice.alimonalisa.tasks.AlimonitorXmlParser;
import android.accounts.Account;
import android.app.IntentService;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.widget.Toast;

public class NotificationSyncAdapter extends AbstractThreadedSyncAdapter {

	public static final String url = "http://alimonitor.cern.ch/atom.jsp";

	private ContentResolver mContentResolver;

	private List<Notification> notifications = null;

	private Intent mIntentService;

	private Context context;

	public NotificationSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);

		mContentResolver = context.getContentResolver();

		this.context = context;

		mIntentService = new Intent(context, NotificationService.class);
		
		notifications = new ArrayList<Notification>();
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		if (NetworkReceiver.refreshData) {
			try {
				Notification not = new Notification();
				
				not.setId(123);
				not.setDescription("CERN Summer Student are having pary!!!! GO NOW!!! :D");
				not.setCategory(Category.INFO);
				
				// here we need some kind of filter
				notifications.addAll(loadXmlFromNetwork(url));


				
				notifications.add(not);
				
				for (Notification n : notifications) {
					mContentResolver
							.insert(NotificationContentProvider.CONTENT_URI,
									ContentProviderUtils
											.notificationToContentValues(n));
				}

				mContentResolver.notifyChange(
						NotificationContentProvider.CONTENT_URI, null);

				mIntentService
						.setAction(CommonConstants.ONE_NOTIFICATION_ACTION);
				// put the id of the notification
				mIntentService.putExtra(CommonConstants.NOTIFICATION_ID,
						notifications.get(0).getId());

				context.startService(mIntentService);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
		}
	}

	private List<Notification> loadXmlFromNetwork(String urlString)
			throws IOException, XmlPullParserException {
		InputStream stream = null;
		AlimonitorXmlParser parser = new AlimonitorXmlParser();
		List<Notification> notifications = null;

		try {
			stream = downloadUrl(urlString);
			notifications = parser.parse(stream);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

		return notifications;
	}

	private InputStream downloadUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();
		InputStream stream = conn.getInputStream();
		return stream;
	}
}
