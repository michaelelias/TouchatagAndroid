package com.touchatag.beta.tag.mifare;

import com.touchatag.beta.util.ByteUtils;

public enum MifareAuthenticationKey {
	
	DEFAULT(0xD3, 0xF7, 0xD3, 0xF7, 0xD3, 0xF7), //
	MANUFACTURER_1(0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF), //
	MANUFACTURER_2(0xA0, 0xA1, 0xA2, 0xA3, 0xA4, 0xA5);

	private final byte[] key;

	private MifareAuthenticationKey(int... byteValue) {
		key = ByteUtils.toBytes(byteValue);
	}

	public byte[] getBytes() {
		return key;
	}
}