package ch.cern.alice.alimonalisa.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import ch.cern.alice.alimonalisa.contentprovider.ContentProviderUtils;
import ch.cern.alice.alimonalisa.contentprovider.NotificationContentProvider;
import ch.cern.alice.alimonalisa.database.NotificationDaoImpl;
import ch.cern.alice.alimonalisa.database.NotificationDbOpenHelper;
import ch.cern.alice.alimonalisa.model.Notification;
import ch.cern.alice.alimonalisa.model.Notification.Category;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

public class DownloadXmlTask extends
		AsyncTask<String, Void, List<Notification>> {
	
	// the context of the application
	private Context context;

	// database access
	private NotificationDaoImpl mDao;

	public DownloadXmlTask(Context context) {
		this.context = context;
		mDao = new NotificationDaoImpl(context, "en");
	}

	@Override
	protected void onPreExecute() {
		mDao.open();
	}

	@Override
	protected List<Notification> doInBackground(String... params) {
		try {
			return loadXmlFromNetwork(params[0]);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(List<Notification> result) {
		ContentResolver cr = context.getContentResolver();
		for (Notification n : result) {
			cr.insert(NotificationContentProvider.CONTENT_URI,
					ContentProviderUtils.notificationToContentValues(n));
		}

		mDao.close();
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
