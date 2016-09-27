package au.com.fatzebra.javalib.errors;

/**
 * Represents a network/connectivity error
 */
public class NetworkError extends Exception {
    public static final long serialVersionUID = 1;
    private String message;
    private Throwable ex;
    private boolean timeout = false;

    /**
     * Initialises a new error
     * @param message the error message
     * @param timeout indicates if this error was a result of a timeout
     */
    public NetworkError(String message, boolean timeout) {
        this.message = message;
        this.timeout = timeout;
    }

    /**
     * Initialises a new error with an encapsulated exception
     * @param message the error message
     * @param timeout indicates if this error was a result of a timeout
     * @param ex the encapsulated exception
     */
    public NetworkError(String message, boolean timeout, Throwable ex) {
        super(message, ex);
        this.timeout = timeout;
    }

    /**
     * Gets the message for the exception
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gets the timeout indicator for the exception
     * @return indicate if the error was a result of a timeout
     */
    public boolean getTimeout() {
        return this.timeout;
    }

    /**
     * Gets the inner exception for the error
     * @return encapsulated exception
     */
    public Throwable getInnerException() {
        return this.ex;
    }
}
