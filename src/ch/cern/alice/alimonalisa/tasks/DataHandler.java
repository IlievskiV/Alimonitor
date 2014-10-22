package ch.cern.alice.alimonalisa.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import org.joda.time.*;
import org.joda.time.format.*;

import android.util.Log;
import ch.cern.alice.alimonalisa.model.Notification;
import ch.cern.alice.alimonalisa.model.Notification.Category;

public class DataHandler extends DefaultHandler {

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
	
	
	// beginning of every node
	private boolean isEntry = false;
	private boolean isId = false;
	private boolean isTitle = false;
	private boolean isLink = false;
	private boolean isPublished = false;
	private boolean isUpdated = false;
	private boolean isSummary = false;
	private boolean isContent = false;
	private boolean isCategory = false;

	public static final String url = "http://alimonitor.cern.ch/atom.jsp";

	// resulting notifications after parsing
	private ArrayList<Notification> notifications;

	// the current notification
	private Notification currentNotification;


	// the resulting string from parsing every element
	private String resultString = "";

	// the context of the application
	private Context context;

	private DateTimeFormatter parser = ISODateTimeFormat.dateTime();
	private DateTime dt = null;

	// constructor
	public DataHandler(Context context) {
		super();
		this.context = context;
		notifications = new ArrayList<Notification>();
	}

	public ArrayList<Notification> getData(InputStream is) throws ParserConfigurationException,
			SAXException, IOException {
		// set the parsing driver
		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
		// create a parser
		SAXParserFactory parseFactory = SAXParserFactory.newInstance();
		SAXParser xmlParser = parseFactory.newSAXParser();
		// get an XML reader
		XMLReader xmlIn = xmlParser.getXMLReader();
		// instruct the app to use this object as the handler
		xmlIn.setContentHandler(this);
		// name and the location of the XML file
		URL xmlURL = new URL(url);
		// open the connection and get an input stream
		URLConnection xmlCon = xmlURL.openConnection();
		InputStreamReader xmlStream = new InputStreamReader(
				xmlCon.getInputStream());
		// build a buffered reader
		BufferedReader xmlBuff = new BufferedReader(xmlStream);
		// parse the data
		xmlIn.parse(new InputSource(is));
		
		return notifications;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String data = new String(ch, start, length);
		
		
		
	}

	@Override
	public void endDocument() throws SAXException {
		Log.i("DataHandler", "End of XML document");
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		switch (localName) {
		case ENTRY:
			isEntry = false;
			notifications.add(currentNotification);
			resultString = "";
			break;

		case ID:
			try {
				currentNotification
						.setId(Integer.parseInt(resultString.trim()));
				resultString = "";
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			break;

		case TITLE:
			currentNotification.setTitle(resultString);
			resultString = "";
			break;

		case LINK:
			currentNotification.setLink(resultString);
			resultString = "";
			break;

		case PUBLISHED:
			dt = parser.parseDateTime(resultString);
			currentNotification.setStartTime(dt);
			resultString = "";
			break;

		case UPDATED:
			dt = parser.parseDateTime(resultString);
			currentNotification.setEndTime(dt);
			resultString = "";
			break;

		case SUMMARY:
			currentNotification.setSummary(resultString);
			resultString = "";
			break;

		case CONTENT:
			currentNotification.setDescription(resultString);
			resultString = "";
			break;

		case CATEGORY:
			// we need some logic here
			currentNotification.setCategory(Category.SERVICE);
			resultString = "";
			break;

		default:
			break;
		}
	}

	@Override
	public void startDocument() throws SAXException {
		Log.i("DataHandler", "Start of XML document");
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		switch (localName) {
		case ENTRY:
			isEntry = true;
			currentNotification = new Notification();
			break;
		case ID:
			
		default:
			break;
		}
	}

}
