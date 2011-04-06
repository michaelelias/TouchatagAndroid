package com.touchatag.android.client.rest.serialization;

import java.io.IOException;
import java.io.StringReader;
import java.util.SortedMap;
import java.util.TreeMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.touchatag.android.client.rest.model.Application;

public class ApplicationAdapter extends PageItemDeserializer<Application> {

	private static final String ATTR_ID = "id";
	private static final String ATTR_OWNERID = "ownerid";
	private static final String ATTR_CREATED = "created";
	private static final String TAG_SPECIFICATION = "specification";
	private static final String TAG_APPLICATION = "application";

	@Override
	public Application deserialize(XmlPullParser xpp) throws XmlPullParserException, IOException {
		Application app = new Application();
		while (!isAtApplicationEndTag(xpp)) {
			switch (xpp.getEventType()) {
			case XmlPullParser.START_TAG:
				if (TAG_APPLICATION.equals(xpp.getName())) {
					app.setId(getAttributeValue(xpp, ATTR_ID));
					app.setOwnerId(getAttributeValue(xpp, ATTR_OWNERID));
					app.setCreated(getAttributeValue(xpp, ATTR_CREATED));
				} else if (TAG_SPECIFICATION.equals(xpp.getName())) {
					// parse spec xml
					throw new RuntimeException("Implement specification parsing");
				}
				break;
			}
			xpp.next();
		}
		return app;
	}

	private boolean isAtApplicationEndTag(XmlPullParser xpp) throws XmlPullParserException {
		return xpp.getEventType() == XmlPullParser.END_TAG && TAG_APPLICATION.equals(xpp.getName());
	}

	public Application deserialize(String appXml) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(appXml));
			return deserialize(xpp);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String serialize(Application app) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		SortedMap<String, String> attributes = new TreeMap<String, String>();
		String ns = "ns2";
		attributes.put("xmlns", "http://acs.touchatag.com/schema/specification-1.1");
		attributes.put("xmlns:" + ns, "http://acs.touchatag.com/schema/application-1.0");
		if (app.getOwnerId() != null) {
			attributes.put(ATTR_OWNERID, app.getOwnerId());
		}
		if (app.getId() != null) {
			attributes.put(ATTR_ID, app.getId());
		}
		if (app.getCreated() != null) {
			attributes.put(ATTR_CREATED, app.getCreated().toString());
		}
		sb.append(AdapterUtils.startTagWithAttributes(ns + ":" + TAG_APPLICATION, attributes));
		if (app.getSpecification() != null) {
			sb.append(app.getSpecification().toXml());
		}
		sb.append(AdapterUtils.endTag(ns + ":" + TAG_APPLICATION));

		return sb.toString();
	}
}
