package com.touchatag.android.tag.mifare.ultralight;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.util.Log;

import com.touchatag.android.tag.TagHandler;
import com.touchatag.android.tag.mifare.MiFareCommandFactory;
import com.touchatag.android.util.HexFormatter;

public class MifareUltralightTagHandler extends TagHandler {

	private static final String TAG = MifareUltralightTagHandler.class.getSimpleName();
	
	private static final int LOCK_LENGTH = 2;
	private static final int USERDATA_LENGTH = 48;
	private static final int PAGE_LENGTH = 4;

	MifareUltralight mifareUltralight;
	
	public MifareUltralightTagHandler(Tag tag) {
		super(tag);
		mifareUltralight = MifareUltralight.get(tag);
	}

	public byte[] readAll() throws CouldNotReadTagDataException {
		try {
			mifareUltralight.connect();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			for (int page16 = 0; page16 < 4; page16++) {
				int blockA = page16 * 4;
				byte[] responseBytes = mifareUltralight.transceive(MiFareCommandFactory.readTagForBlock(blockA).toBytes());
				Log.d(TAG, "Read page16=" + page16 + " : " + HexFormatter.toHexString(responseBytes));
				// Extract the relevant part
				output.write(responseBytes, 0, responseBytes.length);
			}
			return output.toByteArray();
		} catch (IOException e) {
			throw new CouldNotReadTagDataException(e);
		}
	}

	@Override
	public boolean isTagInRange() {
		return mifareUltralight.isConnected();
	}

	@Override
	public void release() {
	}

}
