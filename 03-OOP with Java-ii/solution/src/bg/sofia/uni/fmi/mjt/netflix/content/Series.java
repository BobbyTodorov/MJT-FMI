package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

public class Series extends Stream {
    private final Episode[] episodes;

    public Series(String name, Genre genre, PgRating rating, Episode[] episodes) {
        super(name,genre,rating);
        this.episodes = episodes;
    }

    public Episode[] getEpisodes() {
        return episodes;
    }

    @Override
    public int getDuration() {
        int duration = 0;
        for (Episode ep: episodes) {
            duration += ep.duration();
        }
        return duration;
    }
}
