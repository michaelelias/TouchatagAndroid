package com.touchatag.beta.client.soap.model.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum GenericTagType implements Serializable{
	
	RFID_ISO14443_A_MIFARE_ULTRALIGHT((short) (0x00 * 256 + 0x00)), //
	BARCODE_2D_QR((short) (0x00 * 256 + 0x01)), //
	RFID_ISO14443_A_MIFARE_1K((short) (0x00 * 256 + 0x02)), //
	RFID_ISO14443_A_MIFARE_DESFIRE((short) (0x00 * 256 + 0x03)), //
	RFID_ISO14443_A_TOPAZ((short) (0x00 * 256 + 0x04)), //
	RFID_GENERIC((short) (0x0F * 256 + 0x00)), //
	RFID_ISO14443_A_MIFARE_GENERIC((short) (0x0F * 256 + 0x01));

	private short code;

	private GenericTagType(short code) {
		this.code = code;
		Code.map(code, this);
	}

	public short getCode() {
		return code;
	}

	public boolean isRFID() {
		return name().startsWith("RFID");
	}

	public boolean isBarcode() {
		return name().startsWith("BARCODE");
	}

	public static final class Code {

		private static Map<Short, GenericTagType> lookUpMap = new HashMap<Short, GenericTagType>();

		public static void map(short code, GenericTagType type) {
			lookUpMap.put(code, type);
		}

		public static GenericTagType resolve(short code) {
			return lookUpMap.get(code);
		}

	}
}
