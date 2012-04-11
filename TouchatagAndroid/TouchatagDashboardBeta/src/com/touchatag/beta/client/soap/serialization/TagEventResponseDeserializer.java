package com.touchatag.beta.client.soap.serialization;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.touchatag.beta.client.soap.model.response.ApplicationResponse;
import com.touchatag.beta.client.soap.model.response.TagEventFeedback;

public class TagEventResponseDeserializer {

	private static final String TAG_HANDLE_TAG_EVENT_RESPONSE = "handleTagEventResponse";
	private static final String TAG_SYSTEM_MESSAGE = "systemMessage";
	private static final String TAG_APPLICATION_RESPONSE = "applicationResponse";
	private static final String ATTR_APPLICATION_RESPONSE_IDENTIFIER = "identifier";

	public static TagEventFeedback deserialize(String response) {
		// <env:Envelope
		// xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'><env:Header></env:Header><env:Body>
		// <ns1:handleTagEventResponse
		// xmlns:ns1="http://www.touchatag.com/acs/api/correlation-1.2">
		// <applicationResponse
		// identifier="urn:com.touchatag:legacy-client-action">
		// <ClientAction>
		// <container name="tikitag.standard.tagManagement">
		// <container name="v1.0">
		// <attribute name="message">
		// <string>Tag (re)configuration in progress...</string>
		// </attribute></container></container></ClientAction></applicationResponse>
		// </ns1:handleTagEventResponse></env:Body></env:Envelope>

		// "<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'><env:Header></env:Header><env:Body><ns1:handleTagEventResponse xmlns:ns1=\"http://www.touchatag.com/acs/api/correlation-1.2\"><systemMessage>Tag no. 0x0123456789 is not associated with any action yet.</systemMessage></ns1:handleTagEventResponse></env:Body></env:Envelope>";

		TagEventFeedback feedback = new TagEventFeedback();

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(response));
			int eventType = xpp.getEventType();

			boolean inTagEventResponse = false;
			boolean inSystemMessage = false;
			String applicationResponseIdentifier = null;
			String applicationResponseXml = "";

			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {
				} else if (eventType == XmlPullParser.END_DOCUMENT) {
				} else if (eventType == XmlPullParser.START_TAG) {
					String startTag = xpp.getName();
					if (TAG_HANDLE_TAG_EVENT_RESPONSE.equalsIgnoreCase(startTag)) {
						inTagEventResponse = true;
					} else if (inTagEventResponse && !inSystemMessage && TAG_SYSTEM_MESSAGE.equalsIgnoreCase(startTag)) {
						inSystemMessage = true;
					} else if (inTagEventResponse && TAG_APPLICATION_RESPONSE.equalsIgnoreCase(startTag)) {
						ApplicationResponse appResponse = ApplicationResponseDeserializer.deserialize(xpp);
						if(appResponse != null){
							feedback.getApplicationResponses().put(appResponse.getIdentifier(), appResponse);
						}
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					if (inTagEventResponse && TAG_HANDLE_TAG_EVENT_RESPONSE.equalsIgnoreCase(xpp.getName())) {
						inTagEventResponse = false;
					} else if (inSystemMessage && TAG_HANDLE_TAG_EVENT_RESPONSE.equalsIgnoreCase(xpp.getName())) {
						inSystemMessage = false;
					} 
				} else if (eventType == XmlPullParser.TEXT) {
					if (inSystemMessage) {
						feedback.setSystemMessage(xpp.getText());
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
		return feedback;
	}

}
