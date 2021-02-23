package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;

public class Story extends BaseContentImpl {

    public Story(String author, LocalDateTime publishedOn, String description) {
        super(author, description);
        setExpirationDays(1);
        setPublishedOn(publishedOn);
    }
}