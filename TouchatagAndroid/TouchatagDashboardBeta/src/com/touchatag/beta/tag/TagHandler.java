package com.touchatag.beta.tag;

import android.nfc.Tag;

public abstract class TagHandler {

	protected Tag tag;

	public TagHandler(Tag tag) {
		this.tag = tag;
	}

	public abstract boolean isTagInRange();

	public byte[] getTagUID() {
		return tag.getId();
	}
	
	public abstract void release();

}
