package me.montgome.matasano;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.val;
import me.montgome.matasano.oracles.CbcOracle;
import me.montgome.matasano.oracles.EcbOracle;
import me.montgome.matasano.oracles.Oracle;
import me.montgome.matasano.oracles.Oracles;
import me.montgome.matasano.oracles.PaddingOracle;

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
    public void problem11() {
        byte[] plaintext = Strings.getBytes("0000000000000000000000000000000000000000000000000000000000000000");

        Supplier<byte[]> fiveToTenRandomBytes = () -> Bytes.random(5, 10);
        Oracle cbc = new PaddingOracle(fiveToTenRandomBytes, fiveToTenRandomBytes, new CbcOracle());
        Oracle ecb = new PaddingOracle(fiveToTenRandomBytes, fiveToTenRandomBytes, new EcbOracle(() -> Bytes.random(16)));

        int blockSize = 16;
        assertFalse(Oracles.isEcb(cbc.encrypt(plaintext), blockSize));
        assertTrue(Oracles.isEcb(ecb.encrypt(plaintext), blockSize));
    }

    @Test
    public void problem12() {
        byte[] prefix = new byte[0];
        byte[] suffix = Codec.base64ToBytes(
            Resources.readFileStripNewlines("me/montgome/matasano/resources/2.12.suffix.txt"));
        byte[] ecbKey = Bytes.random(16);

        Oracle oracle = new PaddingOracle(() -> prefix, () -> suffix, new EcbOracle(() -> ecbKey));
        int blockSize = Oracles.getBlockSize(oracle);
        assertEquals("Wrong block size", 16, blockSize);
        assertTrue(
            "Oracle is not ECB",
            Oracles.isEcb(
                oracle.encrypt(
                    Bytes.repeat((byte) 65, blockSize * 2)),
                    blockSize));

        Function<byte[], Map<WrappedBytes, Byte>> createDictionary = x -> {
            val map = new HashMap<WrappedBytes, Byte>();
            for (int i = 0; i < 256; i++) {
                byte[] plaintext = Bytes.extend(x, x.length + 1);
                plaintext[plaintext.length - 1] = (byte) i;
                byte[] ciphertext = oracle.encrypt(plaintext);
                val key = new WrappedBytes(Bytes.first(ciphertext, plaintext.length));
                val value = (byte) i;
                System.out.println(String.format("%s -> %s", key, value));
                map.put(key, value);
            }
            return map;
        };

        byte[] ciphertext = oracle.encrypt(new byte[0]);
        byte[] plaintext = new byte[ciphertext.length];

        int i = 0;
        for (i = 0; i < ciphertext.length; i++) {
            int blockNumber = i / blockSize + 1;

            int paddingSize = blockSize - (i % blockSize) - 1;
            byte[] padding = Bytes.repeat(0, paddingSize);

            byte[] block = Bytes.extend(padding, blockNumber * blockSize - 1);
            System.arraycopy(plaintext, 0, block, padding.length, i);
            val dictionary = createDictionary.apply(block);
            val dKey = new WrappedBytes(
                Bytes.first((oracle.encrypt(padding)), blockNumber * blockSize));
            System.out.println(dKey);
            if (!dictionary.containsKey(dKey)) {
                break;
            }
            plaintext[i] = dictionary.get(dKey);
        }

        assertEquals(
            Resources.readFileKeepNewlines("me/montgome/matasano/resources/Vanilla Ice - Ragtop.txt"),
            Strings.newString(Bytes.first(plaintext, i)));
    }
}
