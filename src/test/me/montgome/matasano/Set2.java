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
import me.montgome.matasano.oracles.ProfileOracle;

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
        assertTrue("Oracle is not ECB", Oracles.isEcb(oracle));
        int blockSize = Oracles.getBlockSize(oracle);
        assertEquals("Wrong block size", 16, blockSize);

        Function<byte[], Map<WrappedBytes, Byte>> createDictionary = x -> {
            val map = new HashMap<WrappedBytes, Byte>();
            for (int i = 0; i < 256; i++) {
                byte[] plaintext = Bytes.extend(x, x.length + 1);
                plaintext[plaintext.length - 1] = (byte) i;
                byte[] ciphertext = oracle.encrypt(plaintext);
                val key = new WrappedBytes(Bytes.first(ciphertext, plaintext.length));
                val value = (byte) i;
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

    @Test
    public void problem13() {
        byte[] key = Bytes.random(16);
        Oracle oracle = new ProfileOracle(new EcbOracle(() -> key));

        assertTrue("Oracle is not ECB", Oracles.isEcb(oracle));
        int blockSize = Oracles.getBlockSize(oracle);

        String actualRole = "user";
        String desiredRole = "admin";
        byte[] adminPlaintext = Paddings.addPkcs7(Strings.getBytes(desiredRole), blockSize);
        byte[] adminCiphertext = null;
        for (int i = 0; i < blockSize; i++) {
            byte[] plaintext = Bytes.combine(
                Bytes.repeat(65, i),
                adminPlaintext,
                adminPlaintext);
            byte[] ciphertext = oracle.encrypt(plaintext);
            if (Bytes.countCollisions(Bytes.split(ciphertext, blockSize)) > 0) {
                adminCiphertext = Bytes.firstCollision(Bytes.split(ciphertext, blockSize));
                break;
            }
        }

        int initialPadding = Oracles.getInitialPadding(oracle);
        byte[] padding = Bytes.repeat(65, initialPadding + Strings.getBytes(actualRole).length);
        byte[] ciphertext = oracle.encrypt(padding);
        System.arraycopy(adminCiphertext, 0, ciphertext, ciphertext.length - adminCiphertext.length, adminCiphertext.length);
        byte[] plaintext = Ciphers.decryptEcb(ciphertext, key);
        Profile p = Profile.parse(Strings.newString(plaintext));
        assertEquals(desiredRole, p.getRole());
    }

    @Test
    public void problem14() {
        byte[] prefix = Bytes.random(8, 24);
        byte[] suffix = Codec.base64ToBytes(
            Resources.readFileStripNewlines("me/montgome/matasano/resources/2.12.suffix.txt"));
        byte[] ecbKey = Bytes.random(16);

        Oracle oracle = new PaddingOracle(() -> prefix, () -> suffix, new EcbOracle(() -> ecbKey));
        assertTrue("Oracle is not ECB", Oracles.isEcb(oracle));
        int blockSize = Oracles.getBlockSize(oracle);
        assertEquals("Wrong block size", 16, blockSize);
        int prefixLength = Oracles.getPrefixLength(oracle);
        assertEquals("Wrong prefix length", prefix.length, prefixLength);
        int prefixPadding = blockSize - (prefixLength % blockSize);
        int prefixBlocks = (prefixLength + (blockSize - 1)) / blockSize;

        Function<byte[], Map<WrappedBytes, Byte>> createDictionary = x -> {
            val map = new HashMap<WrappedBytes, Byte>();
            for (int i = 0; i < 256; i++) {
                byte[] plaintext = Bytes.extend(x, x.length + 1);
                plaintext[plaintext.length - 1] = (byte) i;
                byte[] ciphertext = oracle.encrypt(plaintext);
                val key = new WrappedBytes(Bytes.first(ciphertext, plaintext.length + prefixLength));
                val value = (byte) i;
                map.put(key, value);
            }
            return map;
        };

        byte[] ciphertext = oracle.encrypt(new byte[0]);
        byte[] plaintext = new byte[ciphertext.length];

        int i = 0;
        for (i = 0; i < ciphertext.length; i++) {
            int blockNumber = i / blockSize + 1;

            int paddingSize = blockSize - (i % blockSize) - 1 + prefixPadding;
            byte[] padding = Bytes.repeat(0, paddingSize);

            byte[] block = Bytes.extend(padding, blockNumber * blockSize - 1 + prefixPadding);
            System.arraycopy(plaintext, 0, block, padding.length, i);
            val dictionary = createDictionary.apply(block);
            val dKey = new WrappedBytes(
                Bytes.first((oracle.encrypt(padding)), (blockNumber + prefixBlocks) * blockSize));
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
