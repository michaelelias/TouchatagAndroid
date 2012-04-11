package com.touchatag.beta.tag.mifare;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.touchatag.beta.tag.command.Command;
import com.touchatag.beta.tag.command.SimpleCommand;
import com.touchatag.beta.util.HexFormatter;

public class MiFareCommandFactory {

	public static enum AuthKey {
		A, B
	}

	private static final Logger LOG = Logger.getLogger(MiFareCommandFactory.class.getName());

	public static Command readTagForBlock(int blockAddress) {
		return new SimpleCommand(0x30).add(blockAddress);
	}

	public static Command authenticate(AuthKey selectedKey, int blockAddress, byte[] key, byte[] uid) {
		SimpleCommand command;
		switch (selectedKey) {
		case B:
			command = new SimpleCommand(0x61);
			break;
		case A:
		default:
			command = new SimpleCommand(0x60);
		}
		command.add(blockAddress).add(key).add(uid);
		return command;
	}

	/*
	 * Mifare Ultralight/Classic support
	 */
	public static Command writeTagForBlock(int blockAddress, byte[] data) {
		if (data.length == 4) {
			// Ultralight format
			return new SimpleCommand(0xA2).add(blockAddress).add(data);
		} else if (data.length == 16) {
			// Mifare format
			return new SimpleCommand(0xA0).add(blockAddress).add(data);
		} else {
			throw new IllegalArgumentException("Data must be either 4 or 16 bytes in length");
		}
	}

	/* TODO Bert: Verify that this method still does something sensible... There was some strange string manipulation here...
	 * 
	 * Writes an ascii string in hexadecimal byte form to a tag
	 * to refactor!
	 */
	public static ArrayList<Command> writeContentForTag(int target, String content) {
		ArrayList<Command> commandArray = new ArrayList<Command>();
		//String hexaString = HexFormatter.encodeHexString(content);
		int addr = 1;
		for (int i = 4; i <= 15; i++) {
			String str = content.substring(addr, addr + 7);
			LOG.info("writing ..." + str);
			commandArray.add(writeTagForBlock(i, HexFormatter.fromHexString(str)));
			addr = addr + 8;
		}
		return commandArray;
	}
}
