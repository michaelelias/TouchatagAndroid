package com.touchatag.beta.util;

import java.io.UnsupportedEncodingException;
import java.util.Formatter;

/**
 * Helper class to work with hex representations
 */
public class HexFormatter {

    public static String toHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            // look up high nibble char
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);

            // look up low nibble char
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    // table to convert a nibble to a hex char.
    static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * Convert a hex string to a byte array. Permits upper or lower case hex.
     * 
     * @param s
     *            String must be formed only of digits 0-9 A-F or a-f. No spaces, minus or plus signs.
     * @return corresponding byte array.
     */
    public static byte[] fromHexString(String s) {
        int stringLength = s.length();
        if ((stringLength & 0x1) != 0) {
            // throw new IllegalArgumentException("fromHexString requires an even number of hex characters");
            // Lets assume leading zeros.
            s = "0" + s;
            stringLength = s.length();
        }
        byte[] b = new byte[stringLength / 2];

        for (int i = 0, j = 0; i < stringLength; i += 2, j++) {
            int high = charToNibble(s.charAt(i));
            int low = charToNibble(s.charAt(i + 1));
            b[j] = (byte) ((high << 4) | low);
        }
        return b;
    }

    private static int charToNibble(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        } else if ('a' <= c && c <= 'f') {
            return c - 'a' + 0xa;
        } else if ('A' <= c && c <= 'F') {
            return c - 'A' + 0xa;
        } else {
            throw new IllegalArgumentException("Invalid hex character: " + c);
        }
    }

    public static String toHexString(int i) {
        String response = Integer.toHexString(i);
        if (response.length() == 1) {
            response = "0" + response.toUpperCase();
        }
        return response;
    }
    
    /**
     * Will split up the hexString into sequences of 4 bytes with a memory page offset in front
     * @param string
     * @return
     */
    public static String pageView(String hexString) {
        StringBuilder pageView = new StringBuilder();
        Formatter offsetFormat = new Formatter(pageView);
        
        if (hexString.length()%8 != 0) {
            hexString += addPadding(hexString.length()%8);
        }
        int pages = hexString.length()/8;
        for (int page=0; page<pages; page++) {
            String pageContent = hexString.substring(page*8, page*8+8);
            String byte0 = pageContent.substring(0, 2);
            String byte1 = pageContent.substring(2, 4);
            String byte2 = pageContent.substring(4, 6);
            String byte3 = pageContent.substring(6, 8);
            
            offsetFormat.format("Page %04X : %2s:%2s:%2s:%2s     %s\n", 
                    page,
                    byte0,
                    byte1,
                    byte2,
                    byte3,
                    utf8(HexFormatter.fromHexString( pageContent.replaceAll("-", "0") ) )
                    );
        }
        
        return pageView.toString();
    }

    /**
     * Interpretes a byte array as if they contained UTF-8 encoded characters
     * @param data
     * @return
     */
    public static String utf8(byte[] data) {        
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // We really cannot do much about this, but it SHOULD always be supported
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Exports the string as a UTF-8 encoded byte array
     * @param string
     * @return
     */
    public static byte[] utf8(String string) {
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // We really cannot do much about this, but it SHOULD always be supported
            throw new RuntimeException(e);
        }
    }

    private static String addPadding(int requiredPadding) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i < requiredPadding; i++) {
            sb.append("-");
        }
        return sb.toString();
    }

    public static String convertToUnicodeString(String hexString) {
        return utf8(HexFormatter.fromHexString(hexString));
        
//        StringBuffer output = new StringBuffer();
//        String subStr = null;
//        for (int i = 0; i < hexString.length(); i = i + 2) {
//            subStr = hexString.substring(i, i + 2);
//            char c = (char) Integer.parseInt(subStr, 16);
//            output.append(c);
//        }
//        return output.toString();
    }
    
    public static String convertByteToUnicodeString(byte[] b) {
        return convertToUnicodeString(HexFormatter.toHexString(b));
    }
}
