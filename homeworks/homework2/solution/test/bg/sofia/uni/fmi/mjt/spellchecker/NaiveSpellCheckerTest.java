package bg.sofia.uni.fmi.mjt.spellchecker;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NaiveSpellCheckerTest {

    //These tests test Naive Spell Checker based on Cosine Similarity only.

    private static final String METADATA_TITLE = "= = = Metadata = = =";
    private static final String FINDINGS_TITLE = "= = = Findings = = =";
    private static final String NL = System.lineSeparator();
    private static final String TEST_DICTIONARY =
        "dictword1"
            + NL + "  !DiCtWoRd2"
            + NL + "dictword3$#@"
            + NL + "dictword4!@  "
            + NL + "  @#DICTWORD5,"
            + NL + "dict_word6"
            + NL + "d"
            + NL + "!d@ ";
    private static final String TEST_STOPWORDS =
        "stopword1"
            + NL + "  stopWORD2"
            + NL + "sToPwOrD3   "
            + NL + "   STOPword4   ";

    private static Reader dictionaryReader;
    private static Reader stopwordsReader;
    private static NaiveSpellChecker naiveSpellChecker;

    @BeforeClass
    public static void setup() {
        dictionaryReader = new StringReader(TEST_DICTIONARY);
        stopwordsReader = new StringReader(TEST_STOPWORDS);
        naiveSpellChecker = new NaiveSpellChecker(dictionaryReader, stopwordsReader);
    }

    @AfterClass
    public static void cleanup() throws IOException {
        dictionaryReader.close();
        stopwordsReader.close();
    }

    @Test //from github
    public void compilationTest() throws IOException {
        Reader dictionaryReader = new StringReader(String.join(System.lineSeparator(), List.of("cat", "dog", "bird")));
        Reader stopwordsReader = new StringReader(String.join(System.lineSeparator(), List.of("a", "am", "me")));

        // 1. constructor
        SpellChecker spellChecker = new NaiveSpellChecker(dictionaryReader, stopwordsReader);

        // 2. findClosestWords()
        spellChecker.findClosestWords("hello", 2);

        // 3. metadata()
        Reader catTextReader = new StringReader("hello, i am a cat!");
        Metadata metadata = spellChecker.metadata(catTextReader);
        metadata.characters();
        metadata.words();
        metadata.mistakes();

        // 4. analyze()
        Reader dogTextReader = new StringReader("hello, i am a dog!");
        Writer output = new FileWriter("output.txt");
        spellChecker.analyze(dogTextReader, output, 2);
        dictionaryReader.close();
        stopwordsReader.close();
        dogTextReader.close();
        output.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructionWithNullDictionaryReaderArgument() {
        new NaiveSpellChecker(null, new StringReader(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructionWithNullStopwordsReaderArgument() {
        new NaiveSpellChecker(new StringReader(""), null);
    }

    @Test
    public void testMetadataSuccess() {
        String assertMessage = "Metadata does not return expected value.";
        Metadata actual = null;

        String inputText = "dictword1$%, dictword2 mistake1 mistake2!" + NL + "d  stopword1, !@#$ mistake3...";
        try (Reader r = new StringReader(inputText)) {
            actual = naiveSpellChecker.metadata(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Metadata expected = new Metadata(64, 6, 4);
        assertEquals(assertMessage, expected, actual);
    }

    @Test
    public void testMetadataWithNoText() {
        String assertMessage = "Metadata with no text does not return expected value.";
        Metadata actual = null;

        String inputText = "";
        try (Reader r = new StringReader(inputText)) {
            actual = naiveSpellChecker.metadata(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Metadata expected = new Metadata(0, 0, 0);
        assertEquals(assertMessage, expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMetadataWithNullArgument() {
        naiveSpellChecker.metadata(null);
    }

    @Test
    public void testAnalyzeSuccess() {
        String assertMessage = "Analyze does not return expected value.";
        String actual = null;
        String inputText = "d dictword1$%, dictword2 dictword12 dictword22" + NL + "stopword1, !@#$ dict_word6...";
        try (Reader r = new StringReader(inputText);
             Writer w = new StringWriter()) {
            naiveSpellChecker.analyze(r, w, 2);
            actual = w.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String expected =
            "d dictword1$%, dictword2 dictword12 dictword22"
                + NL + "stopword1, !@#$ dict_word6..."
                + NL + METADATA_TITLE
                + NL + "69 characters, 6 words, 3 spelling issue(s) found"
                + NL + FINDINGS_TITLE
                + NL + "Line #1, {d} - Possible suggestions are {dictword2, dictword3}"
                + NL + "Line #1, {dictword12} - Possible suggestions are {dictword1, dictword2}"
                + NL + "Line #1, {dictword22} - Possible suggestions are {dictword2, dictword3}";

        assertEquals(assertMessage, expected, actual);
    }

    @Test
    public void testAnalyzeWithNoSpellingIssues() {
        String assertMessage = "Analyze with no spelling issues does not return expected value.";
        String actual = null;
        String inputText = "dictword1$% stopword1, !@#$";
        try (Reader r = new StringReader(inputText);
             Writer w = new StringWriter()) {
            naiveSpellChecker.analyze(r, w, 1);
            actual = w.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String expected =
            "dictword1$% stopword1, !@#$"
                + NL + METADATA_TITLE
                + NL + "25 characters, 1 words, 0 spelling issue(s) found"
                + NL + FINDINGS_TITLE
                + NL + "No spelling issues found.";

        assertEquals(assertMessage, expected, actual);
    }

    @Test
    public void testAnalyzeWithEmptyString() {
        String assertMessage = "Analyze with empty string does not return expected value.";
        String actual = null;
        String inputText = "";
        try (Reader r = new StringReader(inputText);
             Writer w = new StringWriter()) {
            naiveSpellChecker.analyze(r, w, 1);
            actual = w.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String expected =
            ""
                + NL + METADATA_TITLE
                + NL + "0 characters, 0 words, 0 spelling issue(s) found"
                + NL + FINDINGS_TITLE
                + NL + "No spelling issues found.";

        assertEquals(assertMessage, expected, actual);
    }

    @Test
    public void testAnalyzeWithNoSuggestions() {
        String assertMessage = "Analyze with no suggestions does not return expected value.";
        String actual = null;
        String inputText = "dictword12#@ stopword1 $%#@ ";
        try (Reader r = new StringReader(inputText);
             Writer w = new StringWriter()) {
            naiveSpellChecker.analyze(r, w, 0);
            actual = w.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String expected =
            "dictword12#@ stopword1 $%#@ "
                + NL + METADATA_TITLE
                + NL + "25 characters, 1 words, 1 spelling issue(s) found"
                + NL + FINDINGS_TITLE
                + NL + "Line #1, {dictword12#@}";

        assertEquals(assertMessage, expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeWithNullReaderArgument() {
        naiveSpellChecker.analyze(null, new StringWriter(), 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeWithNullWriterArgument() {
        naiveSpellChecker.analyze(new StringReader(""), null, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeWithNegativeSuggestionsCountArgument() {
        naiveSpellChecker.analyze(new StringReader(""), new StringWriter(), -1);
    }

    @Test
    public void testFindClosestWordsSuccess() {
        String assertMessage = "findClosestWords did not return correct list.";
        List<String> actual = naiveSpellChecker.findClosestWords("dictword1%", 2);
        List<String> expected = new ArrayList<>();
        expected.add("dictword1");
        expected.add("dictword2");
        assertEquals(assertMessage, expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsWithNullWord() {
        naiveSpellChecker.findClosestWords(null, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsWithNegativeSuggestionsCount() {
        naiveSpellChecker.findClosestWords("", -2);
    }

    @Test
    public void testFindClosestWordsWithZeroSuggestionsCount() {
        String assertMessage = "findClosestWords with zero suggestionCount did not return an empty list.";
        List<String> actual = naiveSpellChecker.findClosestWords("", 0);
        assertEquals(assertMessage, Collections.emptyList(), actual);
    }

}