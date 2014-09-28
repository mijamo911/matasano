package me.montgome.matasano;

public class ScoredPlaintext implements Comparable<ScoredPlaintext> {
    public final double score;
    public final String plaintext;
    public final byte[] key;
    
    public ScoredPlaintext(double score, String plaintext, byte[] key) {
        this.score = score;
        this.plaintext = plaintext;
        this.key = key;
    }

    @Override
    public int compareTo(ScoredPlaintext that) {
        return Double.compare(this.score, that.score);
    }
    
    public String toString() {
        return plaintext;
    }
}
