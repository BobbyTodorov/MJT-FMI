package bg.sofia.uni.fmi.mjt.netflix.platform;

import bg.sofia.uni.fmi.mjt.netflix.account.Account;
import bg.sofia.uni.fmi.mjt.netflix.content.Stream;
import bg.sofia.uni.fmi.mjt.netflix.content.Streamable;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentUnavailableException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.UserNotFoundException;


public class Netflix implements StreamingService{

    private final static String NOT_FOUND_EXCEPTION_MESSAGE = "%s is not on the system!";
    private final static String INCOMPATIBLE_PGR_EXCEPTION_MESSAGE = "%s is %d years old and %s's PGR is %s";

    private final Account[] accounts;
    private final Streamable[] streamableContent;

    private int totalTimeStreaming;

    public Netflix(Account[] accounts, Streamable[] streamableContent){
        this.accounts = accounts;
        this.streamableContent = streamableContent;
        totalTimeStreaming = 0;
    }

    private Account getUser(Account user){
        for (Account usr : accounts) {
            if(usr.equals(user)) return usr;
        }
        return null;
    }

    @Override
    public void watch(Account user, String videoContentName)
        throws ContentUnavailableException, ContentNotFoundException, UserNotFoundException {
        Account activeUser = getUser(user);
        Streamable activeVideoContent = findByName(videoContentName);
        if(activeUser == null) {
            throw new UserNotFoundException(String.format(NOT_FOUND_EXCEPTION_MESSAGE, user.username()));
        }
        if(activeVideoContent == null) {
            throw new ContentNotFoundException(String.format(NOT_FOUND_EXCEPTION_MESSAGE, videoContentName));
        }

        int activeUserAge = user.getAge();
        PgRating activeVideoContentPGR = activeVideoContent.getRating();

        if(!(activeUserAge >= 18
            || activeVideoContentPGR == PgRating.G
            || (activeUserAge >= 14 && activeVideoContentPGR == PgRating.PG13))) {

            throw new ContentUnavailableException(String.format(INCOMPATIBLE_PGR_EXCEPTION_MESSAGE,
                activeUser.username(), activeUserAge, activeVideoContent.getTitle(), activeVideoContentPGR));
        }

        ((Stream)activeVideoContent).watch();
        totalTimeStreaming += activeVideoContent.getDuration();
    }

    @Override
    public Streamable findByName(String videoContentName) {
        for (Streamable str : streamableContent) {
            if(str.getTitle().equals(videoContentName)) {
                return str;
            }
        }
        return null;
    }

    @Override
    public Streamable mostViewed() {
        int maxTimesWatched = 0, currTimesWatched;
        Streamable mostWatchedStr = null;
        for (Streamable str : streamableContent) {
            currTimesWatched = ((Stream)str).getTimesWatched();

            if(currTimesWatched > maxTimesWatched) {
                maxTimesWatched = currTimesWatched;
                mostWatchedStr = str;
            }
        }
        return maxTimesWatched == 0 ? null : mostWatchedStr;
    }

    @Override
    public int totalWatchedTimeByUsers() {
        return totalTimeStreaming;
    }
}
