package com.touchatag.android.util;

public class ByteUtils {
    
    public static byte toByte(int byteValue) {
        return (byte) (byteValue & 0x000000FF);
    }
    
    public static int toInt(byte byteValue) {
        return byteValue & 0x000000FF;
    }
    
    public static byte[] toBytes(int... byteValue) {
        byte[] bytes = new byte[byteValue.length];
        for (int i=0; i < bytes.length; i++) {
            bytes[i] = toByte(byteValue[i]);
        }
        return bytes;
    }
    
    public static int[] toInts(byte... bytes) {
        int[] ints = new int[bytes.length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = toInt(bytes[i]);
        }
        return ints;
    }
    
    public static int[] concat(int[] base, int... extension) {
        int[] compound = new int[base.length+extension.length];
        System.arraycopy(base, 0, compound, 0, base.length);
        System.arraycopy(extension, 0, compound, base.length, extension.length);
        return compound;
    }
    
    public static byte[] concat(byte[] base, byte... extension) {
        byte[] compound = new byte[base.length+extension.length];
        System.arraycopy(base, 0, compound, 0, base.length);
        System.arraycopy(extension, 0, compound, base.length, extension.length);
        return compound;
    }
    
    /**
     * Moves all bytes one up, meaning the left most byte (0) is swapped to the end
     */
    public static byte[] rotateLeft(byte[] input) {
        byte[] cycled = new byte[input.length];
        System.arraycopy(input, 1, cycled, 0, input.length - 1);
        cycled[cycled.length - 1] = input[0];
        return cycled;
    }
    
    /**
     * Moves all bytes one down, meaning the right most byte (length-1) is swapped to the beginning
     */
    public static byte[] rotateRight(byte[] input) {
        byte[] cycled = new byte[input.length];
        System.arraycopy(input, 0, cycled, 1, input.length - 1);
        cycled[0] = input[input.length - 1];
        return cycled;
    }
    
    
    
    public static byte[] xor(byte[] data1, byte[] data2) {
        byte[] xored = new byte[data1.length];
        for (int i = 0; i < xored.length; i++) {
            xored[i] = (byte) (data1[i] ^ data2[i]);
        }
        return xored;
    }
    
    /**
     * Performs logical negation on each bit, forming the ones' complement of the given binary value. 
     * Digits which were 0 become 1, and vice versa
     * @param data
     * @return
     */
    public static byte[] not(byte[] data){
        byte[] complement = new byte[data.length];
        for (int i = 0; i < complement.length; i++) {
            complement[i] = (byte)~data[i];
        }
        return complement;
    }
    
    public static byte[] bitShiftLeft(byte[] data) {
        byte[] shifted = new byte[data.length];
        int carry = 0;
        for (int i=data.length-1; i >= 0; i--) {
            int newCarry = getMostSignificantBit(data[i]);
            shifted[i] = (byte) (data[i] << 1);
            shifted[i] |= carry;
            carry = newCarry;
        }
        return shifted;
    }
    
    public static int getMostSignificantBit(byte b) {
        return b >> 7 & 0x1;
    }
    
    public interface EndianSpecific {
        byte[] asThreeByte(int value);
        byte[] asBytes(long value, int nrOfBytes);
        int asInt(byte... intBytes);
        byte getMostSignificantByte(byte... value);
        int getMostSignificantBit(byte... value);
    }
    
    public interface ByteOrderer {
        int getShiftMultiplier(int byteNr, int length);
        int getMostSignificantByteIndex(int length);
    }
    
    public enum ByteOrdering implements ByteOrderer, EndianSpecific {
        BIG_ENDIAN {
            public int getShiftMultiplier(int byteNr, int length) {
                return length-1-byteNr;
            }
            
            public int getMostSignificantByteIndex(int length) {
                return 0;
            }
        },
        LITTLE_ENDIAN {
            public int getShiftMultiplier(int byteNr, int length) {
                return byteNr;
            }
            
            public int getMostSignificantByteIndex(int length) {
                return length-1;
            }
        };
        
        public byte[] asThreeByte(int value) {
            return asBytes(value, 3);
        }
        
        public byte[] asBytes(long value, int nrOfBytes) {
            byte[] bytes = new byte[nrOfBytes];
            for (int i=0; i < bytes.length; i++) {
                int shift = getShiftMultiplier(i, bytes.length)*8;
                bytes[i] = (byte) ((value & (0x000000FF << shift)) >> shift) ;
            }
            return bytes;
        }
        
        public int asInt(byte... intBytes) {
            int value = 0x00;
            if (intBytes.length > 4) {
                throw new IllegalArgumentException("Input byte array exceeds int size limit");
            }
            
            for (int i=0; i < intBytes.length; i++) {
                int shift = getShiftMultiplier(i, intBytes.length);
                value |= toInt(intBytes[i]) << (shift*8);
            }
            return value;
        }
        
        public byte getMostSignificantByte(byte... value) {
            return value[getMostSignificantByteIndex(value.length)];
        }
        
        public int getMostSignificantBit(byte... value) {
            byte mostSignificantByte = getMostSignificantByte(value);
            return ByteUtils.getMostSignificantBit(mostSignificantByte);
        }
    }
    
    public static EndianSpecific bigEndian() {
        return ByteOrdering.BIG_ENDIAN;
    }

    public static EndianSpecific littleEndian() {
        return ByteOrdering.LITTLE_ENDIAN;
    }
}
