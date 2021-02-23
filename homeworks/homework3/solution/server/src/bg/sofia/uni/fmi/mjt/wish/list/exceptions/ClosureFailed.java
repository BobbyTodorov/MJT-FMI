package bg.sofia.uni.fmi.mjt.wish.list.exceptions;

public class ClosureFailed extends RuntimeException {
    Exception parentException;

    public ClosureFailed(String msg, Exception parentException) {
        super(msg);
        this.parentException = parentException;
    }

    public Exception getParentException() {
        return parentException;
    }
}
