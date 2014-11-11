package me.montgome.matasano;

import java.nio.ByteBuffer;

public class MersenneTwister {

    /* Period parameters */
    static final int N = 624;
    private static final int M = 397;
    private static final int MATRIX_A = 0x9908b0df; /* constant vector a */
    private static final int UPPER_MASK = 0x80000000; /* most significant w-r bits */
    private static final int LOWER_MASK = 0x7fffffff; /* least significant r bits */

    private static int[] mag01 = new int[] { 0x0, MATRIX_A };
    /* mag01[x] = x * MATRIX_A  for x=0,1 */

    private int[] state = new int[N]; /* the array for the state vector  */
    private int index = N + 1; /* mti==N+1 means mt[N] is not initialized */

    public MersenneTwister() {}

    public MersenneTwister(int seed) {
        initialize(seed);
    }

    public MersenneTwister(int[] state, int index) {
        this.state = state;
        this.index = index;
    }

    /* initializes mt[N] with a seed */
    void initialize(int seed) {
        state[0] = seed;
        for (index = 1; index < N; index++) {
            state[index] = (1812433253 * (state[index - 1] ^ (state[index - 1] >>> 30)) + index);
        }
    }

    void initialize(int[] seed) {
        int i, j, k;
        initialize(19650218);
        i = 1;
        j = 0;
        k = (N > seed.length ? N : seed.length);
        for (; k != 0; k--) {
            state[i] = (state[i] ^ ((state[i - 1] ^ (state[i - 1] >>> 30)) * 1664525)) + seed[j] + j; /* non linear */
            i++;
            j++;
            if (i >= N) {
                state[0] = state[N - 1];
                i = 1;
            }
            if (j >= seed.length) {
                j = 0;
            }
        }
        for (k = N - 1; k != 0; k--) {
            state[i] = (state[i] ^ ((state[i - 1] ^ (state[i - 1] >>> 30)) * 1566083941)) - i; /* non linear */
            i++;
            if (i >= N) {
                state[0] = state[N - 1];
                i = 1;
            }
        }

        state[0] = 0x80000000; /* MSB is 1; assuring non-zero initial array */
    }

    /* generates a random number on [0,0xffffffff]-interval */
    int nextInt() {
        int y;

        if (index >= N) { /* generate N words at one time */
            int kk;

            if (index == N + 1) /* if init_genrand() has not been called, */
                initialize(5489); /* a default initial seed is used */

            for (kk = 0; kk < N - M; kk++) {
                y = (state[kk] & UPPER_MASK) | (state[kk + 1] & LOWER_MASK);
                state[kk] = state[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            for (; kk < N - 1; kk++) {
                y = (state[kk] & UPPER_MASK) | (state[kk + 1] & LOWER_MASK);
                state[kk] = state[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            y = (state[N - 1] & UPPER_MASK) | (state[0] & LOWER_MASK);
            state[N - 1] = state[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

            index = 0;
        }

        y = state[index++];

        return temper(y);
    }

    static int temper(int y) {
        y ^= (y >>> 11);
        y ^= (y << 7) & 0x9d2c5680;
        y ^= (y << 15) & 0xefc60000;
        y ^= (y >>> 18);

        return y;
    }

    static int untemper(int y) {
        y = undoRight(y, 18);
        y = undoLeft(y, 15, 0xefc60000);
        y = undoLeft(y, 7, 0x9d2c5680);
        y = undoRight(y, 11);

        return y;
    }

    private static int undoRight(int y, int shift) {
        Bits b = new Bits(y);
        for (int i = shift; i < Integer.SIZE; i++) {
            b.xorBit(i, b.getBit(i - shift));
        }
        ByteBuffer buffer = ByteBuffer.wrap(b.getBytes());
        return buffer.getInt();
    }

    private static int undoLeft(int y, int shift, int m) {
        Bits mask = new Bits(m);
        Bits b = new Bits(y);
        for (int i = Integer.SIZE - (shift + 1); i >= 0; i--) {
            if (mask.getBit(i)) {
                b.xorBit(i, b.getBit(i + shift));
            }
        }
        ByteBuffer buffer = ByteBuffer.wrap(b.getBytes());
        return buffer.getInt();
    }
}
