package com.touchatag.android.correlation.api.v1_2.model;

import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Root;

@Root
public enum GenericTagType {
	
	RFID_ISO14443_A_MIFARE_ULTRALIGHT((short)(0x00 * 256 + 0x00)),
	BARCODE_2D_QR((short)(0x00 * 256 + 0x01)),
	RFID_ISO14443_A_MIFARE_1K((short)(0x00 * 256 + 0x02)),
	RFID_ISO14443_A_MIFARE_DESFIRE((short)(0x00 * 256 + 0x03)),
	RFID_ISO14443_A_TOPAZ((short)(0x00 * 256 + 0x04)),
	
	RFID_GENERIC((short)(0x0F * 256 + 0x00)),
	RFID_ISO14443_A_MIFARE_GENERIC((short)(0x0F * 256 + 0x01));
	
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
