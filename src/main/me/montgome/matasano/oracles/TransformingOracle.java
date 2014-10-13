package me.montgome.matasano.oracles;

import java.util.function.Function;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TransformingOracle implements Oracle {
    private final Function<byte[], byte[]> transform;
    private final Oracle delegate;

    @Override
    public byte[] encrypt(byte[] plaintext) {
        return delegate.encrypt(transform.apply(plaintext));
    }
}
