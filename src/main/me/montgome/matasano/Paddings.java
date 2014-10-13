package me.montgome.matasano;

public class Paddings {
    public static byte[] addPkcs7(byte[] plaintext, int blocklength) {
        if (plaintext.length % blocklength == 0) {
            byte[] padded = new byte[plaintext.length + blocklength];
            System.arraycopy(plaintext, 0, padded, 0, plaintext.length);
            System.arraycopy(Bytes.repeat(blocklength, blocklength), 0, padded, padded.length - blocklength, blocklength);
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
        int padding = 0xFF & plaintext[plaintext.length - 1];

        if (padding > plaintext.length) {
            throw new PaddingException("Padding too long!");
        }

        for (int i = plaintext.length - padding; i < plaintext.length - 1; i++) {
            if (plaintext[i] != padding) {
                throw new PaddingException("Malformed padding");
            }
        }

        byte[] original = new byte[plaintext.length - padding];
        System.arraycopy(plaintext, 0, original, 0, original.length);
        return original;
    }
}
