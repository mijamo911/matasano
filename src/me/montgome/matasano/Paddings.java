package me.montgome.matasano;

public class Paddings {
    public static byte[] addPkcs7(byte[] plaintext, int blocklength) {
        if (plaintext.length % blocklength == 0) {
            byte[] padded = new byte[plaintext.length + blocklength];
            System.arraycopy(plaintext, 0, padded, 0, plaintext.length);
            System.arraycopy(Bytes.repeat((byte) blocklength, blocklength), 0, plaintext, plaintext.length - blocklength, blocklength);
            return padded;
        }
        
        byte[] padded = new byte[((plaintext.length + blocklength) / blocklength) * blocklength];
        System.arraycopy(plaintext, 0, padded, 0, plaintext.length);
        
        byte padding = (byte) (padded.length - plaintext.length);
        for (int i = plaintext.length; i < padded.length; i++) {
            padded[i] = padding;
        }
        
        return padded;
    }
    
    public static byte[] removePkcs7(byte[] plaintext) {
        int padding = plaintext[plaintext.length - 1];
        byte[] original = new byte[plaintext.length - padding];
        System.arraycopy(plaintext, 0, original, 0, original.length);
        return original;
    }
}
