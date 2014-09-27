package me.montgome.matasano;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class Set2 {
    @Test
    public void problem9() {
        byte[] original = Strings.getBytes("YELLOW SUBMARINE");
        byte[] expected = new byte[20];
        System.arraycopy(original, 0, expected, 0, original.length);
        expected[16] = 0x04;
        expected[17] = 0x04;
        expected[18] = 0x04;
        expected[19] = 0x04;
        
        assertTrue(Arrays.equals(expected, Paddings.addPkcs7(original, 20)));
    }

    @Test
    public void problem10() {
        byte[] ciphertext = Codec.base64ToBytes(Resources.readFileStripNewlines("me/montgome/matasano/resources/gist.3132976.txt"));
        assertEquals(
            Resources.readFileKeepNewlines("me/montgome/matasano/resources/Vanilla Ice - Play That Funky Music.cbc.txt"),
            Strings.newString(
                Ciphers.decryptCbc(
                    ciphertext,
                    Strings.getBytes("YELLOW SUBMARINE"),
                    Bytes.repeat((byte) 0, 16))));
    }
    
    @Test
    public void problem11() throws InterruptedException {
        byte[] plaintext = Strings.getBytes("0000000000000000000000000000000000000000000000000000000000000000");
        for (int i = 0; i < 100; i++) {
            byte[] ciphertext = Oracles.ecb(plaintext);
            boolean isEcb = Bytes.collisions(Bytes.split(ciphertext, 16)) > 0;
            System.out.println(String.format("%s - %s",
                Codec.bytesToHex(ciphertext),
                isEcb));
            Thread.sleep(5000);
        }
    }
}
