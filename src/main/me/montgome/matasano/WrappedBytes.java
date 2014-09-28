package me.montgome.matasano;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class WrappedBytes {
    @Getter
    private byte[] bytes;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WrappedBytes)) {
            return false;
        }

        WrappedBytes that = (WrappedBytes) o;
        return Arrays.equals(this.bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (byte b : bytes) {
            hash ^= b;
        }
        return hash;
    }

    @Override
    public String toString() {
        return Codec.bytesToBase64(bytes);
    }
}
