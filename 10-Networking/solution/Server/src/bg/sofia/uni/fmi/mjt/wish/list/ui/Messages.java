package bg.sofia.uni.fmi.mjt.wish.list.ui;

public final class Messages extends ServerUserInterface {
    private Messages() {}

    public static final String MESSAGE_UNKNOWN_COMMAND = "[ Unknown command ]" + NL;
    public static final String MESSAGE_CLIENT_DISCONNECTED = "[ Disconnected from server ]" + NL;
    public static final String MESSAGE_NO_MORE_PEOPLE = "[ There are no students present in the wish list ]" + NL;
    public static final String MESSAGE_PLEASE_LOGOUT = "[ Please log-out before attempting to log in ]" + NL;
    public static final String MESSAGE_NOT_LOGGED_IN = "[ You are not logged in ]" + NL;
    public static final String MESSAGE_LOG_OUT = "[ Successfully logged out ]" + NL;
    public static final String MESSAGE_INVALID_USER = "[ Invalid username/password combination ]" + NL;
    public static final String MESSAGE_INVALID_USERNAME = "[ Username %s is invalid, select a valid one ]" + NL;
    public static final String MESSAGE_USER_LOGIN = "[ User %s successfully logged in ]" + NL;
    public static final String MESSAGE_USERNAME_ALREADY_TAKEN =
        "[ Username %s is already taken, select another one ]" + NL;
    public static final String MESSAGE_USER_SUCCESSFULLY_REGISTERED = "[ Username %s successfully registered ]" + NL;
    public static final String MESSAGE_USER_NOT_REGISTERED = "[ Student with username %s is not registered ]" + NL;
    public static final String MESSAGE_USER_LOGGED_IN_FROM_ANOTHER_CLIENT =
        "[ User %s is already logged in from another client ]" + NL;
    public static final String MESSAGE_SAME_GIFT_FOR_USER =
        "[ The same gift for student %s was already submitted ]" + NL;
    public static final String MESSAGE_GIFT_SUBMITTED_FOR_USER =
        "[ Gift %1$s for student %2$s submitted successfully ]" + NL;
    public static final String MESSAGE_USER_TO_WISH_LIST = "[ %1$s:%2$s]" + NL;
    public static final String MESSAGE_INVALID_SYNTAX = "[ %s incorrect syntax ]" + NL;
}
