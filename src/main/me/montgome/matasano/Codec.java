package me.montgome.matasano;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Codec {
    private static final String BASE_16_TABLE = "0123456789abcdef";
    static final String BASE_64_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    
    private static final Map<Character,Byte> BASE_16_INVERTED = invert(BASE_16_TABLE);
    private static final Map<Character,Byte> BASE_64_INVERTED = invert(BASE_64_TABLE);

    public static byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];

        for (int i = 0; i < hex.length(); i += 2) {
            char first = hex.charAt(i);
            char second = hex.charAt(i + 1);

            int high = hexMap(first);
            int low = hexMap(second);

            int value = (high << 4) | low;
            
            bytes[i / 2] = (byte) value;
        }

        return bytes;
    }
    
    private static int hexMap(char c) {
        switch (c) {
        case '0':
            return 0;
        case '1':
            return 1;
        case '2':
            return 2;
        case '3':
            return 3;
        case '4':
            return 4;
        case '5':
            return 5;
        case '6':
            return 6;
        case '7':
            return 7;
        case '8':
            return 8;
        case '9':
            return 9;
        case 'a':
            return 10;
        case 'b':
            return 11;
        case 'c':
            return 12;
        case 'd':
            return 13;
        case 'e':
            return 14;
        case 'f':
            return 15;
        default:
            throw new RuntimeException("Unknown character " + c);
        }
    }
    
    public static String bytesToHex(byte[] bs) {
        StringBuilder s = new StringBuilder();
        for (byte b : bs) {
            int high = ((b & 0xFF) >>> 4);
            int low = ((b & 0xFF) & ((1 << 4) - 1));
            
            s.append(BASE_16_TABLE.charAt(high));
            s.append(BASE_16_TABLE.charAt(low));
        }
        return s.toString();
    }

    public static String bytesToBase64(byte[] bytes) {
        System.out.println(Bytes.toBinaryString(bytes));
        
        StringBuilder b = new StringBuilder();

        for (int i = 0; i < (bytes.length * 8 / 6); i++) {
            int bits = Bytes.bits(bytes, i * 6, (i + 1) * 6);
            b.append(BASE_64_TABLE.charAt(bits));
        }
        
        return b.toString();
    }
    
    public static byte[] base64ToBytes(String base64) {
        Bits bits = new Bits();
        for (int i = 0; i < base64.length(); i++) {
            if (base64.charAt(i) != '=') {
                bits.putBits(i * 6, (byte) (BASE_64_INVERTED.get(base64.charAt(i))), 6);
            }
        }
        return bits.getBytes();
    }
    
    public static byte base64ToByte(char c) {
        for (int i = 0; i < BASE_64_TABLE.length(); i++) {
            if (BASE_64_TABLE.charAt(i) == c) {
                return (byte) i;
            }
        }
        throw new IllegalArgumentException("Unknown base64 character " + c);
    }
    
    private static Map<Character, Byte> invert(String s) {
        Map<Character, Byte> inverted = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            inverted.put(s.charAt(i), (byte) i);
        }
        return inverted;
    }
    
    private Codec() {}
}
