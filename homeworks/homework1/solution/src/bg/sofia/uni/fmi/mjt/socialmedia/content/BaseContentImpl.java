package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public abstract class BaseContentImpl implements BaseContent {

    private int expirationDays;
    private static int numberOfContents = 0;
    private int numberOfLikes;
    private int numberOfComments;
    private String author;
    private String string;
    private List<String> tags = null;
    private List<String> mentions = null;
    private String id;
    private LocalDateTime publishedOn;

    public BaseContentImpl(String author, String description) {
        setAuthor(author);
        setDescription(description);

        numberOfContents++;

        generateMentions(description);
        generateTags(description);
        generateId();
    }

    @Override
    public void addLike(String likedFromUsername) {
        if (likedFromUsername == null) {
            throw new IllegalArgumentException("likedFromUsername must not be null");
        }

        numberOfLikes++;
    }

    @Override
    public void addComment(String username, String text) {
        if (username == null || text == null) {
            throw new IllegalArgumentException("addComment takes non-null arguments");
        }

        numberOfComments++;
    }

    @Override
    public boolean isExpired() {
        return LocalDateTime.now().minusDays(expirationDays).isAfter(publishedOn);
    }

    @Override
    public void setPublishedOn(LocalDateTime publishedOn) {
        if (publishedOn.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("content cannot be published in the future");
        }

        this.publishedOn = publishedOn;
    }

    @Override
    public void setAuthor(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username must not be null");
        }

        this.author = username;
    }

    @Override
    public void setDescription(String description) {
        if (description == null) {
            throw new IllegalArgumentException("description must not be null");
        }

        this.string = description;
    }

    protected void setExpirationDays(int days) {
        this.expirationDays = days;
    }


    @Override
    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    @Override
    public int getNumberOfComments() {
        return numberOfComments;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Collection<String> getTags() {
        return tags;
    }

    @Override
    public Collection<String> getMentions() {
        return mentions;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return string;
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public static int getNumberOfContents() {
        return numberOfContents;
    }

    private Collection<String> getSubstringsStartingWith(String substringStartChar, String string) {
        string = " " + string + " ";
        List<String> resultSubstrings = new ArrayList<>();
        String[] splitString;
        splitString = string.split(" ");
        for (String str : splitString) {
            if (str.startsWith(substringStartChar)) {
                resultSubstrings.add(str);
            }
        }

        return resultSubstrings;
    }

    private void generateTags(String description) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }

        this.tags = (List<String>) getSubstringsStartingWith("#", description);
    }

    private void generateMentions(String description) {
        if (this.mentions == null) {
            this.mentions = new ArrayList<>();
        }

        this.mentions = (List<String>) getSubstringsStartingWith("@", description);
    }

    private void generateId() {
        this.id = getAuthor() + "-" + getNumberOfContents();
    }
}
