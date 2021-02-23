package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.content.BaseContent;
import bg.sofia.uni.fmi.mjt.socialmedia.content.BaseContentImpl;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Post;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Story;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.NoUsersException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedHashMap;


public final class EvilSocialInator implements SocialMediaInator {

    private final Map<String, List<BaseContent>> users;
    private final Map<String, List<String>> activityLog;

    public EvilSocialInator() {
        users = new LinkedHashMap<>();
        activityLog = new LinkedHashMap<>();
    }


    @Override
    public void register(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username must not be null");
        }

        if (userExists(username)) {
            throw new UsernameAlreadyExistsException(username + " is already registered");
        }

        users.put(username, new ArrayList<>());
        activityLog.put(username, new LinkedList<>());
    }

    @Override
    public String publishPost(String username, LocalDateTime publishedOn, String description) {
        if (username == null || publishedOn == null || description == null) {
            throw new IllegalArgumentException("publishPost takes non-null arguments");
        }

        if (!userExists(username)) {
            throw new UsernameNotFoundException(username + " is not registered in the platform");
        }

        BaseContent post = new Post(username, publishedOn, description);
        users.get(username).add(post);

        String logString = publishedOn.format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy"))
                + ": Created a post with id " + post.getId();
        addActivityLogToUser(username, logString);

        return post.getId();
    }

    @Override
    public String publishStory(String username, LocalDateTime publishedOn, String description) {
        if (username == null || publishedOn == null || description == null) {
            throw new IllegalArgumentException("publishStory takes non-null arguments");
        }

        if (!userExists(username)) {
            throw new UsernameNotFoundException(username + " is not registered in the platform");
        }

        BaseContent story = new Story(username, publishedOn, description);
        users.get(username).add(story);

        String logString = publishedOn.format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy"))
                + ": Created a story with id " + story.getId();
        addActivityLogToUser(username, logString);

        return story.getId();
    }

    @Override
    public void like(String username, String id) {
        if (username == null || id == null) {
            throw new IllegalArgumentException("like takes non-null arguments");
        }

        if (!userExists(username)) {
            throw new UsernameNotFoundException(username + " is not registered in the platform");
        }

        BaseContent contentToLike = getUserContent(id);

        if (contentToLike == null) {
            throw new ContentNotFoundException("There is no content with id: " + id);
        }

        contentToLike.addLike(username);

        String logString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy"))
                + ": Liked a content with id " + id;
        addActivityLogToUser(username, logString);
    }

    @Override
    public void comment(String username, String text, String id) {
        if (username == null || text == null || id == null) {
            throw new IllegalArgumentException("comment takes non-null arguments");
        }

        if (!userExists(username)) {
            throw new UsernameNotFoundException(username + " is not registered in the platform");
        }

        BaseContent contentToComment = getUserContent(id);

        if (contentToComment == null) {
            throw new ContentNotFoundException("There is no content with id: " + id);
        }

        contentToComment.addComment(username, text);

        String logString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy"))
                + ": Commented \"" + text + "\" on a content with id " + id;
        addActivityLogToUser(username, logString);
    }

    @Override
    public Collection<Content> getNMostPopularContent(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative int");
        }

        Map<Integer, ArrayList<Content>> contentsPopularity = getContentsPopularity();


        return Collections.unmodifiableCollection(getNMostPopularContentsFromMap(contentsPopularity, n));
    }

    @Override
    public Collection<Content> getNMostRecentContent(String username, int n) {
        if (username == null || n < 0) {
            throw new IllegalArgumentException("username must not be null and n must be non-negative int");
        }

        if (!userExists(username)) {
            throw new UsernameNotFoundException(username + "is not registered in the system");
        }

        Map<LocalDateTime, ArrayList<Content>> mostRecentContents = getUserMostRecentContents(username);

        return Collections.unmodifiableCollection(getNMostRecentContentsFromMap(mostRecentContents, n));
    }

    @Override
    public String getMostPopularUser() {
        if (users.isEmpty()) {
            throw new NoUsersException("There are no users in the platform.");
        }

        Map<String, Integer> usersMentionsCount = getUsersMentionsCountMap();
        if (usersMentionsCount.isEmpty()) {
            return (String) users.keySet().toArray()[0];
        }
        return getMostMentionedUserName(usersMentionsCount);
    }

    @Override
    public Collection<Content> findContentByTag(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("tag must not be null");
        }

        return getContentsContainingTag(tag);
    }

    @Override
    public List<String> getActivityLog(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username must not be null");
        }

        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException(username + " is not registered in the platform");
        }

        return activityLog.get(username);
    }


    private void addActivityLogToUser(String username, String activityLogString) {
        if (!activityLog.containsKey(username)) {
            activityLog.put(username, new LinkedList<>());
        }

        ((LinkedList<String>) activityLog.get(username)).addFirst(activityLogString);
    }

    private boolean userExists(String username) {
        return users.containsKey(username);
    }

    private BaseContent getUserContent(String contentId) {
        String contentAuthor = contentId.substring(0, contentId.indexOf("-"));
        for (BaseContent content : users.get(contentAuthor)) {
            if (content.getId().equals(contentId)) {
                return content;
            }
        }

        return null;
    }

    private Map<String, Integer> getUsersMentionsCountMap() {
        Map<String, Integer> usersMentionsCount = new LinkedHashMap<>(users.size());
        for (List<BaseContent> userContents : users.values()) {
            for (BaseContent content : userContents) {
                if (content.isExpired()) {
                    continue;
                }

                for (String mention : content.getMentions()) {
                    if (users.containsKey(mention)) {
                        if (usersMentionsCount.containsKey(mention)) {
                            usersMentionsCount.put(mention, usersMentionsCount.get(mention) + 1);
                        } else {
                            usersMentionsCount.put(mention, 1);
                        }
                    }
                }
            }
        }

        return usersMentionsCount;
    }

    private String getMostMentionedUserName(Map<String, Integer> usersMentionsCount) {
        int mostPopularUserMentionsCount = 0;
        String mostPopularUserName = "";
        for (Map.Entry<String, Integer> userMentions : usersMentionsCount.entrySet()) {
            int userMentionsCount = userMentions.getValue();
            if (userMentionsCount > mostPopularUserMentionsCount) {
                mostPopularUserMentionsCount = userMentionsCount;
                mostPopularUserName = userMentions.getKey();
            }
        }

        return mostPopularUserName;
    }

    private Collection<Content> getContentsContainingTag(String tag) {
        Collection<Content> contentsContainingTag = new ArrayList<>();

        for (List<BaseContent> usersContents : users.values()) {
            for (BaseContent content : usersContents) {
                if (!(content.isExpired()) && content.getTags().contains(tag)) {
                    contentsContainingTag.add(content);
                }
            }
        }

        return contentsContainingTag;
    }

    private Map<Integer, ArrayList<Content>> getContentsPopularity() {
        Map<Integer, ArrayList<Content>> contentsPopularity = new TreeMap<>(Collections.reverseOrder());

        for (Map.Entry<String, List<BaseContent>> userContents : users.entrySet()) {
            for (BaseContent content : userContents.getValue()) {
                if (!content.isExpired()) {
                    int popularity = content.getNumberOfComments() + content.getNumberOfLikes();
                    if (contentsPopularity.get(popularity) == null) {
                        contentsPopularity.put(popularity, new ArrayList<>());
                    }
                    contentsPopularity.get(popularity).add(content);
                }
            }
        }

        return contentsPopularity;
    }

    private Collection<Content> getNMostPopularContentsFromMap(Map<Integer, ArrayList<Content>> map, int n) {
        Collection<Content> mostPopularContents = new ArrayList<>();
        int numberOfContents = 0;
        for (Map.Entry<Integer, ArrayList<Content>> contentPopularity : map.entrySet()) {
            for (Content content : contentPopularity.getValue()) {
                if (numberOfContents >= n) {
                    return Collections.unmodifiableCollection(mostPopularContents);
                }

                mostPopularContents.add(content);
                numberOfContents++;
            }
        }

        return mostPopularContents;
    }

    private Collection<Content> getNMostRecentContentsFromMap(Map<LocalDateTime, ArrayList<Content>> map, int n) {
        Collection<Content> mostRecentContents = new ArrayList<>();
        int numberOfContents = 0;
        for (Map.Entry<LocalDateTime, ArrayList<Content>> contentsToThatTime : map.entrySet()) {
            for (Content content : contentsToThatTime.getValue()) {
                if (numberOfContents >= n) {
                    return Collections.unmodifiableCollection(mostRecentContents);
                }

                mostRecentContents.add(content);
                numberOfContents++;
            }
        }

        return mostRecentContents;
    }

    private Map<LocalDateTime, ArrayList<Content>> getUserMostRecentContents(String username) {
        Map<LocalDateTime, ArrayList<Content>> mostRecentContents = new TreeMap<>();
        for (Content content : users.get(username)) {
            if (!((BaseContentImpl) content).isExpired()) {
                LocalDateTime contentPublishedOn = ((BaseContentImpl) content).getPublishedOn();
                if (mostRecentContents.get(contentPublishedOn) == null) {
                    mostRecentContents.put(contentPublishedOn, new ArrayList<>());
                }
                mostRecentContents.get(contentPublishedOn).add(content);
            }
        }

        return mostRecentContents;
    }
}
