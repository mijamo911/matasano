package me.montgome.matasano;

import java.util.Arrays;

public class Cracker {
    public static byte[] getXorKey(byte[] ciphertext, int keysize) {
        byte[] key = new byte[keysize];

        byte[][] transposed = Bytes.transpose(ciphertext, key.length);
        for (int i = 0; i < key.length; i++) {
            ScoredPlaintext candidate = Cracker.singleByteXor(transposed[i]);
            key[i] = candidate.key[0];
        }

        return key;
    }

    public static ScoredPlaintext singleByteXor(byte[] ciphertext) {
        ScoredPlaintext[] results = new ScoredPlaintext[256];

        for (int i = 0; i < 256; i++) {
            byte[] key = Bytes.repeat((byte) i, ciphertext.length);
            String plaintext = Strings.newString(Bytes.xor(key, ciphertext));
            double score = Scorer.score(plaintext);
            results[i] = new ScoredPlaintext(score, plaintext, new byte[] {(byte) i});
        }

        Arrays.sort(results);
        return results[255];
    }

    public static ScoredKeysize[] keysize(byte[] bytes, int minLength, int maxLength, int topN) {
        ScoredKeysize[] results = new ScoredKeysize[maxLength - minLength + 1];

        for (int i = minLength; i <= maxLength; i++) {
            byte[] first = Arrays.copyOfRange(bytes, 0, i);
            byte[] second = Arrays.copyOfRange(bytes, i, 2*i);
            int distance = Bytes.hamming(first, second);
            double score = (double) distance / (double) i;
            results[i - minLength] = new ScoredKeysize(score, i);
        }

        Arrays.sort(results);
        return Arrays.copyOfRange(results, 0, topN);
    }

    public static byte[] repeatedKeyXor(byte[] plaintext, byte[] key) {
        return Bytes.xor(plaintext, Bytes.repeat(key, plaintext.length));
    }
}
