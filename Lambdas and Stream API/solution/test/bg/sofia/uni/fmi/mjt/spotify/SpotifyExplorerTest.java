package bg.sofia.uni.fmi.mjt.spotify;


import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;


public class SpotifyExplorerTest {

    private static final String TEST_TRACKS_BEFORE_90S =
        """
            id,artists,name,year,popularity,duration_ms,tempo,loudness,valence,acousticness,danceability,energy,liveness,speechiness,explicit
            1KxQrwWlYuDupWMVgvS6jG,['Umm Kulthum'],Talat Layaly Al Beaad,1981,0,466387,84.478,-21.617,0.157,0.979,0.187,0.111,0.141,0.0362,0
            73JKaB7cIDhwtLBHSCRZKc,['Artie Shaw'; 'Peg LaCentra'],There's Frost On The Moon,1982,30,175973,161.662,-11.568,0.438,0.993,0.58,0.237,0.246,0.0589,0
            """;

    private static final String TEST_NO_EXPLICIT_TRACKS = """
        id,artists,name,year,popularity,duration_ms,tempo,loudness,valence,acousticness,danceability,energy,liveness,speechiness,explicit
        1Jq5TvM2dTy9oSffrEkvWy,['Tommy Dorsey'],Ya Fouady Eh Yenobak,1996,25,348520,102.745,-15.481,0.506,0.993,0.395,0.186,0.359,0.0401,0
        1KXrq7AyZVeo6QRA2WwCZf,['Эрих Мария Ремарк'],Часть 3.11 - Обратный путь,2000,0,304100,86.037,-1.0,0.462,0.524,0.712,0.135,0.252,0.947,0
        1KXrq7AyZVeo6QRk2WwCZf,['Эрих Мария Ремарк'],Часть 3.11 - Обратный путь2,2000,0,304100,86.037,-1.0,0.462,0.524,0.712,0.135,0.252,0.947,0""";
    private static final String TEST_TRACKS_BASE;

    static {
        TEST_TRACKS_BASE = """
            id,artists,name,year,popularity,duration_ms,tempo,loudness,valence,acousticness,danceability,energy,liveness,speechiness,explicit
            1HvEfn8KE2Yks30WEp5Xdr,['Zofia Dromlewiczowa'],Chapter 3.9 - Dziewczynka z Luna Parku: Część 1,1991,0,108300,76.494,-21.349,0.001,0.628,0.721,0.205,0.216,0.96,0
            1Jq5TvM2dTy9oSffrEkvWy,['Tommy Dorsey'],Ya Fouady Eh Yenobak,1996,25,348520,102.745,-15.481,0.02,0.993,0.395,0.186,0.359,0.0401,0
            1KXrq7AyZVeo6QRA2WwCZf,['Эрих Мария Ремарк'],Часть 3.11 - Обратный путь,1999,30,304100,86.037,-1.0,0.03,0.524,0.712,0.135,0.252,0.947,1
            1KxQrwWlYuDupWMVgvS6jG,['Umm Kulthum'],Talat Layaly Al Beaad,1981,0,466387,84.478,-21.617,0.04,0.979,0.187,0.111,0.141,0.0362,0
            4FmnUrA4cN7bsTuHNuEOqv,['Frankie Carle'; 'Marjorie Hughes'],Little Jack Frost Get Lost (with Marjorie Hughes) - 78rpm Version,1932,31,164160,135.87,-12.5,0.732,0.942,0.779,0.247,0.153,0.0338,0
            6DaE48egpIqB7qTgUHxSG8,['Bing Crosby'; 'Lenny Hayton & His Orchestra'],Brother Can You Spare a Dime? (with Lenny Hayton & His Orchestra),1932,31,192640,84.676,-14.51,0.322,0.987,0.473,0.154,0.114,0.119,0
            73JKaB7cIDhwtLBHSCRZKc,['Artie Shaw'; 'Peg LaCentra'],There's Frost On The Moon,1982,30,175973,161.662,-11.568,0.05,0.993,0.58,0.237,0.246,0.0589,0
            0wXU4MtaCktXgjztldCl40,['Tommy Dorsey'; 'Cliff Weston'; 'Edythe Wright'],Santa Claus is Comin' to Town,1932,23,202520,113.939,-12.396,0.953,0.975,0.83,0.407,0.452,0.197,0
            2a8JpO3JP8atv9bNuVibHS,['Tex Beneke'; 'Ray Eberle'; 'The Modernaires'; 'Paula Kelly'],Sleigh Ride (with Paula Kelly),1932,23,181453,201.613,-12.682,0.873,0.594,0.423,0.417,0.311,0.0404,0
            4zV9dHCtNmachpW19l5rNw,['Benny Goodman'; 'Bunny Berigan'; 'Tommy Dorsey'],Jingle Bells,1932,22,154973,197.581,-13.556,0.964,0.923,0.614,0.28,0.0944,0.0701,0
            1fkAMzPxjmv0WdSfeC06tG,['Henry Hall'; 'The BBC Dance Orchestra'],Lullaby of the Leaves,1932,23,140027,117.278,-18.971,0.438,0.964,0.545,0.14,0.078,0.0438,0
            49yTRhDp8tmOK6UUTdm9FM,['Benny Goodman'],Santa Claus Came in the Spring,1982,22,188600,149.33,-12.903,0.06,0.961,0.581,0.219,0.0811,0.0705,1
            """;
    }

    private static final String[] TEST_TRACKS = TEST_TRACKS_BASE.split("\n");

    @Test(expected = IllegalArgumentException.class)
    public void testConstructingSpotifyExplorerWithNullArgument() {
        SpotifyExplorer se = new SpotifyExplorer(null);
    }

    @Test
    public void testGetAllSpotifyTracksSuccess() {
        Collection<SpotifyTrack> actual;

        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getAllSpotifyTracks();
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        Collection<SpotifyTrack> expected = List.of(
            SpotifyTrack.of(TEST_TRACKS[1]), SpotifyTrack.of(TEST_TRACKS[2]), SpotifyTrack.of(TEST_TRACKS[3]),
            SpotifyTrack.of(TEST_TRACKS[4]), SpotifyTrack.of(TEST_TRACKS[5]), SpotifyTrack.of(TEST_TRACKS[6]),
            SpotifyTrack.of(TEST_TRACKS[7]), SpotifyTrack.of(TEST_TRACKS[8]), SpotifyTrack.of(TEST_TRACKS[9]),
            SpotifyTrack.of(TEST_TRACKS[10]), SpotifyTrack.of(TEST_TRACKS[11]), SpotifyTrack.of(TEST_TRACKS[12]));

        assertArrayEquals("getAllSpotifyTracks must return expected collection",
            expected.toArray(), actual.toArray());
    }

    @Test
    public void testGetAllSpotifyTracksWithNoTracks() {
        Collection<SpotifyTrack> actual;

        try (var is = new BufferedReader(new StringReader("first line"))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getAllSpotifyTracks();
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        Collection<SpotifyTrack> expected = Collections.emptyList();

        assertArrayEquals("getAllSpotifyTracks with no tracks must return empty unmodifiable collection",
            expected.toArray(), actual.toArray());
    }

    @Test
    public void testGetExplicitSpotifyTracksSuccess() {
        Collection<SpotifyTrack> actual;

        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getExplicitSpotifyTracks();
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        Collection<SpotifyTrack> expected = List.of(
            SpotifyTrack.of(TEST_TRACKS[3]),
            SpotifyTrack.of(TEST_TRACKS[12]));

        assertEquals("getExplicitSpotifyTracks must return correct collection", expected, actual);
    }

    @Test
    public void testGetExplicitSpotifyTracksWithNoExplicitTracks() {
        Collection<SpotifyTrack> actual;

        try (var is = new BufferedReader(new StringReader(TEST_NO_EXPLICIT_TRACKS))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getExplicitSpotifyTracks();
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        Collection<SpotifyTrack> expected = Collections.emptyList();

        assertEquals("getExplicitSpotifyTracks with no explicit tracks must return empty collection",
            expected, actual);
    }

    @Test
    public void testGetExplicitSpotifyTracksWithNoTracks() {
        Collection<SpotifyTrack> actual;

        try (var is = new BufferedReader(new StringReader("first line"))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getExplicitSpotifyTracks();
        } catch (IOException e) {
            throw new RuntimeException(" Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        Collection<SpotifyTrack> expected = Collections.emptyList();

        assertEquals("getExplicitSpotifyTracks with no tracks must return empty collection",
            expected, actual);
    }

    @Test
    public void testGroupSpotifyTracksByYearSuccess() {
        Map<Integer, Set<SpotifyTrack>> actual;

        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.groupSpotifyTracksByYear();
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        Map<Integer, Set<SpotifyTrack>> expected = Map.of(
            1991, Set.of(SpotifyTrack.of(TEST_TRACKS[1])),
            1996, Set.of(SpotifyTrack.of(TEST_TRACKS[2])),
            1999, Set.of(SpotifyTrack.of(TEST_TRACKS[3])),
            1981, Set.of(SpotifyTrack.of(TEST_TRACKS[4])),
            1932, Set.of(
                SpotifyTrack.of(TEST_TRACKS[5]),
                SpotifyTrack.of(TEST_TRACKS[6]),
                SpotifyTrack.of(TEST_TRACKS[8]),
                SpotifyTrack.of(TEST_TRACKS[9]),
                SpotifyTrack.of(TEST_TRACKS[10]),
                SpotifyTrack.of(TEST_TRACKS[11])),
            1982, Set.of(
                SpotifyTrack.of(TEST_TRACKS[7]),
                SpotifyTrack.of(TEST_TRACKS[12])));

        assertEquals("groupSpotifyTracksByYear must return correct map", expected, actual);
    }

    @Test
    public void testGroupSpotifyTracksByYearWithNoTracks() {
        Map<Integer, Set<SpotifyTrack>> actual;

        try (var is = new BufferedReader(new StringReader("first line"))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.groupSpotifyTracksByYear();
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        Map<Integer, Set<SpotifyTrack>> expected = Collections.emptyMap();

        assertEquals("groupSpotifyTracksByYear with no tracks must return empty map", expected, actual);
    }

    @Test
    public void testGetArtistActiveYearsSuccess() {
        int actual;
        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getArtistActiveYears("Tommy Dorsey");
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        int expected = 65;

        assertEquals("getArtistActiveYears must return correct int value", expected, actual);
    }

    @Test
    public void testGetArtistActiveYearsWithOneTrackFromArtist() {
        int actual;
        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getArtistActiveYears("The BBC Dance Orchestra");
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        int expected = 1;

        assertEquals("getArtistActiveYears with one track from that artist" +
            " must return 1", expected, actual);
    }

    @Test
    public void testGetArtistActiveYearsWithNoTracksFromArtist() {
        int actual;
        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getArtistActiveYears("Lady Gaga");
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        int expected = 0;

        assertEquals("getArtistActiveYears with no tracks from that artist" +
            " must return 0", expected, actual);
    }

    @Test
    public void testGetArtistActiveYearsWithNoTracksInDataSet() {
        int actual;
        try (var is = new BufferedReader(new StringReader("first line"))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getArtistActiveYears("Somebody");
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        int expected = 0;

        assertEquals("getArtistActiveYears with no tracks in dataset" +
            " must return 0", expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArtistActiveYearsWithNullArtist() {
        int actual;
        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getArtistActiveYears(null);
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        }
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80sWithNBiggerThanNumberOfTracks() {

        Collection<SpotifyTrack> actual;
        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getTopNHighestValenceTracksFromThe80s(10000);
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }


        Collection<SpotifyTrack> expected = List.of(
            SpotifyTrack.of(TEST_TRACKS[12]),
            SpotifyTrack.of(TEST_TRACKS[7]),
            SpotifyTrack.of(TEST_TRACKS[4]));

        assertEquals("getTopNHighestValenceTracksFromThe80s with N>number of tracks must " +
            "return correct collection", expected, actual);
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80sWithNSmallerThanNumberOfTracks() {

        Collection<SpotifyTrack> actual;
        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getTopNHighestValenceTracksFromThe80s(2);
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }


        Collection<SpotifyTrack> expected = List.of(
            SpotifyTrack.of(TEST_TRACKS[12]),
            SpotifyTrack.of(TEST_TRACKS[7]));

        assertEquals("getTopNHighestValenceTracksFromThe80s with N<number of tracks must " +
            "return correct collection", expected, actual);
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80sWith0() {

        Collection<SpotifyTrack> actual;
        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getTopNHighestValenceTracksFromThe80s(0);
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }


        Collection<SpotifyTrack> expected = Collections.emptyList();

        assertEquals("getTopNHighestValenceTracksFromThe80s with N=0 must return empty collection", expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTopNHighestValenceTracksFromThe80sWithNegativeN() {

        Collection<SpotifyTrack> actual;
        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getTopNHighestValenceTracksFromThe80s(-1);
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        }
    }

    @Test
    public void testGetMostPopularTrackFromThe90Success() {
        SpotifyTrack actual;

        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getMostPopularTrackFromThe90s();
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        SpotifyTrack expected = SpotifyTrack.of(TEST_TRACKS[3]);

        assertEquals("getMostPopularTrackFromThe90 must return correct Spotify Track", expected, actual);
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetMostPopularTrackFromThe90WithNoTracksFromThe90S() {
        SpotifyTrack actual;

        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BEFORE_90S))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getMostPopularTrackFromThe90s();
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        }
    }

    @Test
    public void testGetNumberOfLongerTracksBeforeYearSuccess() {
        long actual;

        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getNumberOfLongerTracksBeforeYear(3, 1980);
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        long expected = 3;

        assertEquals("getNumberOfLongerTracksBeforeYear must return expected value", expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberOfLongerTracksBeforeYearWithNegativeMinutes() {
        long actual;

        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getNumberOfLongerTracksBeforeYear(-1, 1980);
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberOfLongerTracksBeforeYearWithNegativeYear() {
        long actual;

        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getNumberOfLongerTracksBeforeYear(4, -1);
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        }
    }

    @Test
    public void testGetTheLoudestTrackInYearSuccess() {
        Optional<SpotifyTrack> actual;

        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getTheLoudestTrackInYear(1932);
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        } catch (Exception e) {
            throw new RuntimeException("Calling function failed due to: " + e);
        }

        Optional<SpotifyTrack> expected = Optional.of(SpotifyTrack.of(TEST_TRACKS[8]));

        assertEquals("getTheLoudestTrackInYear must return correct optional of spotify track", expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTheLoudestTrackInYearWithNegativeYear() {
        Optional<SpotifyTrack> actual;

        try (var is = new BufferedReader(new StringReader(TEST_TRACKS_BASE))) {
            SpotifyExplorer se = new SpotifyExplorer(is);
            actual = se.getTheLoudestTrackInYear(-1);
        } catch (IOException e) {
            throw new RuntimeException("Reading tracks failed due to: " + e);
        }
    }
}
