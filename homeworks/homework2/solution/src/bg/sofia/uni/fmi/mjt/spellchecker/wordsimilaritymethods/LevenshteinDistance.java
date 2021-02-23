package bg.sofia.uni.fmi.mjt.spellchecker.wordsimilaritymethods;

public final class LevenshteinDistance {
    //Levenshtein Distance - can be called in NaiveSpellChecker#findClosestWord
    //This software does not contain tests for Levenshtein Distance.

    private LevenshteinDistance() {
    }

    public static double calcLevenshteinDistance(String word1, String word2) {
        //dynamic programming method
        double[][] matrix = new double[word1.length() + 1][word2.length() + 1];

        int word1Length = word1.length();
        int word2Length = word2.length();
        for (int i = 0; i <= word1Length; ++i) {
            for (int j = 0; j <= word2Length; ++j) {
                if (i == 0) {
                    matrix[i][j] = j;
                } else if (j == 0) {
                    matrix[i][j] = i;
                } else {
                    matrix[i][j] = Math.min(Math.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1),
                        matrix[i - 1][j - 1]
                            + levenshteinDistanceCostOfSubstitution(word1.charAt(i - 1), word2.charAt(j - 1)));
                }
            }
        }

        return matrix[word1Length][word2Length];
    }

    private static int levenshteinDistanceCostOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
}
