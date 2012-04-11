package com.touchatag.beta.activity.template;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Map;

import com.touchatag.beta.activity.connections.Connection;

public class JavascriptFactory {

	private static final String TEMPLATE_APP_RESPONSE_IDENTIFIER = "urn:com.touchatag:android-client-action";

	private static String METADATA_PROCESSOR;

	static {
		StringBuilder sb = new StringBuilder();

		sb.append("importPackage(java.lang);");
		sb.append("importPackage(java.util);");
		sb.append("importPackage(Packages.com.tikitag.util.xml);");

		sb.append("var metadataProcessor = function() {");
		sb.append("    var _log = new TLogger();");
		sb.append("    var source = new MetadataSource();");
		sb.append("    var xml = source.getMetadata();");
		sb.append("    _log.debug(\"Received Metadata: \n\" + xml);");
		sb.append("    var defaultType = \"any\";");
		sb.append("    return {");
		sb.append("        extractItem : function(provider, label) {");
		sb.append("            try {");
		sb.append("                var xpath = \"//item[type='\" + defaultType + \"' and starts-with(provider, '\" + provider + \"') and label='\" + label + \"']/value\";");
		sb.append("                var item = '' + XmlUtils.xpath(xpath).on(xml).single().asText();");
		sb.append("                return item;");
		sb.append("            } catch (err) {");
		sb.append("                if (err.javaException instanceof com.tikitag.util.xml.XmlException) {");
		sb.append("                    if (err.javaException.message.contains(\"Empty document\")) {");
		sb.append("                     throw new Exception(\"Metadata missing: type=\" + defaultType + \" provider=\" + provider + \" label=\" + label);");
		sb.append("                }");
		sb.append("             }");
		sb.append("             throw err; // Propagate otherwise");
		sb.append("       },");
		sb.append("        findItemByType : function(provider, type) {");
		sb.append("            try {");
		sb.append("                var xpath = \"//item[type='\" + type + \"' and starts-with(provider, '\" + provider + \"')]/value\";");
		sb.append("                var item = '' + XmlUtils.xpath(xpath).on(xml).single().asText();");
		sb.append("                return item;");
		sb.append("            } catch (err) {");
		sb.append("                if (err.javaException instanceof com.tikitag.util.xml.XmlException) {");
		sb.append("                    if (err.javaException.message.contains(\"Empty document\")) {");
		sb.append("                     throw new Exception(\"Metadata missing: type=\" + defaultType + \" provider=\" + provider + \" label=\" + label);");
		sb.append("                }");
		sb.append("            }");
		sb.append("            throw err; // Propagate otherwise");
		sb.append("        }");
		sb.append("    }");
		sb.append("}();");
		
		METADATA_PROCESSOR = sb.toString();

	}

	public static String createScriptWithAppResponseContainingParameters(String templateId, Map<String, String> params) {
		StringBuilder script = new StringBuilder();
		script.append("var identifier = \"" + TEMPLATE_APP_RESPONSE_IDENTIFIER + "\";");

		StringBuilder paramsXml = new StringBuilder();
		paramsXml.append("<parameters>");
		paramsXml.append("<parameter name=\"" + Template.IDENTIFIER + "\">");
		paramsXml.append(templateId);
		paramsXml.append("</parameter>");
		for (Map.Entry<String, String> param : params.entrySet()) {
			paramsXml.append("<parameter name=\"" + param.getKey() + "\">");
			paramsXml.append(param.getValue());
			paramsXml.append("</parameter>");
		}
		paramsXml.append("</parameters>");
		script.append("var params = '" + paramsXml.toString() + "';");
		script.append("var tagEventContext = new TagEventContext();");
		script.append("tagEventContext.addApplicationResponse(identifier, params);");
		return script.toString();
	}

	public static String createFoursquareCheckinScript(String templateId, String venueId, String shout, String latitude, String longitude) {
		StringBuilder script = new StringBuilder();
		
		String checkinUri = "https://api.foursquare.com/v2/checkins/add";
		
		script.append(METADATA_PROCESSOR);
		
		script.append("var log = new TLogger();");
		
		script.append("var metadata = {");
		script.append("	    user4SqToken: metadataProcessor.findItemByType(\"user.client\", \"" + Connection.FOURSQUARE.getMetadataType() + "\");");
		script.append("}");
		
		script.append("var token = metadata.user4SqToken;");
		script.append("var uri = '" + checkinUri + "';");
		script.append("var req = new HTTPRequest();");
		
		script.append("req.setParam('venueId', '" + venueId + "');");
		script.append("req.setParam('shout', '" + shout + "');");
		script.append("req.setParam('broadcast', 'public');");
		script.append("req.setParam('ll', '" + (latitude + "," + longitude) + "');");
		script.append("req.open('POST', uri);");
		script.append("req.send();");
		script.append("log.debug(req.getResponseText());");
		script.append("req.getResponseText();");
		
		return script.toString();
	}

}
