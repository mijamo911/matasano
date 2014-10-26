package me.montgome.matasano;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
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

        assertEquals(
            "Yo, VIP Let's kick it Ice, Ice, baby Ice, Ice, baby I",
            Strings.newString(plaintext));
    }
}
