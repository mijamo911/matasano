package me.montgome.matasano;

public class Scorer {
    public static double score(String s) {
        int points = 0;
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c) || c == ' ' || c == '\n' || c == ',' || c == '\'') {
                points++;
            } else {
                points--;
            }
        }
        
        return (double) points / (double) s.length();
    }
}
