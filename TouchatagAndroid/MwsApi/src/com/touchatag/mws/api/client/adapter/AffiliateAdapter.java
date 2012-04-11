package com.touchatag.mws.api.client.adapter;

import java.io.StringWriter;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.touchatag.mws.api.client.model.Affiliate;

public class AffiliateAdapter {

	public static String toXml(Affiliate affiliate) throws Exception{
		Serializer serializer = new Persister();
		StringWriter writer = new StringWriter();
		serializer.write(affiliate, writer);
		return writer.toString();
	}
	
	public static Affiliate fromXml(String xml) throws Exception{
		Serializer serializer = new Persister();
		return serializer.read(Affiliate.class, xml);
	}
}
