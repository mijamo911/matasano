package me.montgome.matasano.oracles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import me.montgome.matasano.Bytes;
import me.montgome.matasano.Paddings;

import com.google.common.base.Throwables;

@AllArgsConstructor
public class CbcPaddingOracle {
    private final ValidPaddingOracle oracle;
    private final Supplier<byte[]> iv;

    public byte[] decrypt(byte[] ciphertext) {
        byte[][] blocks = Bytes.split(ciphertext, iv.get().length);

        ByteArrayOutputStream plaintext = new ByteArrayOutputStream();

        byte[] previous = iv.get();

        for (int z = 0; z < blocks.length; z++) {
            byte[] block = blocks[z];
            byte[] known = new byte[block.length];
            byte[] prefix = new byte[block.length];

            block:
            for (int i = block.length - 1; i >= 0; i--) {
                for (int j = 0; j < 256; j++) {
                    prefix[i] = (byte) j;
                    boolean isPaddingValid = oracle.decrypt(Bytes.combine(prefix, block));
                    if (isPaddingValid) {
                        int padding = block.length - i;
                        known[i] = (byte) (j ^ padding);

                        for (int k = block.length - 1; k >= i; k--) {
                            prefix[k] = (byte) ((0xFF & known[k]) ^ (padding + 1));
                        }

                        continue block;
                    }
                }
            }

            boolean isLastBlock = (z == blocks.length - 1);
            try {
                if (isLastBlock) {
                    plaintext.write(Paddings.removePkcs7(Bytes.xor(previous, known)));
                } else {
                    plaintext.write(Bytes.xor(previous, known));
                    previous = block;
                }
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }

        return plaintext.toByteArray();
    }
}
