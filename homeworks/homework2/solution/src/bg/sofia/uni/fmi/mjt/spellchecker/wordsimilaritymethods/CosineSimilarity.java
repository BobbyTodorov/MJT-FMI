package bg.sofia.uni.fmi.mjt.spellchecker.wordsimilaritymethods;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class CosineSimilarity {
    //Cosine Similarity - called in NaiveSpellChecker#findClosestWord

    private CosineSimilarity() {
    }

    public static double calcCosineSimilarity(String word1, String word2) {
        LinkedHashMap<String, Integer> word1VectorOfBigrams = getWordBigramsVector(word1);
        double word1VectorOfBigramsLength = calcVectorOfBigramsLength(word1VectorOfBigrams);

        LinkedHashMap<String, Integer> word2VectorOfBigrams = getWordBigramsVector(word2);
        double word2VectorOfBigramsLength = calcVectorOfBigramsLength(word2VectorOfBigrams);

        return multiplyTwoVectorsOfBigrams(word1VectorOfBigrams, word2VectorOfBigrams)
            / (word1VectorOfBigramsLength * word2VectorOfBigramsLength);
    }

    private static int multiplyTwoVectorsOfBigrams(LinkedHashMap<String, Integer> v1,
                                                   LinkedHashMap<String, Integer> v2) {
        AtomicInteger result = new AtomicInteger();
        v1.forEach((k, v) -> {
            if (v2.containsKey(k)) {
                result.addAndGet(v2.get(k));
            }
        });
        return result.get();
    }

    private static double calcVectorOfBigramsLength(LinkedHashMap<String, Integer> vectorOfBigrams) {
        AtomicReference<Double> result = new AtomicReference<>((double) 0);
        vectorOfBigrams.forEach((v, k) -> result.updateAndGet(v1 -> v1 + k * k));
        return Math.sqrt(result.get());
    }

    private static LinkedHashMap<String, Integer> getWordBigramsVector(String word) {
        LinkedHashMap<String, Integer> bigramsToCountMap = new LinkedHashMap<>();
        int lastBigramEndIndex = word.length() - 1;
        for (int i = 0; i < lastBigramEndIndex; ++i) {
            String bigram = word.substring(i, i + 2);
            if (bigramsToCountMap.containsKey(bigram)) {
                bigramsToCountMap.put(bigram, bigramsToCountMap.get(bigram) + 1);
            } else {
                bigramsToCountMap.put(bigram, 1);
            }
        }
        return bigramsToCountMap;
    }
}
