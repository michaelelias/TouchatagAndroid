package com.touchatag.mws.api.client.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.touchatag.mws.api.client.model.Affiliate;
import com.touchatag.mws.api.client.model.Email;
import com.touchatag.mws.api.client.model.PhoneNumber;

public class AffiliateAdapterTest {

	@Test
	public void testToXml() throws Exception{
		Affiliate aff = new Affiliate();
		aff.setId("id");
		aff.setIdentityId("identityId");
		aff.setUserName("affiliateUsername");
		aff.setDisplayName("affiliateDisplayName");
		aff.setOrganizationId("orgId");
		
		Email email = new Email();
		email.setEmail("aff@mws.com");
		email.setVerified(false);
		
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.setPhoneNumber("123456");
		phoneNumber.setVerified(true);
		
		aff.setEmail(email);
		
		String xml = AffiliateAdapter.toXml(aff);
		
		System.out.println(xml);
	}
	
	@Test
	public void testFromXml() throws Exception{
		InputStream is = AffiliateAdapterTest.class.getResourceAsStream("Affiliate.xml");

		List<String> lines = IOUtils.readLines(is);
		String affXml = lines.get(0);
		
		Affiliate aff = AffiliateAdapter.fromXml(affXml);
		
		System.out.println(aff);
	}
	
}
