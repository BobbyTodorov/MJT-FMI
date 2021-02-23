package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

public abstract class Stream implements Streamable{
    protected String name;
    protected Genre genre;
    protected PgRating rating;
    protected int timesWatched;

    public Stream(String name, Genre genre, PgRating rating) {
        this.name = name;
        this.genre = genre;
        this.rating = rating;
        timesWatched = 0;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public PgRating getRating() {
        return rating;
    }

    public void watch() {
        timesWatched++;
    }

    public int getTimesWatched() {
        return timesWatched;
    }
}
