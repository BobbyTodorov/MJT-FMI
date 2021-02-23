package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.util.concurrent.atomic.AtomicBoolean;

public final record Account(String username, String password) {

    public Account {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Arguments must not be null.");
        }

        if (!isCorrectUsername(username)) {
            throw new IllegalArgumentException("Username must contain alphanumeric characters and "
                + "'.', '_', '-' only.");
        }

    }

    private boolean isCorrectUsername(String username) {
        AtomicBoolean isCorrect = new AtomicBoolean(true);
        username.chars()
            .forEach(c -> {
                if (!((c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9')
                    || c == '.'
                    || c == '_'
                    || c == '-')) {
                    isCorrect.set(false);
                }
            });
        return isCorrect.get();
    }
}
