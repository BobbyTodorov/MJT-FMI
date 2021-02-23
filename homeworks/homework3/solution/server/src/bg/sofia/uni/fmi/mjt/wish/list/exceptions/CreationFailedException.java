package bg.sofia.uni.fmi.mjt.wish.list.exceptions;

public class CreationFailedException extends RuntimeException {
    Exception parentException;

    public CreationFailedException(String msg, Exception parentException) {
        super(msg);
        this.parentException = parentException;
    }

    public Exception getParentException() {
        return parentException;
    }
}
