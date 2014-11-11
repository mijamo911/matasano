package me.montgome.matasano;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import me.montgome.matasano.oracles.CbcPaddingOracle;
import me.montgome.matasano.oracles.ValidPaddingOracle;

import org.junit.Test;

public class Set3 {
    @Test
    public void problem17() throws Exception {
        byte[] key = Bytes.random(16);
        byte[] iv = Bytes.random(16);
        CbcPaddingOracle oracle = new CbcPaddingOracle(new ValidPaddingOracle(() -> key, () -> iv), () -> iv);

        Function<String, byte[]> encrypt = (s) -> Ciphers.encryptCbc(Codec.base64ToBytes(s), key, iv);
        Function<byte[], byte[]> decrypt = (b) -> oracle.decrypt(b);
        Function<String, String> roundtrip = (s) -> Strings.newString(decrypt.apply(encrypt.apply(s)));

        Iterator<String> it = Resources.readLines("me/montgome/matasano/resources/3.17.txt").iterator();

        assertEquals("000000Now that the party is jumping\0", roundtrip.apply(it.next()));
        assertEquals("000001With the bass kicked in and the Vega's are pumpin'\0", roundtrip.apply(it.next()));
        assertEquals("000002Quick to the point, to the point, no faking\0", roundtrip.apply(it.next()));
        assertEquals("000003Cooking MC's like a pound of bacon\0", roundtrip.apply(it.next()));
        assertEquals("000004Burning 'em, if you ain't quick and nimble", roundtrip.apply(it.next()));
        assertEquals("000005I go crazy when I hear a cymbal\0", roundtrip.apply(it.next()));
        assertEquals("000006And a high hat with a souped up tempo\0", roundtrip.apply(it.next()));
        assertEquals("000007I'm on a roll, it's time to go solo\0", roundtrip.apply(it.next()));
        assertEquals("000008ollin' in my five point oh\0", roundtrip.apply(it.next()));
        assertEquals("000009ith my rag-top down so my hair can blow", roundtrip.apply(it.next()));
    }

    @Test
    public void problem18() {
        byte[] key = Strings.getBytes("YELLOW SUBMARINE");
        byte[] nonce = new byte[8];
        byte[] ciphertext = Codec.base64ToBytes("L77na/nrFsKvynd6HzOoG7GHTLXsTVu9qvY/2syLXzhPweyyMTJULu/6/kXX0KSvoOLSFQ==");
        byte[] plaintext = Ciphers.ctr(ciphertext, key, nonce);

        assertEquals("Yo, VIP Let's kick it Ice, Ice, baby Ice, Ice, baby I", Strings.newString(plaintext));
    }

    @Test
    public void problem19() throws Exception {
        // A terrible beauty
    }

    @Test
    public void problem20() {
        byte[] key = Bytes.random(16);
        byte[] nonce = new byte[8];

        Collection<byte[]> ciphertexts = new LinkedList<>();
        for (String line : Resources.readLines("me/montgome/matasano/resources/3.20.txt")) {
            ciphertexts.add(Ciphers.ctr(Codec.base64ToBytes(line), key, nonce));
        }

        int minLength = Integer.MAX_VALUE;
        for (byte[] b : ciphertexts) {
            if (b.length < minLength) {
                minLength = b.length;
            }
        }

        List<byte[]> truncated = new LinkedList<>();
        for (byte[] b : ciphertexts) {
            truncated.add(Bytes.first(b, minLength));
        }

        byte[] ciphertext = Bytes.combine(truncated);
        byte[] recoveredKey = Cracker.getXorKey(ciphertext, minLength);

        int i = 0;
        for (String line : Resources.readLines("me/montgome/matasano/resources/3.20.truncated.txt")) {
            String recoveredPlaintext = Strings.newString(Bytes.xor(truncated.get(i), recoveredKey));
            assertEquals(line, recoveredPlaintext);
            i++;
        }
    }

    @Test
    public void problem21() {
        int[] key = new int[] { 0x123, 0x234, 0x345, 0x456 };
        MersenneTwister mt = new MersenneTwister();
        mt.init_by_array(key, key.length);

        Iterator<String> lines = Resources.readLines("me/montgome/matasano/resources/mt19937ar.out").iterator();
        lines.next(); //Skip header line
        while (lines.hasNext()) {
            String line = lines.next();
            String[] tokens = line.split(" +");
            for (String token : tokens) {
                if (token.length() == 0) {
                    continue;
                }

                long n = Long.parseLong(token);
                assertEquals(n, 0xFFFFFFFFL & mt.genrand_int32());
            }
        }
    }

    @Test
    public void problem22() throws Exception {
        MersenneTwister mt = new MersenneTwister();

        mt.init_genrand(4);
        int output1 = mt.genrand_int32();
        int output2 = mt.genrand_int32();
        int output3 = mt.genrand_int32();
        mt.init_genrand(4);
        assertEquals(output1, mt.genrand_int32());
        assertEquals(output2, mt.genrand_int32());
        assertEquals(output3, mt.genrand_int32());

        long start = System.currentTimeMillis();

        Random random = new Random();
        int min = 40;
        int max = 1000;
        long spread = 1000 - 40;

        Thread.sleep((Math.abs(random.nextLong()) % (spread * 1000)) + min * 1000);

        int seed = (int) (0xFFFFFFFF & System.currentTimeMillis());
        mt.init_genrand(seed);

        Thread.sleep((Math.abs(random.nextLong()) % (spread * 1000)) + min * 1000);

        int output = mt.genrand_int32();

        long end = System.currentTimeMillis();

        for (long l = start; l < end; l++) {
            int candidateSeed = (int) (0xFFFFFFFF & l);
            mt.init_genrand(candidateSeed);
            if (mt.genrand_int32() == output) {
                assertEquals(seed, candidateSeed);
                System.out.println(String.format("Found seed %s with output %s", seed, output));
            }
        }
    }
}
