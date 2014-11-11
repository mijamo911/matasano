package me.montgome.matasano;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.Test;

public class MersenneTwisterTest {
    @Test
    public void knownAnswerTest() {
        int[] key = new int[] { 0x123, 0x234, 0x345, 0x456 };
        MersenneTwister mt = new MersenneTwister();
        mt.init_by_array(key, key.length);

        Iterator<String> lines = Resources.readLines("me/montgome/matasano/resources/mt19937ar.out").iterator();
        lines.next(); //Skip header line
        while (lines.hasNext()) {
            String line = lines.next();
            String[] tokens = line.split(" +");
            for (String token : tokens) {
                if (token.length() == 0) {
                    continue;
                }

                long n = Long.parseLong(token);
                assertEquals(n, 0xFFFFFFFFL & mt.genrand_int32());
            }
        }
    }
}
