package me.montgome.matasano.oracles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.val;

import com.google.common.base.Throwables;

@AllArgsConstructor
public class PaddingOracle implements Oracle {
    private Supplier<byte[]> prefix;
    private Supplier<byte[]> suffix;
    private Oracle delegate;

    @Override
    public byte[] encrypt(byte[] plaintext) {
        try {
            val s = new ByteArrayOutputStream();
            s.write(prefix.get());
            s.write(plaintext);
            s.write(suffix.get());
            return delegate.encrypt(s.toByteArray());
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
