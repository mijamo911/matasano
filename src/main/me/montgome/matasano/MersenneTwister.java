package me.montgome.matasano;

public class MersenneTwister {

    /* Period parameters */
    private static final int N = 624;
    private static final int M = 397;
    private static final int MATRIX_A = 0x9908b0df; /* constant vector a */
    private static final int UPPER_MASK = 0x80000000; /* most significant w-r bits */
    private static final int LOWER_MASK = 0x7fffffff; /* least significant r bits */

    private static int[] mag01 = new int[] { 0x0, MATRIX_A };
    /* mag01[x] = x * MATRIX_A  for x=0,1 */

    private int[] mt = new int[N]; /* the array for the state vector  */
    private int mti = N + 1; /* mti==N+1 means mt[N] is not initialized */

    /* initializes mt[N] with a seed */
    void init_genrand(int s) {
        mt[0] = s;
        for (mti = 1; mti < N; mti++) {
            mt[mti] = (1812433253 * (mt[mti - 1] ^ (mt[mti - 1] >>> 30)) + mti);
        }
    }

    /* initialize by an array with array-length */
    /* init_key is the array for initializing keys */
    /* key_length is its length */
    /* slight change for C++, 2004/2/26 */
    void init_by_array(int init_key[], int key_length) {
        int i, j, k;
        init_genrand(19650218);
        i = 1;
        j = 0;
        k = (N > key_length ? N : key_length);
        for (; k != 0; k--) {
            mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * 1664525)) + init_key[j] + j; /* non linear */
            i++;
            j++;
            if (i >= N) {
                mt[0] = mt[N - 1];
                i = 1;
            }
            if (j >= key_length)
                j = 0;
        }
        for (k = N - 1; k != 0; k--) {
            mt[i] = (mt[i] ^ ((mt[i - 1] ^ (mt[i - 1] >>> 30)) * 1566083941)) - i; /* non linear */
            i++;
            if (i >= N) {
                mt[0] = mt[N - 1];
                i = 1;
            }
        }

        mt[0] = 0x80000000; /* MSB is 1; assuring non-zero initial array */
    }

    /* generates a random number on [0,0xffffffff]-interval */
    int genrand_int32() {
        int y;

        if (mti >= N) { /* generate N words at one time */
            int kk;

            if (mti == N + 1) /* if init_genrand() has not been called, */
                init_genrand(5489); /* a default initial seed is used */

            for (kk = 0; kk < N - M; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            for (; kk < N - 1; kk++) {
                y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
                mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 0x1];
            }
            y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
            mt[N - 1] = mt[M - 1] ^ (y >>> 1) ^ mag01[y & 0x1];

            mti = 0;
        }

        y = mt[mti++];

        /* Tempering */
        y ^= (y >>> 11);
        y ^= (y << 7) & 0x9d2c5680;
        y ^= (y << 15) & 0xefc60000;
        y ^= (y >>> 18);

        return y;
    }

}
