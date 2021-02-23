package bg.sofia.uni.fmi.mjt.tagger;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;


public class TaggerTest {

    private final String lineSeparator = System.lineSeparator();
    private final static Path worldCitiesDataBasePath = Path.of("world-cities.csv");
    private static Tagger globalTaggerObject;


    @BeforeClass
    public static void Setup() {
        try(var is = Files.newBufferedReader(worldCitiesDataBasePath)) {
            globalTaggerObject = new Tagger(is);
        } catch (IOException e) {
            throw new RuntimeException("Setup failed due to IOException: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Initializing global tagger failed due to: " + e);
        }
    }

    @Test
    public void testTaggerConstruction() {
        //Expected value construction.
        Map<String, String> expected = new LinkedHashMap<>();
        try (var fr = Files.newBufferedReader(worldCitiesDataBasePath)) {
            String line;
            while ((line = fr.readLine()) != null) {
                char cityToCountrySeparator = ',';
                int indexAfterSeparator = line.indexOf(cityToCountrySeparator);

                expected.put(line.substring(0, indexAfterSeparator), line.substring(indexAfterSeparator + 1));
            }
        } catch(IOException e) {
            throw new RuntimeException("Expected value construction failed with IOException: " + e);
        }

        //Actual value construction.
        Map<String, String> actual;
        try(var fr = Files.newBufferedReader(worldCitiesDataBasePath)) {
            Tagger tagger = new Tagger(fr);
            actual = tagger.getCitiesToCountryMap();
        } catch (IOException e) {
            throw new RuntimeException("Actual value construction failed with IOException: " +e);
        }

        assertEquals("Constructor must construct correct Map.", expected, actual);
    }

    @Test
    public void testTagCitiesCorrectness() {
        final String INPUT_STRING = "Plovdiv's old town is a major tourist attraction. It is the second largest city" +
                lineSeparator + "in Bulgaria, after the capital ,Sofia." + lineSeparator + "Sofia Varna";

        String TEST_OUTPUT_STRING;
        final String CORRECT_OUTPUT_STRING = "<city country=\"Bulgaria\">Plovdiv</city>'s old town is a major tourist attraction. It is the second largest city" +
                lineSeparator + "in Bulgaria, after the capital ,<city country=\"Bulgaria\">Sofia</city>." + lineSeparator +
                "<city country=\"Bulgaria\">Sofia</city> <city country=\"Bulgaria\">Varna</city>";

        try (var is = new BufferedReader(new StringReader(INPUT_STRING));
             var os = new StringWriter()) {
            globalTaggerObject.tagCities(is, os);
            TEST_OUTPUT_STRING = os.toString();
        } catch (IOException e) {
            throw new RuntimeException("Calling tagCities (correctness test) failed due to IOException: " + e);
        }

        assertEquals("tagCitiesCorrectness must return expected value", CORRECT_OUTPUT_STRING, TEST_OUTPUT_STRING);
    }

    @Test
    public void testGetNMostTaggedCitiesWithArgumentHigherThanNumberOfCities() {
        final String INPUT_STRING = "Plovdiv's old town is a major tourist attraction. It is the second largest city" +
                lineSeparator + "in Bulgaria, after the capital ,Sofia." + lineSeparator +
                "Sofia Varna Sofia Plovdiv";

        Collection<String> actual;
        try (var is = new BufferedReader(new StringReader(INPUT_STRING));
             var os = new StringWriter()) {
            globalTaggerObject.tagCities(is, os);
            actual = globalTaggerObject.getNMostTaggedCities(5);
        } catch (IOException e) {
            throw new RuntimeException("getNMostTaggedCities (n>Cities) failed due to IOException: " + e);
        }

        Collection<String> expected = new ArrayList<>();
        expected.add("Sofia");
        expected.add("Plovdiv");
        expected.add("Varna");

        assertEquals("NMostTaggedCities didn't return correct Collection for n > N of cities", expected, actual);
    }

    @Test
    public void testGetNMostTaggedCitiesWithArgumentLowerThanNumberOfCities() {
        final String INPUT_STRING = "Plovdiv's old town is a major tourist attraction. It is the second largest city" +
                lineSeparator + "in Bulgaria, after the capital ,Sofia." + lineSeparator +
                "Sofia Varna";

        Collection<String> actual;
        try (var is = new BufferedReader(new StringReader(INPUT_STRING));
             var os = new StringWriter()) {
            globalTaggerObject.tagCities(is, os);
            actual = globalTaggerObject.getNMostTaggedCities(2);
        } catch (IOException e) {
            throw new RuntimeException("getNMostTaggedCities (n<Cities) failed due to IOException: " + e);
        }

        Collection<String> expected = new ArrayList<>();
        expected.add("Sofia");
        expected.add("Plovdiv");

        assertEquals("NMostTaggedCities didn't return correct Collection for n < N of cities", expected, actual);
    }

    @Test
    public void testGetNMostTaggedCitiesWithNoCities() {
        final String INPUT_STRING = "old town is a major tourist attraction. It is the second largest city" +
                lineSeparator + "in Bulgaria, after the capital." + lineSeparator;

        Collection<String> actual;
        try (var is = new BufferedReader(new StringReader(INPUT_STRING));
             var os = new StringWriter()) {
            globalTaggerObject.tagCities(is, os);
            actual = globalTaggerObject.getNMostTaggedCities(2);
        } catch (IOException e) {
            throw new RuntimeException("getNMostTaggedCities (with no cities) failed due to IOException: " + e);
        }

        Collection<String> expected = new ArrayList<>();

        assertEquals("NMostTaggedCities didn't return empty collection", expected, actual);
    }

    @Test
    public void testGetNMostTaggedCitiesWithArgumentZero() {
        final String INPUT_STRING = "Plovdiv's old town is a major tourist attraction. It is the second largest city" +
                lineSeparator + "in Bulgaria, after the capital ,Sofia." + lineSeparator + "Sofia Varna";

        Collection<String> actual;
        try (var is = new BufferedReader(new StringReader(INPUT_STRING));
             var os = new StringWriter()) {
            globalTaggerObject.tagCities(is, os);
            actual = globalTaggerObject.getNMostTaggedCities(0);
        } catch (IOException e) {
            throw new RuntimeException("getNMostTaggedCities (with argument 0) failed due to IOException: " + e);
        }

        Collection<String> expected = new ArrayList<>();

        assertEquals("NMostTaggedCities didn't return empty collection", expected, actual);
    }

    @Test
    public void testGetAllTaggedCitiesCorrectness() {
        final String INPUT_STRING = "Plovdiv's old town is a major tourist attraction. It is the second largest city" +
                lineSeparator + "in Bulgaria, after the capital ,Sofia." + lineSeparator + "Sofia Varna";

        Collection<String> tempCol;
        try (var is = new BufferedReader(new StringReader(INPUT_STRING));
             var os = new StringWriter()) {
            globalTaggerObject.tagCities(is, os);
            tempCol = globalTaggerObject.getAllTaggedCities();
        } catch (IOException e) {
            throw new RuntimeException("getAllTaggedCities correctness test failed due to IOException: " + e);
        }

        List<String> expected = new ArrayList<>();
        expected.add("Plovdiv");
        expected.add("Sofia");
        expected.add("Varna");

        List<String> actual = new ArrayList<>(tempCol);

        Collections.sort(actual);
        Collections.sort(expected);

        assertEquals("getAllTaggedCities didn't return expected collection", expected, actual);
    }

    @Test
    public void testGetAllTaggedCitiesWithNoCities() {
        final String INPUT_STRING = "old town is a major tourist attraction. It is the second largest city" +
                lineSeparator + "in Bulgaria, after the capital" + lineSeparator;

        Collection<String> tempCol;
        try (var is = new BufferedReader(new StringReader(INPUT_STRING));
             var os = new StringWriter()) {
            globalTaggerObject.tagCities(is, os);
            tempCol = globalTaggerObject.getAllTaggedCities();
        } catch (IOException e) {
            throw new RuntimeException("getAllTaggedCities (with no cities) test failed due to IOException: " + e);
        }

        List<String> actual = new ArrayList<>(tempCol);
        List<String> expected = new ArrayList<>();

        assertEquals("getAllTaggedCities didn't return empty collection", expected, actual);
    }

    @Test
    public void testGetAllTagsCountCorrectness() {
        final String INPUT_STRING = "Plovdiv's old town is a major tourist attraction. It is the second largest city" +
                lineSeparator + "in Bulgaria, after the capital ,Sofia." + lineSeparator + "Sofia Varna";

        long actual;
        try (var is = new BufferedReader(new StringReader(INPUT_STRING));
             var os = new StringWriter()) {
            globalTaggerObject.tagCities(is, os);
            actual = globalTaggerObject.getAllTagsCount();
        } catch (IOException e) {
            throw new RuntimeException("getAllTagsCount correctness test failed due to IOException: " + e);
        }

        long expected = 4;

        assertEquals("getAllTagsCount didn't return expected value", expected, actual);
    }

    @Test
    public void testGetAllTagsCountWithNoCities() {
        final String INPUT_STRING = "asd asd fdfg gd" + lineSeparator + " fdgd h.fd ";

        long actual;
        try (var is = new BufferedReader(new StringReader(INPUT_STRING));
             var os = new StringWriter()) {
            globalTaggerObject.tagCities(is, os);
            actual = globalTaggerObject.getAllTagsCount();
        } catch (IOException e) {
            throw new RuntimeException("Calling getAllTagsCount (with no cities) test failed due to IOException: " + e);
        }

        long expected = 0;

        assertEquals("getAllTagsCount didn't return 0", expected, actual);
    }
}
