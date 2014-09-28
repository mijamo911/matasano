package me.montgome.matasano;

public class ScoredKeysize implements Comparable<ScoredKeysize>{
    public final double score;
    public final int keysize;
    
    public ScoredKeysize(double score, int keysize) {
        this.score = score;
        this.keysize = keysize;
    }

    @Override
    public int compareTo(ScoredKeysize that) {
        return Double.compare(this.score, that.score);
    }
    
    @Override
    public String toString() {
        return String.format("{%s,%s}", keysize, score);
    }
}
