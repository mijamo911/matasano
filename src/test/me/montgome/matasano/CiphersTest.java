package me.montgome.matasano;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CiphersTest {
    @Test
    public void ecbRoundTrip() {
        byte[] key = Bytes.random(16);

        byte[] plaintext = Strings.getBytes("banana");
        byte[] ciphertext = Ciphers.encryptEcb(plaintext, key);
        byte[] recoveredPlaintext = Ciphers.decryptEcb(ciphertext, key);

        assertEquals(
            Strings.newString(plaintext),
            Strings.newString(recoveredPlaintext));
    }
}
