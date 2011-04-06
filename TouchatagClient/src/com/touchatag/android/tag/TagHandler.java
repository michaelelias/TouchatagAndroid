package com.touchatag.android.tag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
