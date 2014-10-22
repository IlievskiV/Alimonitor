package ch.cern.alice.alimonalisa.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.DESedeKeySpec;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.sax.StartElementListener;
import android.util.Xml;
import ch.cern.alice.alimonalisa.CommonConstants;
import ch.cern.alice.alimonalisa.model.Notification;
import ch.cern.alice.alimonalisa.model.Notification.Category;

public class AlimonitorXmlParser implements ParseTag {
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
	public static final String REL = "rel";
	public static final String TERM = "term";
	// we don't use namespaces
	private static final String ns = null;

	// formatting of the date and time
	private DateTimeFormatter dateTimeParser = ISODateTimeFormat.dateTime();

	public List<Notification> parse(InputStream in)
			throws XmlPullParserException, IOException {
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		}finally{
			in.close();
		}
	}

	private List<Notification> readFeed(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<Notification> notifications = new ArrayList<Notification>();

		parser.require(XmlPullParser.START_TAG, ns, "feed");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			if (name.equals(ENTRY)) {
				notifications.add(readNotification(parser));
			} else {
				skip(parser);
			}
		}
		
		return notifications;
	}

	private Notification readNotification(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, ENTRY);
		int id = 0;
		String link = null;
		DateTime startTime = null;
		DateTime endTime = null;
		String summary = null;
		String title = null;
		String description = null;
		String category = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();

			switch (name) {
			case ID:
				id = readId(parser);
				break;
			case LINK:
				link = readLink(parser);
				break;
			case PUBLISHED:
				startTime = readPublished(parser);
				break;
			case UPDATED:
				endTime = readUpdated(parser);
				break;
			case SUMMARY:
				summary = readSummary(parser);
				break;
			case TITLE:
				title = readTitle(parser);
				break;
			case CONTENT:
				description = readDescription(parser);
				break;
			case CATEGORY:
				category = readCtegory(parser);
				break;
			default:
				break;
			}
		}

		Category c;
		switch (category.trim()) {
		case CommonConstants.SERVICES:
			c = Category.SERVICE;
			break;
		case CommonConstants.STORAGE:
			c = Category.STORAGE;
			break;
		case CommonConstants.NETWORK:
			c = Category.NETWORK;
			break;
		case CommonConstants.PROXY:
			c = Category.PROXY;
			break;
		default:
			c = Category.INFO;
			break;
		}

		return new Notification(id, link, startTime, endTime, summary, title,
				description, c, false, false, false);
	}

	// Skips tags the parser isn't interested in.
	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	@Override
	public int readId(XmlPullParser parser) throws XmlPullParserException,
			IOException, NumberFormatException {
		return Integer.parseInt(readTag(parser, ID));
	}

	@Override
	public String readLink(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		String link = "";
		parser.require(XmlPullParser.START_TAG, ns, LINK);
		String tag = parser.getName();
		String relType = parser.getAttributeValue(null, REL);
		if (tag.equals(LINK)) {
			if (relType.equals("alternate")) {
				link = parser.getAttributeValue(null, HREF);
				parser.nextTag();
			}
		}
		parser.require(XmlPullParser.END_TAG, ns, LINK);
		return link;
	}

	@Override
	public DateTime readPublished(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		return dateTimeParser.parseDateTime(readTag(parser, PUBLISHED));
	}

	@Override
	public DateTime readUpdated(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		return dateTimeParser.parseDateTime(readTag(parser, UPDATED));
	}

	@Override
	public String readSummary(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		return readTag(parser, SUMMARY);
	}

	@Override
	public String readTitle(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		return readTag(parser, TITLE);
	}

	@Override
	public String readDescription(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		return readTag(parser, CONTENT);
	}

	@Override
	public String readCtegory(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		String term = "";
		parser.require(XmlPullParser.START_TAG, ns, CATEGORY);
		term = parser.getAttributeValue(null, TERM);
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, ns, CATEGORY);
		return term;
	}

	@Override
	public String readText(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	@Override
	public String readTag(XmlPullParser parser, String tagName)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, tagName);
		String tagContent = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, tagName);
		return tagContent;
	}
}
