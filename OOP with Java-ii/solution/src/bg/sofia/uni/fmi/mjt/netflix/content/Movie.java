package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

public class Movie extends Stream {
    private final int duration;

    public Movie(String name, Genre genre, PgRating rating, int duration) {
        super(name, genre, rating);
        this.duration = duration;
    }

    @Override
    public int getDuration() {
        return duration;
    }
}
