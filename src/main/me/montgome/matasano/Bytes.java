package me.montgome.matasano;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import lombok.val;

import com.google.common.base.Throwables;

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

            //System.out.println(toBinaryString((byte) high)
            //    + toBinaryString((byte) low));

            int lowBits = end % 8;
            int highBits = bits - lowBits;

            int mask = (1 << highBits) - 1;
            //System.out.println(toBinaryString((byte) mask));

            int highValue = (high & mask) << lowBits;
            int lowValue = ((0xFF & low) >>> (8 - lowBits));

            //System.out.println(toBinaryString((byte) highValue));
            //System.out.println(toBinaryString((byte) lowValue));

            int value = highValue | lowValue;
            //System.out.println(toBinaryString((byte) value));

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

    public static byte[] repeat(int i, int length) {
        if (i > 255) {
            throw new IllegalArgumentException("Byte value outside of allowed range");
        }
        return repeat((byte) i, length);
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

    public static Set<WrappedBytes> getCollisions(byte[][] blocks) {
        Set<WrappedBytes> seen = new HashSet<>();
        Set<WrappedBytes> collisions = new HashSet<>();
        for (byte[] block : blocks) {
            WrappedBytes wrapped = new WrappedBytes(block);
            if (seen.contains(wrapped)) {
                collisions.add(wrapped);
            } else {
                seen.add(wrapped);
            }
        }
        return collisions;
    }

    public static int countCollisions(byte[][] blocks) {
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

    public static byte[] extend(byte[] b, int length) {
        byte[] extended = new byte[length];
        System.arraycopy(b, 0, extended, 0, b.length);
        return extended;
    }

    public static byte[] first(byte[] b, int count) {
        byte[] first = new byte[count];
        System.arraycopy(b, 0, first, 0, count);
        return first;
    }

    public static byte[] combine(byte[]... bs) {
        try {
            val s = new ByteArrayOutputStream();
            for (byte[] b : bs) {
                s.write(b);
            }
            return s.toByteArray();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public static byte[] firstCollision(byte[][] bs) {
        val seen = new HashSet<WrappedBytes>();
        for (byte[] b : bs) {
            WrappedBytes w = new WrappedBytes(b);
            if (seen.contains(w)) {
                return b;
            } else {
                seen.add(w);
            }
        }
        throw new RuntimeException("No collision found!");
    }
}
