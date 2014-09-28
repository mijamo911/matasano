package me.montgome.matasano.oracles;

import me.montgome.matasano.Bytes;

public class Oracles {
    public static int getBlockSize(Oracle o) {
        int initialLength = o.encrypt(new byte[0]).length;
        Integer firstChange = null;

        for (int i = 1; i < 1024; i++) {
            byte[] b = Bytes.repeat((byte) 65, i);
            int length = o.encrypt(b).length;
            if (firstChange == null) {
                if (length != initialLength) {
                    firstChange = length;
                }
            } else if (length != firstChange) {
                return length - firstChange;
            }
        }

        throw new RuntimeException("Unable to determine block size");
    }

    public static int getInitialPadding(Oracle o) {
        int initialLength = o.encrypt(new byte[0]).length;
        Integer firstChange = null;
        Integer firstChangeIndex = null;

        for (int i = 1; i < 1024; i++) {
            byte[] b = Bytes.repeat((byte) 65, i);
            int length = o.encrypt(b).length;
            if (firstChange == null) {
                if (length != initialLength) {
                    firstChange = length;
                    firstChangeIndex = i;
                }
            } else if (length != firstChange) {
                return firstChangeIndex % (length - firstChange);
            }
        }

        throw new RuntimeException("Unable to determine initial padding");
    }

    public static boolean isEcb(Oracle o) {
        int blockSize = getBlockSize(o);
        byte[] plaintext = Bytes.repeat(65, blockSize * 3);
        byte[] ciphertext = o.encrypt(plaintext);
        int collisions = Bytes.countCollisions(Bytes.split(ciphertext, blockSize));
        return collisions > 0;
    }

    public static boolean isEcb(byte[] ciphertext, int blockSize) {
        return Bytes.countCollisions(Bytes.split(ciphertext, blockSize)) > 0;
    }
}
