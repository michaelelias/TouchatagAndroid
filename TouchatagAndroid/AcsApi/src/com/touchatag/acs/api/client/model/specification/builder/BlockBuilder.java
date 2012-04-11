package com.touchatag.acs.api.client.model.specification.builder;

import com.touchatag.acs.api.client.model.specification.Block;
import com.touchatag.acs.api.client.model.specification.Property;
import com.touchatag.acs.api.client.model.specification.PropertyType;

public class BlockBuilder {

	private Block block;

	public BlockBuilder(String id, String ref) {
		block = new Block();
		block.id = id;
		block.ref = ref;
	}

	public BlockBuilder addProperty(PropertyType type, String value) {
		Property prop = new Property(type);
		switch (type) {
		case TEXT:
			prop.setText(value);
			break;
		case URI:
			prop.setUri(value);
			break;
		case DELEGATE:
			prop.setAs(value);
			break;
		}
		return this;
	}

	
	
}
