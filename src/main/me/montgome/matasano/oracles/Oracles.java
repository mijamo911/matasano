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

    public static boolean isEcb(byte[] ciphertext, int blockSize) {
        return Bytes.collisions(Bytes.split(ciphertext, blockSize)) > 0;
    }
}
