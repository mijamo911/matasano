package me.montgome.matasano;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

public class Set1 {
    @Test
    public void problem1() {
        byte[] bytes = Codec
                .hexToBytes("49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d");
        String base64 = Codec.bytesToBase64(bytes);
        assertEquals("SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t", base64);
    }
    
    @Test
    public void problem2() {
        byte[] a = Codec.hexToBytes("1c0111001f010100061a024b53535009181c");
        byte[] b = Codec.hexToBytes("686974207468652062756c6c277320657965");
        byte[] c = Bytes.xor(a,b);
        assertEquals("746865206b696420646f6e277420706c6179", Codec.bytesToHex(c));
    }
    
    @Test
    public void problem3() throws Exception {
        byte[] b = Codec.hexToBytes("1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736");
        ScoredPlaintext result = Cracker.singleByteXor(b);
        assertEquals("Cooking MC's like a pound of bacon", result.plaintext);
    }
    
    @Test
    public void problem4() {
        ScoredPlaintext best = new ScoredPlaintext(0, null,null);
        
        for (String line : Resources.readLines("me/montgome/matasano/resources/gist.3132713.txt")) {
            ScoredPlaintext candidate = Cracker.singleByteXor(Codec.hexToBytes(line));
            if (candidate.score > best.score) {
                best = candidate;
            }
        }
        
        assertEquals("Now that the party is jumping\n", best.plaintext);
    }
    
    @Test
    public void problem5() throws Exception {
        byte[] plaintext = "Burning 'em, if you ain't quick and nimble\nI go crazy when I hear a cymbal".getBytes("UTF-8");
        byte[] key = "ICE".getBytes("UTF-8");
        byte[] ciphertext = Cracker.repeatedKeyXor(plaintext, key);
        assertEquals(
                "0b3637272a2b2e63622c2e69692a23" +
                "693a2a3c6324202d623d63343c2a26" +
                "226324272765272a282b2f20430a65" +
                "2e2c652a3124333a653e2b2027630c" +
                "692b20283165286326302e27282f",
                Codec.bytesToHex(ciphertext));
   }
    
    @Test
    public void problem6() {
        byte[] ciphertext = Codec.base64ToBytes(Resources.readFileStripNewlines("me/montgome/matasano/resources/gist.3132752.txt"));
        
        int nKeysToTry = 20;
        ScoredKeysize[] bestKeysizes = Cracker.keysize(ciphertext, 2, 40, nKeysToTry);
        byte[][] keys = new byte[nKeysToTry][];
        for (int j = 0; j < bestKeysizes.length; j++) {
            byte[] key = new byte[bestKeysizes[j].keysize];
            
            for (int i = 0; i < key.length; i++) {
                byte[][] transposed = Bytes.transpose(ciphertext, key.length);
                ScoredPlaintext candidate = Cracker.singleByteXor(transposed[i]);
                key[i] = candidate.key[0];
            }
            
            keys[j] = key;
        }

        PriorityQueue<ScoredPlaintext> plaintexts = new PriorityQueue<ScoredPlaintext>(keys.length, Collections.reverseOrder());
        for (byte[] key : keys) {
            String plaintext = Strings.newString(Cracker.repeatedKeyXor(ciphertext, key));
            double score = Scorer.score(plaintext);
            ScoredPlaintext scoredPlaintext = new ScoredPlaintext(score, plaintext, key);
            plaintexts.add(scoredPlaintext);
        }
        
        ScoredPlaintext best = plaintexts.poll();
        byte[] bestKey = best.key;
        assertEquals(29, bestKey.length);
        assertTrue(Arrays.equals(
            new byte[] {84, 101, 114, 109, 105, 110, 97, 116, 111, 114, 32, 88, 58, 32, 66, 114, 105, 110, 103, 32, 116, 104, 101, 32, 110, 111, 105, 115, 101},
            bestKey));
        
        assertEquals(
            Resources.readFileKeepNewlines("me/montgome/matasano/resources/Vanilla Ice - PLay That Funky Music.xor.txt"),
            best.plaintext);
    }
    
    @Test
    public void problem7() throws Exception {
        byte[] ciphertext = Codec.base64ToBytes(Resources.readFileStripNewlines("me/montgome/matasano/resources/gist.3132853.txt"));
        SecretKey key = new SecretKeySpec(Strings.getBytes("YELLOW SUBMARINE"), "AES");
        Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aes.init(Cipher.DECRYPT_MODE, key);
        String plaintext = Strings.newString(aes.doFinal(ciphertext));
        assertEquals(
            Resources.readFileKeepNewlines("me/montgome/matasano/resources/Vanilla Ice - Play That Funky Music.aes.txt"),
            plaintext);
    }

    @Test
    public void problem8() {
        int maxCollisions = 0;
        String bestLine = null;
        
        for (String line : Resources.readLines("me/montgome/matasano/resources/gist.3132928.txt")) {
            int collisions = Bytes.collisions(Bytes.split(Codec.hexToBytes(line), 16));
            if (collisions > maxCollisions) {
                maxCollisions = collisions;
                bestLine = line;
            }
        }
        
        assertEquals(
            "d880619740a8a19b7840a8a31c810a3d08649af70dc06f4fd5d2d69c744cd283e2dd052f6b641dbf9d11b0348542bb5708649af70dc06f4fd5d2d69c744cd2839475c9dfdbc1d46597949d9c7e82bf5a08649af70dc06f4fd5d2d69c744cd28397a93eab8d6aecd566489154789a6b0308649af70dc06f4fd5d2d69c744cd283d403180c98c8f6db1f2a3f9c4040deb0ab51b29933f2c123c58386b06fba186a",
            bestLine);
    }
}
