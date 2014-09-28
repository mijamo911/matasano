package me.montgome.matasano;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringsTest {
    @Test
    public void hamming() {
        assertEquals(37, Strings.hamming("this is a test", "wokka wokka!!!"));
    }
}
