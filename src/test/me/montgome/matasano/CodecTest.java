package me.montgome.matasano;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class CodecTest {
    @Test
    public void base64TableIsCorrectSize() {
        assertEquals(64, Codec.BASE_64_TABLE.length());
    }

    @Test
    public void wikipediaBase16() throws Exception {
        byte[] expected = Bytes.bytes(77, 97, 110);
        byte[] actual = Codec.hexToBytes("4d616e");
        System.out.println(Bytes.toBinaryString(expected));
        System.out.println(Bytes.toBinaryString(actual));
        assertTrue(Arrays.equals(expected, actual));
    }

    @Test
    public void wikipediaToStringBinaryTest() throws Exception {
        assertEquals("010011010110000101101110", Bytes.toBinaryString(Strings.getBytes("Man")));
    }

    @Test
    public void wikipediaBase64() throws Exception {
        byte[] bytes = "Man".getBytes("UTF-8");
        assertEquals("TWFu", Codec.bytesToBase64(bytes));
        assertEquals(
            Bytes.toBinaryString(bytes),
            Bytes.toBinaryString(Codec.base64ToBytes("TWFu")));
    }

    @Test
    public void moreBase64() {
        String expected =
            "000111" + // H 7
            "010100" + // U 20
            "001000" + // I 8
            "011111" + // f 31
            "010011" + // T 19
            "010000" + // Q 16
            "101100" + // s 44
            "001111";  // P 15
        String actual = Bytes.toBinaryString(Codec.base64ToBytes("HUIfTQsP"));
        assertEquals(expected, actual);
    }

    @Test
    public void base64LeftmostBitOfLowerByteIsSet() {
        String expected = "goig";
        String actual =
            Codec.bytesToBase64(
                Bytes.bytes(130, 136, 160));
        assertEquals(expected, actual);
    }

    @Test
    public void base64SingleByte() {
        assertEquals("AQ==", Codec.bytesToBase64(Bytes.bytes(1)));
        assertEquals("Ag==", Codec.bytesToBase64(Bytes.bytes(2)));
    }

    @Test
    public void base64DoubleByte() {
        assertEquals("AAE=", Codec.bytesToBase64(Bytes.bytes(0, 1)));
        assertEquals("AAI=", Codec.bytesToBase64(Bytes.bytes(0, 2)));
    }
}
