package ch.cern.alice.alimonalisa.tasks;

import java.io.IOException;

import org.joda.time.DateTime;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public interface ParseTag {

	// for getting the id
	public int readId(XmlPullParser parser) throws XmlPullParserException,
			IOException, NumberFormatException;

	// for getting the link
	public String readLink(XmlPullParser parser) throws XmlPullParserException,
			IOException;

	// for getting the start time
	public DateTime readPublished(XmlPullParser parser)
			throws XmlPullParserException, IOException;

	// for getting the end time
	public DateTime readUpdated(XmlPullParser parser)
			throws XmlPullParserException, IOException;

	// for getting the summary
	public String readSummary(XmlPullParser parser)
			throws XmlPullParserException, IOException;

	// for getting the title
	public String readTitle(XmlPullParser parser)
			throws XmlPullParserException, IOException;

	// for getting the description
	public String readDescription(XmlPullParser parser)
			throws XmlPullParserException, IOException;

	// for getting the category
	public String readCtegory(XmlPullParser parser)
			throws XmlPullParserException, IOException;

	public String readText(XmlPullParser parser) throws XmlPullParserException,
			IOException;

	// for reading the specified tag
	public String readTag(XmlPullParser parser, String tagName)
			throws XmlPullParserException, IOException;
}
