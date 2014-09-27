package me.montgome.matasano;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class Oracles {
    private static final SecureRandom RANDOM = new SecureRandom();
    
    public static byte[] ecb(byte[] plaintext) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            int prefixLength = RANDOM.nextInt(6) + 4; // 5 - 10
            byte[] prefix = new byte[prefixLength];
            RANDOM.nextBytes(prefix);
            stream.write(prefix);
            
            stream.write(plaintext);
            
            int suffixLength = RANDOM.nextInt(6) + 4; // 5 -10
            byte[] suffix = new byte[suffixLength];
            RANDOM.nextBytes(suffix);
            stream.write(suffix);
            
            byte[] padded = stream.toByteArray();
            
            byte[] key = Bytes.random(16);
            
            if (RANDOM.nextBoolean()) {
                return Ciphers.encryptEcb(padded, key);
            } else {
                return Ciphers.encryptCbc(padded, key, Bytes.random(16));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
