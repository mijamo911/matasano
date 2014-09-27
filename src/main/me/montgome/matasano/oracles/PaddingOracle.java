package me.montgome.matasano.oracles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.val;
import me.montgome.matasano.Bytes;

import com.google.common.base.Throwables;

@AllArgsConstructor
public class PaddingOracle implements Oracle {
    private Oracle delegate;

    @Override
    public byte[] encrypt(byte[] plaintext) {
        try {
            val s = new ByteArrayOutputStream();
            s.write(Bytes.random(5, 10));
            s.write(plaintext);
            s.write(Bytes.random(5, 10));
            return delegate.encrypt(s.toByteArray());
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }
}
