package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;

public interface BaseContent extends Content{

    void addLike(String likedFromUsername);

    void addComment(String username, String text);

    boolean isExpired();

    void setPublishedOn(LocalDateTime publishedOn);

    void setAuthor(String username);

    void setDescription(String description);
}
