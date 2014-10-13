package me.montgome.matasano.oracles;

import java.util.Set;

import me.montgome.matasano.Bytes;
import me.montgome.matasano.WrappedBytes;

import com.google.common.collect.Iterables;

public class Oracles {
    public static int getPrefixLength(Oracle o) {
        int blockSize = getBlockSize(o);

        int i = 0;
        byte[] encrypted = null;
        WrappedBytes collision = null;

        for (i = 1; i < 1024; i++) {
            byte[] plaintext = Bytes.repeat((byte) 65, i);
            encrypted = o.encrypt(plaintext);
            Set<WrappedBytes> collisions = Bytes.getCollisions(Bytes.split(encrypted, blockSize));
            if (!collisions.isEmpty()) {
                collision = Iterables.getOnlyElement(collisions);
                break;
            }
        }

        if (collision == null) {
            throw new RuntimeException("Unable to determine prefix length");
        }

        byte[][] blocks = Bytes.split(encrypted, blockSize);
        for (int j = 0; j < blocks.length; j++) {
            if (collision.equals(new WrappedBytes(blocks[j]))) {
                int offset = j * blockSize;
                return offset - (i % blockSize);
            }
        }

        throw new RuntimeException("Unable to determine prefix length");
    }

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
