package bg.sofia.uni.fmi.mjt.wish.list.command;

import java.util.Arrays;

public class CommandCreator {

    private CommandCreator() {}

    public static Command newCommand(String clientInput) {
        String[] tokens = CommandCreator.getCommandArguments(clientInput);
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        return new Command(tokens[0], args);
    }

    private static String[] getCommandArguments(String input) {
        final String LINE_DELIMITER = " ";

        String inputWithoutWhitespace = input.replaceAll("\\s+", LINE_DELIMITER).trim();
        return inputWithoutWhitespace.split(LINE_DELIMITER);
    }
}
