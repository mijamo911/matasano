package me.montgome.matasano.oracles;

import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import me.montgome.matasano.Ciphers;

@AllArgsConstructor
public class EcbOracle implements Oracle {
    private Supplier<byte[]> key;

    @Override
    public byte[] encrypt(byte[] plaintext) {
        return Ciphers.encryptEcb(plaintext, key.get());
    }
}
