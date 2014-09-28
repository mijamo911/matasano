package me.montgome.matasano.oracles;

import me.montgome.matasano.Bytes;
import me.montgome.matasano.Ciphers;

public class CbcOracle implements Oracle {
    @Override
    public byte[] encrypt(byte[] plaintext) {
        byte[] key = Bytes.random(16);
        byte[] iv = Bytes.random(16);
        return Ciphers.encryptCbc(plaintext, key, iv);
    }
}
