package me.montgome.matasano.oracles;

import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import me.montgome.matasano.Ciphers;
import me.montgome.matasano.PaddingException;

@AllArgsConstructor
public class ValidPaddingOracle {
    private final Supplier<byte[]> key;
    private final Supplier<byte[]> iv;

    public boolean decrypt(byte[] ciphertext) {
        try {
            Ciphers.decryptCbc(ciphertext, key.get(), iv.get());
            return true;
        } catch (PaddingException e) {
            return false;
        }
    }
}
