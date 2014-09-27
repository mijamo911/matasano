package me.montgome.matasano;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Bytes {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static int bits(byte[] bytes, int start, int end) {
        int startByte = start / 8;
        int endByte = (end - 1) / 8;
        int bits = end - start;

        if (endByte == startByte) {
            int value = bytes[startByte];
            value = value >> ((8 - (end % 8)) % 8);
            int mask = (1 << bits) - 1;
            value &= mask;
            return value;
        } else {
            int high = bytes[startByte];
            int low = bytes[endByte];

            System.out.println(toBinaryString((byte) high)
                + toBinaryString((byte) low));

            int lowBits = end % 8;
            int highBits = bits - lowBits;

            int mask = (1 << highBits) - 1;
            System.out.println(toBinaryString((byte) mask));

            int highValue = (high & mask) << lowBits;
            int lowValue = (low >>> (8 - lowBits));

            System.out.println(toBinaryString((byte) highValue));
            System.out.println(toBinaryString((byte) lowValue));

            int value = highValue | lowValue;
            System.out.println(toBinaryString((byte) value));

            return value;
        }
    }

    public static String toBinaryString(byte b) {
        StringBuilder s = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            if ((b & (1 << i)) != 0) {
                s.append("1");
            } else {
                s.append("0");
            }
        }
        return s.toString();
    }

    public static String toBinaryString(byte[] bs) {
        StringBuilder s = new StringBuilder();
        for (byte b : bs) {
            s.append(toBinaryString(b));
        }
        return s.toString();
    }

    public static byte[] bytes(int... is) {
        byte[] b = new byte[is.length];
        for (int i = 0; i < is.length; i++) {
            b[i] = (byte) is[i];
        }
        return b;
    }

    public static byte xor(byte a, byte b) {
        return (byte) ((0xFF & a) ^ (0xFF & b));
    }

    public static byte[] xor(byte[] a, byte[] b) {
        byte[] c = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = xor(a[i], b[i]);
        }
        return c;
    }

    public static byte[] repeat(byte b, int length) {
        byte[] r = new byte[length];
        for (int i = 0; i < length; i++) {
            r[i] = b;
        }
        return r;
    }

    public static byte[] repeat(byte[] b, int length) {
        byte[] r = new byte[length];
        for (int i = 0; i < length; i++) {
            r[i] = b[i % b.length];
        }
        return r;
    }

    public static int ones(byte b) {
        int count = 0;

        for (int i = 0; i < 8; i++) {
            if ((b & (1 << i)) != 0) {
                count++;
            }
        }

        return count;
    }

    public static int hamming(byte[] x, byte[] y) {
        int distance = 0;
        for (int i = 0; i < x.length; i++) {
            distance += Bytes.ones(Bytes.xor(x[i], y[i]));
        }
        return distance;
    }

    public static byte[][] transpose(byte[] original, int blocksize) {
        int nBlocks = (int) Math.ceil((double) original.length
            / (double) blocksize);
        byte[][] result = new byte[blocksize][nBlocks];
        for (int i = 0; i < original.length; i++) {
            int block = i / blocksize;
            int position = i % blocksize;
            result[position][block] = original[i];
        }
        return result;
    }

    public static byte[][] split(byte[] original, int blocksize) {
        int nBlocks = (int) Math.ceil((double) original.length
            / (double) blocksize);
        byte[][] blocks = new byte[nBlocks][];
        for (int i = 0; i < nBlocks; i++) {
            blocks[i] = Arrays.copyOfRange(original, i * blocksize,
                Math.min(original.length, (i + 1) * blocksize));
        }
        return blocks;
    }

    public static byte[] random(int length) {
        byte[] b = new byte[length];
        RANDOM.nextBytes(b);
        return b;
    }

    public static byte[] random(int minLength, int maxLength) {
        int spread = maxLength - minLength;
        int i = RANDOM.nextInt(spread + 1);
        return random(minLength + i);
    }

    public static int collisions(byte[][] blocks) {
        int collisions = 0;
        Set<WrappedBytes> seen = new HashSet<>();
        for (byte[] block : blocks) {
            WrappedBytes wrapped = new WrappedBytes(block);
            if (seen.contains(wrapped)) {
                collisions++;
            } else {
                seen.add(wrapped);
            }
        }
        return collisions;
    }

    private static class WrappedBytes {
        private byte[] bytes;

        public WrappedBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof WrappedBytes)) {
                return false;
            }

            WrappedBytes that = (WrappedBytes) o;
            return Arrays.equals(this.bytes, that.bytes);
        }

        @Override
        public int hashCode() {
            int hash = 0;
            for (byte b : bytes) {
                hash ^= b;
            }
            return hash;
        }
    }
}
