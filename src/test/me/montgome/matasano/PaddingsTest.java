package me.montgome.matasano;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class PaddingsTest {
    @Test
    public void pkcs7PadEmptyBlock() {
        byte[] expected = Bytes.repeat(16, 16);
        byte[] actual = Paddings.addPkcs7(new byte[0], 16);
        assertTrue(Arrays.equals(expected, actual));
    }
}
