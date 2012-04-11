package com.touchatag.mws.api.client.model;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Profile {

	@ElementList(required=false, inline=true)
	List<Field> fields;
}


