package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;

public class Post extends BaseContentImpl {

    public Post(String author, LocalDateTime publishedOn, String description) {
        super(author, description);
        setExpirationDays(30);
        setPublishedOn(publishedOn);
    }
}
