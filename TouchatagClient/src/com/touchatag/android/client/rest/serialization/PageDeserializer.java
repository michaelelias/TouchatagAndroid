package com.touchatag.android.client.rest.serialization;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.touchatag.android.client.rest.model.Page;

public class PageDeserializer {

	public static String TAG_PAGE = "page";
	public static String ATTR_PAGE = "page";
	public static String ATTR_PAGESIZE = "pagesize";
	public static String ATTR_TOTAL = "total";
	
	private static Map<String, PageItemDeserializer<?>> pageItemDeserializers = new HashMap<String, PageItemDeserializer<?>>();
	
	static {
		pageItemDeserializers.put("tag", new TagDeserializer());
		pageItemDeserializers.put("application", new ApplicationAdapter());
	}
	
	
	public static Page<?> deserialize(String pageXml) {

		int pageNumber, pageSize, total;

		Page<Object> page = new Page<Object>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(pageXml));
			int eventType = xpp.getEventType();
			
			boolean inPageTag = false;
			
			while(eventType != XmlPullParser.END_DOCUMENT){
				if (eventType == XmlPullParser.START_TAG) {
					String startTag = xpp.getName();
					if(TAG_PAGE.equals(startTag)){
						inPageTag = true;
						page.setTotal(Integer.parseInt(xpp.getAttributeValue(0)));
						page.setPageSize(Integer.parseInt(xpp.getAttributeValue(1)));
						page.setPageNumber(Integer.parseInt(xpp.getAttributeValue(2)));
					} else if(inPageTag){
						PageItemDeserializer pageItemDeserializer = pageItemDeserializers.get(startTag);
						if(pageItemDeserializer != null){
							page.getItems().add(pageItemDeserializer.deserialize(xpp));
						}
					}
				} else if(eventType == XmlPullParser.END_TAG){
					String endTag = xpp.getName();
					if(TAG_PAGE.equals(endTag)){
						inPageTag = false;
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return page;
	}

}
