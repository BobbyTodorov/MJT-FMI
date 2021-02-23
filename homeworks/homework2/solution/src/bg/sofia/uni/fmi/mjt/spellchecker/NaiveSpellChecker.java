package bg.sofia.uni.fmi.mjt.spellchecker;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Scanner;

import java.util.function.Function;
import java.util.stream.Collectors;

import static bg.sofia.uni.fmi.mjt.spellchecker.wordsimilaritymethods.CosineSimilarity.calcCosineSimilarity;

public class NaiveSpellChecker implements SpellChecker {

    private static final String METADATA_TITLE = "= = = Metadata = = =";
    private static final String FINDINGS_TITLE = "= = = Findings = = =";
    private static final int TEXT_LINE_COUNTER_BEGIN_VALUE = 1;

    private final Set<String> dictionary = new LinkedHashSet<>();
    private final Set<String> stopwords = new LinkedHashSet<>();


    public NaiveSpellChecker(Reader dictionaryReader, Reader stopwordsReader) {
        if (dictionaryReader == null) {
            throw new IllegalArgumentException("Argument dictionaryReader must not be null.");
        }

        if (stopwordsReader == null) {
            throw new IllegalArgumentException("Argument stopwordsReader must not be null.");
        }

        constructDictionary(dictionaryReader);
        constructStopwords(stopwordsReader);
    }

    private void constructDictionary(Reader dictionaryReader) {
        try {
            var dr = new BufferedReader(dictionaryReader);
            String line;
            while ((line = dr.readLine()) != null) {
                String word = reformatToSpellCheckerWord(line);
                if (word.length() <= 1) {
                    continue;
                }

                dictionary.add(word);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed during reading dictionary data.");
        } catch (Exception e) {
            throw new RuntimeException("Failed during storing dictionary data.");
        }
    }

    private void constructStopwords(Reader stopwordsReader) {
        try {
            var sr = new BufferedReader(stopwordsReader);
            String line;
            while ((line = sr.readLine()) != null) {
                stopwords.add(removeLeadingAndTrailingNonAlphanumericCharacters(line.trim().toLowerCase()));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed during reading stopwords data.");
        } catch (Exception e) {
            throw new RuntimeException("Failed during storing stopwords data.");
        }
    }


    @Override
    public void analyze(Reader textReader, Writer output, int suggestionsCount) {
        if (textReader == null) {
            throw new IllegalArgumentException("Argument textReader must not be null.");
        }
        if (output == null) {
            throw new IllegalArgumentException("Argument output must not be null.");
        }
        if (suggestionsCount < 0) {
            throw new IllegalArgumentException("Argument suggestionsCount must be positive int.");
        }
        try (var outputStream = new PrintWriter(output)) {
            var inputStream = new StringReader(new BufferedReader(textReader).lines()
                .collect(Collectors.joining(System.lineSeparator())));

            String text = readTextFromStringReader(inputStream);
            outputStream.write(text + System.lineSeparator());
            Metadata metadata;
            try (var metadataInput = new StringReader(text)) {
                metadata = metadata(metadataInput);
            }
            writeMetadataToStream(metadata, outputStream);
            List<String> findings = getFindingsLines(text, suggestionsCount);
            writeFindingsToStream(findings, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("I/O streaming failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Analyzing text failed due to: " + e);
        }
    }

    private String readTextFromStringReader(StringReader inputStream) {
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }


    @Override
    public Metadata metadata(Reader textReader) {
        if (textReader == null) {
            throw new IllegalArgumentException("Argument textReader must not be null.");
        }
        StringReader inputStream = (StringReader) textReader;
        String text = readTextFromStringReader(inputStream);

        return new Metadata(getNumberOfCharacters(text), getNumberOfWords(text),
            getNumberOfMistakes(text));
    }

    private int getNumberOfCharacters(String inputText) {
        return inputText.lines()
            .collect(Collectors.joining())
            .replaceAll("\\s+", "")
            .length();
    }

    private int getNumberOfWords(String inputString) {

        List<String> wordsInText = Arrays.stream(inputString.lines()
            .collect(Collectors.joining(" "))
            .split(" "))
            .collect(Collectors.toList());

        return (int) wordsInText.stream()
            .filter(w -> !isNonAlphaNumericWord(w) && !stopwords.contains(reformatToSpellCheckerWord(w)))
            .count();
    }

    private int getNumberOfMistakes(String inputString) {
        String[] wordsInText = inputString.split(" |\r\n|\n");
        int result = 0;
        for (String word : wordsInText) {
            String spellCheckerFormatWord = reformatToSpellCheckerWord(word);
            if (!dictionary.contains(spellCheckerFormatWord)
                && !stopwords.contains(spellCheckerFormatWord)
                && !spellCheckerFormatWord.equals("")) { // "" in case of non-alphanumeric unprocessed word
                result++;
            }
        }
        return result;
    }

    private void writeMetadataToStream(Metadata metadata, PrintWriter outputStream) {
        outputStream.println(METADATA_TITLE);
        outputStream.write(metadata.characters() + " characters, "
            + metadata.words() + " words, " + metadata.mistakes()
            + " spelling issue(s) found" + System.lineSeparator());
    }


    private boolean isNonAlphaNumericWord(String word) {
        int wordLength = word.length();
        for (int i = 0; i < wordLength; ++i) {
            char charAtI = word.charAt(i);
            if ((charAtI > 'a' && charAtI < 'z')
                || (charAtI > 'A' && charAtI < 'Z')
                || (charAtI > 0 && charAtI < 9)) {
                return false;
            }
        }
        return true;
    }

    private String reformatToSpellCheckerWord(String word) {
        return removeLeadingAndTrailingNonAlphanumericCharacters(word.trim().toLowerCase());
    }

    private String removeLeadingAndTrailingNonAlphanumericCharacters(String string) {
        if (isNonAlphaNumericWord(string)) {
            return "";
        }

        String resultString = removeLeadingNonAlphanumericChars(string);
        return removeTrailingNonAlphanumericChars(resultString);
    }

    private String removeLeadingNonAlphanumericChars(String string) {
        StringBuilder resultString = new StringBuilder(string);
        while (true) {
            char charAtI = resultString.charAt(0);
            if ((charAtI >= 'a' && charAtI <= 'z')
                || (charAtI >= '0' && charAtI <= '9')) {
                break;
            } else {
                resultString.deleteCharAt(0);
            }
        }
        return resultString.toString();
    }

    private String removeTrailingNonAlphanumericChars(String string) {
        StringBuilder resultString = new StringBuilder(string);
        int lastStringIndex = string.length() - 1;
        while (true) {
            char charAtI = resultString.charAt(lastStringIndex);
            if ((charAtI >= 'a' && charAtI <= 'z')
                || (charAtI >= '0' && charAtI <= '9')) {
                break;
            } else {
                resultString.deleteCharAt(lastStringIndex--);
            }
        }
        return resultString.toString();
    }


    private List<String> getFindingsLines(String text, int suggestionsCount)
            throws IOException {
        List<String> result = new ArrayList<>();
        int numberOfLine = TEXT_LINE_COUNTER_BEGIN_VALUE;
        String[] lines = text.split(System.lineSeparator());
        for (String line : lines) {
            String inputLineFindings = getFindingsForLine(line, numberOfLine++, suggestionsCount);
            if (!inputLineFindings.equals("")) {
                result.add(inputLineFindings);
            }
        }
        if (result.isEmpty()) {
            result.add("No spelling issues found.");
        }
        return result;
    }

    private String getFindingsForLine(String inputLine, int numberOfLine, int suggestionsCount) {
        String[] wordsInLine = inputLine.split(" ");
        StringBuilder outputLine = new StringBuilder();
        for (String word : wordsInLine) {
            String wordInSpellCheckerFormat = reformatToSpellCheckerWord(word);
            if (isMisspelledWord(wordInSpellCheckerFormat)) {
                outputLine.append(buildFindingLine(numberOfLine, word, suggestionsCount));
            }
        }
        return outputLine.toString();
    }

    private boolean isMisspelledWord(String word) {
        return !dictionary.contains(word) && !stopwords.contains(word) && !word.equals("");
    }

    private StringBuilder buildFindingLine(int numberOfLine, String word, int suggestionsCount) {
        StringBuilder findingLine = new StringBuilder();
        findingLine.append("Line #").append(numberOfLine).append(", {").append(word)
            .append("}");
        if (suggestionsCount > 0) {
            findingLine.append(" - Possible suggestions are {");

            findingLine.append(String.join(", ", findClosestWords(word, suggestionsCount)));

            int findingLineLength = findingLine.length();
            findingLine.delete(findingLineLength - 2, findingLineLength); // delete last ", "
            findingLine.append("}").append(System.lineSeparator());
        }
        return findingLine;
    }

    @Override
    public List<String> findClosestWords(String word, int n) {
        if (word == null) {
            throw new IllegalArgumentException("Argument word must not be null.");
        }
        if (n < 0) {
            throw new IllegalArgumentException("Calling with n <= 0 is unnecessary.");
        }
        if (n == 0) {
            return Collections.emptyList();
        }

        Map<String, Double> dictWordsToCosSim = dictionary.stream()
            .collect(Collectors.toMap(Function.identity(), x -> calcCosineSimilarity(x, word)));

        return dictWordsToCosSim.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(n)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private void writeFindingsToStream(List<String> findings, PrintWriter outputStream) {
        outputStream.println(FINDINGS_TITLE);
        int lastIndexOfFindings = findings.size() - 1;
        int findingsCounter = 0;
        for (String finding : findings) {
            findingsCounter++;
            if (findingsCounter < lastIndexOfFindings) {
                outputStream.write(finding);
            } else {
                outputStream.write(finding.stripTrailing()); //remove new line at the end of the file
            }
        }
    }
}
