package me.montgome.matasano;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class BytesTest {
    @Test
    public void xor() {
        byte[] a = Bytes.bytes(0x1c);
        byte[] b = Bytes.bytes(0x68);
        
        byte[] expected = Bytes.bytes(0x74);
        byte[] actual = Bytes.xor(a, b);
        
        assertTrue(Arrays.equals(expected, actual));
    }
    
    @Test
    public void decodeXor() {
        byte[] a = Codec.hexToBytes("1c");
        byte[] b = Codec.hexToBytes("68");
        
        byte[] expected = Bytes.bytes(0x74);
        byte[] actual = Bytes.xor(a, b);
        
        assertTrue(Arrays.equals(expected, actual));
        assertEquals("74", Codec.bytesToHex(actual));
    }
    
    @Test
    public void transpose() {
        byte[] original = new byte[] {0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3};
        byte[][] transposed = Bytes.transpose(original, 4);
        assertTrue(Arrays.equals(new byte[] {0, 0, 0}, transposed[0]));
        assertTrue(Arrays.equals(new byte[] {1, 1, 1}, transposed[1]));
        assertTrue(Arrays.equals(new byte[] {2, 2, 2}, transposed[2]));
        assertTrue(Arrays.equals(new byte[] {3, 3, 3}, transposed[3]));
    }
}
