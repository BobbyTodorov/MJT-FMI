package bg.sofia.uni.fmi.mjt.wish.list.exceptions;

public class RunningInterruptedException extends RuntimeException {
    Exception parentException;

    public RunningInterruptedException(String msg, Exception parentException) {
        super(msg);
        this.parentException = parentException;
    }

    public Exception getParentException() {
        return parentException;
    }
}
