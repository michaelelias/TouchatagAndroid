package com.touchatag.android.correlation.api.v1_2.command;

import org.junit.Test;

import com.touchatag.android.correlation.api.v1_2.command.TagEventCommand;
import com.touchatag.android.correlation.api.v1_2.model.ClientId;
import com.touchatag.android.correlation.api.v1_2.model.GenericTagType;
import com.touchatag.android.correlation.api.v1_2.model.ReaderId;
import com.touchatag.android.correlation.api.v1_2.model.TagEvent;
import com.touchatag.android.correlation.api.v1_2.model.TagEventType;
import com.touchatag.android.correlation.api.v1_2.model.TagId;
import com.touchatag.android.correlation.api.v1_2.model.TagInfo;

public class TagEventCommandTest {

	@Test
	public void testSerialization(){
		TagEvent tagEvent = new TagEvent();
		tagEvent.setTagEventType(TagEventType.TOUCH);
		tagEvent.setClientId(new ClientId("0x12345"));
		tagEvent.setReaderId(new ReaderId("0x9999", "iwe97823djo2djo2"));
		tagEvent.setActionTag(new TagInfo(new TagId("0x11111", GenericTagType.RFID_ISO14443_A_MIFARE_ULTRALIGHT)));
		
		TagEventCommand command = new TagEventCommand(tagEvent, "", "");
		
		System.out.println(command.serializeRequest(tagEvent));
	}
	
}
