package me.montgome.matasano.oracles;

import static org.junit.Assert.assertEquals;
import me.montgome.matasano.Bytes;

import org.junit.Test;

public class OraclesTest {
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
