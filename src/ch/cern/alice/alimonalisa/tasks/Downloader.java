package ch.cern.alice.alimonalisa.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.cern.alice.alimonalisa.MainActivity;
import ch.cern.alice.alimonalisa.database.NotificationDaoImpl;
import ch.cern.alice.alimonalisa.model.Notification;
import ch.cern.alice.alimonalisa.model.Notification.Category;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class Downloader extends AsyncTask<String, Void, List<Notification>> {

	// string constants for xml elements
	public static final String ENTRY = "entry";
	public static final String ID = "id";
	public static final String TITLE = "title";
	public static final String LINK = "link";
	public static final String PUBLISHED = "published";
	public static final String UPDATED = "updated";
	public static final String SUMMARY = "summary";
	public static final String CONTENT = "content";
	public static final String CATEGORY = "category";

	// string constants for attributes
	public static final String HREF = "href";
	public static final String TERM = "term";

	// string constants for categories
	public static final String STORAGE = "Storages";
	public static final String SERVICE = "Site services";
	public static final String PROXY = "Proxies";
	public static final String NETWORK = "";
	public static final String INFO = "Announcements";

	// database for inserting
	private NotificationDaoImpl mDao;
	// the context of the application for creating GUI
	private Context context;

	// progress dialog for downloading
	private ProgressDialog loadingDialog;
	// parser for date and time
	private DateTimeFormatter parser = ISODateTimeFormat.dateTime();

	public Downloader(Context context) {
		mDao = new NotificationDaoImpl(context, "en");
		this.context = context;
	}

	public List<Notification> parseXml(InputStream in) throws SAXException,
			ParserConfigurationException, IOException {
		List<Notification> res = new ArrayList<Notification>();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document dom = db.parse(in);

		Element docElement = dom.getDocumentElement();
		in.close();

		NodeList nl = docElement.getElementsByTagName("entry");

		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// i-th entry
				Element entry = (Element) nl.item(i);

				int id = Integer.parseInt(getElementValue(getElementByTagName(
						entry, ID)));

				String link = getElementAttributeValue(
						getElementByTagName(entry, LINK), HREF);

				DateTime startTime = parser
						.parseDateTime(getElementValue(getElementByTagName(
								entry, PUBLISHED)));

				DateTime endTime = parser
						.parseDateTime(getElementValue(getElementByTagName(
								entry, UPDATED)));

				// html
				String summary = getElementValue(getElementByTagName(entry,
						SUMMARY));

				String title = getElementValue(getElementByTagName(entry, TITLE));

				// html
				String description = getElementValue(getElementByTagName(entry,
						CONTENT));

				String c = getElementAttributeValue(
						getElementByTagName(entry, CATEGORY), TERM);

				Category category = null;

				switch (c) {
				case STORAGE:
					category = Category.STORAGE;
					break;
				case SERVICE:
					category = Category.SERVICE;
					break;
				case PROXY:
					category = Category.PROXY;
					break;
				case NETWORK:
					category = Category.NETWORK;
					break;
				case INFO:
					category = Category.INFO;
					break;
				default:
					category = Category.OTHER;
					break;
				}

				res.add(new Notification(id, link, startTime, endTime, summary,
						title, description, category, false, false, false));
			}
		}

		return res;

	}

	private Element getElementByTagName(Element entry, String name) {
		NodeList nl = entry.getElementsByTagName(name);
		if (nl != null && nl.getLength() > 0) {
			Element e = (Element) nl.item(0);
			return e;
		}
		return null;
	}

	private String getElementValue(Element entry) {
		String res = "";

		if (entry != null) {
			res = entry.getNodeValue();
		}

		return res;
	}

	private String getElementAttributeValue(Element entry, String attrName) {
		String res = "";
		if (entry.hasAttribute(attrName)) {
			res = entry.getAttribute(attrName);
		}

		return res;
	}

	public InputStream getStreamFromUrl(String url) throws Exception {
		HttpResponse httpResponse = null;
		InputStream is = null;

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();

		if (httpResponse != null
				&& httpResponse.getStatusLine().getStatusCode() == 200) {
			return is;
		}

		return null;
	}

	private void createDialog() {
		loadingDialog = new ProgressDialog(context);
		loadingDialog.setTitle("Downloading");
		loadingDialog
				.setMessage("Please wait while we are downloading the todo items.");
		loadingDialog.setIndeterminate(true);
		loadingDialog.setCancelable(false);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// createDialog();
		// loadingDialog.show();

	}

	@Override
	protected List<Notification> doInBackground(String... params) {
		List<Notification> notifications = null;
		if (params.length == 1) {
			String url = params[0];
			try {
				notifications = parseXml(getStreamFromUrl(url));
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return notifications;
	}

	@Override
	protected void onPostExecute(List<Notification> result) {

		mDao.open();
		
		for (Notification n : result) {
			try {
				mDao.insert(n);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		mDao.close();

	}
}
