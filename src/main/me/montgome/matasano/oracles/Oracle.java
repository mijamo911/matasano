package me.montgome.matasano.oracles;

public interface Oracle {
    byte[] encrypt(byte[] plaintext);
}
