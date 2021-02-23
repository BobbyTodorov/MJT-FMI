package bg.sofia.uni.fmi.mjt.tagger;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Comparator;


public class Tagger {

    private Map<String, String> cityToCountryMap = new LinkedHashMap<>();
    private Map<String, Integer> cityToTagCount = new LinkedHashMap<>();

    /**
     * Creates a new instance of Tagger for a given list of city/country pairs
     *
     * @param citiesReader a java.io.Reader input stream containing list of cities and countries
     *                     in the specified CSV format
     */
    public Tagger(Reader citiesReader) {
        try (BufferedReader reader = new BufferedReader(citiesReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Pair<String, String> ciTyToCountryLine = cityToCountryLineSeparator(line);
                cityToCountryMap.put(ciTyToCountryLine.getLeftElement(), ciTyToCountryLine.getRightElement());
            }
        } catch (IOException e) {
            throw new RuntimeException("Reading from file failed with IOException: " + e);
        }
    }

    /**
     * Processes an input stream of a text file, tags any cities and outputs result
     * to a text output stream.
     *
     * @param text   a java.io.Reader input stream containing text to be processed
     * @param output a java.io.Writer output stream containing the result of tagging
     */
    public void tagCities(Reader text, Writer output) {
        cityToTagCount.clear();
        try (var is = new BufferedReader(text);
                var os = new BufferedWriter(output)) {

            String line;
            boolean isFirstLine = true;
            while ((line = is.readLine()) != null) {
                if (!isFirstLine) {
                    os.newLine();
                } else {
                    isFirstLine = false;
                }

                os.write(tagCitiesInString(line));
            }
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException("tagCities failed due to IOException: " + e);
        }
    }

    /**
     * Returns a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If @n exceeds the total number of cities tagged, return as many as available
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @param n the maximum number of top tagged cities to return
     * @return a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getNMostTaggedCities(int n) {
        Map<Integer, String> sortedMap = new TreeMap<Integer, String>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                int compareToResult = o1.compareTo(o2);
                return switch (compareToResult) {
                    case 0, -1 -> 1;
                    case 1 -> -1;
                    default -> 0;
                };
            }
        });

        for (Map.Entry<String, Integer> entry : cityToTagCount.entrySet()) {
            sortedMap.put(entry.getValue(), entry.getKey());
        }

        Collection<String> result = new ArrayList<>();

        int addedCitiesCount = 0;
        for (Map.Entry<Integer, String> entry : sortedMap.entrySet()) {
            if (addedCitiesCount >= n) {
                break;
            }
            String cityToAdd = entry.getValue();
            cityToAdd = cityToAdd.substring(0, 1).toUpperCase() + cityToAdd.substring(1).toLowerCase();
            if (!result.contains(cityToAdd)) {
                addedCitiesCount++;
            }
            result.add(cityToAdd);
        }
        return result;
    }

    /**
     * Returns a collection of all tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @return a collection of all tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getAllTaggedCities() {
        return cityToTagCount.keySet();
    }

    /**
     * Returns the total number of tagged cities in the input text
     * from the last tagCities() invocation
     * In case a particular city has been taged in several occurences, all must be counted.
     * If tagCities() has not been invoked at all, return 0.
     *
     * @return the total number of tagged cities in the input text
     */
    public long getAllTagsCount() {
        long result = 0;
        for (Integer currentCityTimesTagged : cityToTagCount.values()) {
            result += currentCityTimesTagged;
        }

        return result;
    }


    private Pair<String, String> cityToCountryLineSeparator(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line must not be null.");
        }

        char cityToCountrySeparator = ',';
        int indexOfSeparator = line.indexOf(cityToCountrySeparator);

        return new Pair<>(line.substring(0, indexOfSeparator), line.substring(indexOfSeparator + 1));
    }

    private String tagCitiesInString(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string must not be null.");
        }

        //Working with original line and "work" line in parallel.
        //"work" line is lowercase original line. It is used to compute
        //correct working indexes. Computing on the original line directly would be risky.
        String workString = string.toLowerCase();
        String result = string;

        for (Map.Entry<String, String> cityToCountry : cityToCountryMap.entrySet()) {
            String city = cityToCountry.getKey().toLowerCase();
            int cityStartPosInStr = 0;

            while ((cityStartPosInStr = getCityStartPositionInString(cityStartPosInStr, city, workString)) != -1) {
                increaseCityOccurrenceCount(city);
                int cityEndPosInString = cityStartPosInStr + city.length();
                String originalCityText = result.substring(cityStartPosInStr, cityEndPosInString);

                String textBeforeCity = result.substring(0, cityStartPosInStr);
                String textAfterCity = result.substring(cityEndPosInString);
                result = textBeforeCity + constructCityTag(originalCityText) + textAfterCity;

                //In "work" string, tag the city and replace it with its uppercase version.
                //It will not further be recognised as city, as we work on lowercase only.
                //Tagging it is important because both sizes and structures of original and "work" line
                //must be equal. That way we have relation between indexes of the original and "work" line.
                workString = workString.replaceFirst(city, constructCityTag(originalCityText));
                workString = workString.replaceFirst(originalCityText, city.toUpperCase());
            }
        }
        return result;
    }

    private int getCityStartPositionInString(int fromIndex, String city, String string) {
        if (city == null || string == null) {
            throw new IllegalArgumentException("Null arguments are not allowed.");
        }

        int startPosition = string.indexOf(city, fromIndex);

        if (startPosition == - 1) {
            return -1;
        }

        int endPosition = startPosition + city.length();

        //Handle beginning and ending of a line.
        char charBeforeCity;
        char charAfterCity;
        if (startPosition <= 0) {
            charBeforeCity = '.';
        } else {
            charBeforeCity = string.charAt(startPosition - 1);
        }

        if (endPosition >= string.length()) {
            charAfterCity = '.';
        } else {
            charAfterCity = string.charAt(endPosition);
        }

        //Check surrounding chars.
        if ((charBeforeCity > 'a' && charBeforeCity < 'z')
                || (charBeforeCity > 'A' && charBeforeCity < 'Z')
                || (charAfterCity > 'a' && charAfterCity < 'z')
                || (charAfterCity > 'A' && charAfterCity < 'Z')) {
            return -1;
        }

        return startPosition;
    }

    private String constructCityTag(String city) {
        if (city == null) {
            throw new IllegalArgumentException("City must not be null.");
        }

        return "<city country=\"" + cityToCountryMap.get(city) + "\">" + city + "</city>";
    }

    private void increaseCityOccurrenceCount(String city) {
        if (city == null) {
            throw new IllegalArgumentException("City must not be null.");
        }

        String capitalizedCity = city.substring(0, 1).toUpperCase() + city.substring(1, city.length()).toLowerCase();
        if (cityToTagCount.containsKey(capitalizedCity)) {
            cityToTagCount.put(capitalizedCity, cityToTagCount.get(capitalizedCity) + 1);
        } else {
            cityToTagCount.put(capitalizedCity, 1);
        }
    }

    public Map<String, String> getCitiesToCountryMap() {
        return cityToCountryMap;
    } //test purposes

}