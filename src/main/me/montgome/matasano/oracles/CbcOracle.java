package me.montgome.matasano.oracles;

import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import me.montgome.matasano.Ciphers;

@AllArgsConstructor
public class CbcOracle implements Oracle {
    private final Supplier<byte[]> key;
    private final Supplier<byte[]> iv;

    @Override
    public byte[] encrypt(byte[] plaintext) {
        return Ciphers.encryptCbc(plaintext, key.get(), iv.get());
    }
}
