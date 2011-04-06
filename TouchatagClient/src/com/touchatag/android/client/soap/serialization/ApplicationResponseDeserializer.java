package com.touchatag.android.client.soap.serialization;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.touchatag.android.client.soap.model.response.ApplicationResponse;
import com.touchatag.android.client.soap.model.response.ClientAction;
import com.touchatag.android.client.soap.model.response.Container;
import com.touchatag.android.client.soap.model.response.LegacyClientActionResponse;

public class ApplicationResponseDeserializer {

	private static final String TAG_APPLICATION_RESPONSE = "applicationResponse";
	private static final String ATTR_APPLICATION_RESPONSE_IDENTIFIER = "identifier";
	
	private static final String TAG_CLIENT_ACTION = "clientaction";
	private static final String TAG_CONTAINER = "container";
	private static final String TAG_ATTRIBUTE = "attribute";
	private static final String TAG_STRING = "string";
	private static final String ATTR_NAME = "name";
	
	public enum ApplicationResponseType {
		
		LEGACY_CLIENT_ACTION("urn:com.touchatag:legacy-client-action") {
			@Override
			public ApplicationResponse parse(XmlPullParser xpp) {
				try {
					LegacyClientActionResponse appResponse = new LegacyClientActionResponse();
					appResponse.setIdentifier(xpp.getAttributeValue(0));
					boolean inClientAction = false;
					boolean inContainerOne = false;
					boolean inContainerTwo = false;
					boolean inAttribute = false;
					String inAttributeName = null;
					boolean inString = false;
					while (!isAtApplicationResponseEndTag(xpp)) {
						int eventType = xpp.next();
						String tagName = xpp.getName();
						if(eventType == XmlPullParser.START_TAG){
							if(TAG_CLIENT_ACTION.equalsIgnoreCase(tagName)){
								inClientAction = true;
								appResponse.setClientAction(new ClientAction());
							} else if(TAG_CONTAINER.equalsIgnoreCase(tagName)){
								if(inContainerOne){
									inContainerTwo = true;
									Container con = new Container();
									con.setName(xpp.getAttributeValue(0));
									appResponse.getClientAction().getContainer().setContainer(con);
								} else {
									inContainerOne = true;
									Container con = new Container();
									con.setName(xpp.getAttributeValue(0));
									appResponse.getClientAction().setContainer(con);
								}
							} else if(TAG_ATTRIBUTE.equalsIgnoreCase(tagName)){
								inAttribute = true;
								inAttributeName = xpp.getAttributeValue(0);
								if(inContainerTwo){
									appResponse.getClientAction().getContainer().getContainer().getAttributes().put(inAttributeName, "");
								}
							} else if(TAG_STRING.equalsIgnoreCase(tagName)){
								inString = true;
							} 
						}
						else if(eventType == XmlPullParser.END_TAG){
							if(TAG_CLIENT_ACTION.equalsIgnoreCase(tagName)){
								inClientAction = false;
							} else if(TAG_CONTAINER.equalsIgnoreCase(tagName)){
								if(inContainerTwo){
									inContainerTwo = false;
								} else {
									inContainerOne = false;
								}
							} else if(TAG_ATTRIBUTE.equalsIgnoreCase(tagName)){
								inAttribute = false;
							} else if(TAG_STRING.equalsIgnoreCase(tagName)){
								inString = false;
							} 
						} else if(eventType == XmlPullParser.TEXT){
							if(inContainerTwo && inAttribute && inString){
								appResponse.getClientAction().getContainer().getContainer().getAttributes().put(inAttributeName, xpp.getText());
							}
						}
					}
					return appResponse;
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		};
//		<env:Envelope xmlns:env='http://schemas.xmlsoap.org/soap/envelope/'><env:Header></env:Header><env:Body><ns1:handleTagEventResponse xmlns:ns1="http://www.touchatag.com/acs/api/correlation-1.2">
//		<applicationResponse identifier="urn:com.touchatag:legacy-client-action">
//		<ClientAction>
//	    <container name="tikitag.standard.url">
//	        <container name="v1.0">
//	            <attribute name="url">
//	                <string>http://m.foursquare.com/venue/1450372</string>
//	            </attribute>
//	        </container>
//	    </container>
//	</ClientAction></applicationResponse></ns1:handleTagEventResponse></env:Body></env:Envelope>
		
		;
		
		
		
		private static boolean isAtApplicationResponseEndTag(XmlPullParser xpp) throws XmlPullParserException{
			return xpp.getEventType() == XmlPullParser.END_TAG && TAG_APPLICATION_RESPONSE.equalsIgnoreCase(xpp.getName());
		}
		
		private String identifier;
		
		private ApplicationResponseType(String identifier){
			this.identifier = identifier;
		}
		
		public String getIdentifier(){
			return identifier;
		}
		
		public static ApplicationResponseType resolveByIdentifier(String identifier){
			for(ApplicationResponseType type : ApplicationResponseType.values()){
				if(type.getIdentifier().equalsIgnoreCase(identifier)){
					return type;
				}
			}
			return null;
		}
		
		public abstract ApplicationResponse parse(XmlPullParser xpp);
		
	}
	
	public static ApplicationResponse deserialize(XmlPullParser xpp) {
		if(!TAG_APPLICATION_RESPONSE.equalsIgnoreCase(xpp.getName())){
			throw new RuntimeException("The given XmlPullParser must be in a state where it just detected the applicationResponse");
		}
		if(!ATTR_APPLICATION_RESPONSE_IDENTIFIER.equalsIgnoreCase(xpp.getAttributeName(0))){
			throw new RuntimeException("applicationResponse tag is supposed to have an identifier attribute");
		}
		String identifier = xpp.getAttributeValue(0);
		ApplicationResponseType type = ApplicationResponseType.resolveByIdentifier(identifier);
		return type.parse(xpp);
	}
	
}
