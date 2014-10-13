package me.montgome.matasano.oracles;

import static org.junit.Assert.assertEquals;
import me.montgome.matasano.Bytes;

import org.junit.Test;

public class OraclesTest {
    @Test
    public void getPrefixLength() {
        for (int i = 0; i <= 128 + 1; i++) {
            byte[] prefix = Bytes.random(i);
            byte[] suffix = new byte[0];
            byte[] key = Bytes.random(16);
            Oracle o = new PaddingOracle(() -> prefix, () -> suffix, new EcbOracle(() -> key));
            assertEquals(i, Oracles.getPrefixLength(o));
        }
    }

    @Test
    public void getInitialPadding() {
        byte[] key = Bytes.random(16);
        Oracle oracle = new PaddingOracle(
            () -> new byte[1],
            () -> new byte[0],
            new EcbOracle(() -> key));
        assertEquals(15, Oracles.getInitialPadding(oracle));
    }

    @Test
    public void getInitialPaddingNone() {
        byte[] key = Bytes.random(16);
        Oracle oracle = new EcbOracle(() -> key);
        assertEquals(0, Oracles.getInitialPadding(oracle));
    }
}
