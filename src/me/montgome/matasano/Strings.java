package me.montgome.matasano;

import java.io.UnsupportedEncodingException;

public class Strings {
    private static final String UTF8 = "UTF-8";
    
    public static byte[] getBytes(String s) {
        try {
            return s.getBytes(UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String newString(byte[] b) {
        try {
            return new String(b, UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    
    public static int hamming(String x, String y) {
        return Bytes.hamming(getBytes(x), getBytes(y));
    }
}
