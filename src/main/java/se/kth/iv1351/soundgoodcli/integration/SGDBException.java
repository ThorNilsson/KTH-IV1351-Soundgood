package se.kth.iv1351.soundgoodcli.integration;

/**
 * Thrown when a call to the sound good database fails.
 */
public class SGDBException extends Exception {

    /**
     * Create a new instance thrown because of the specified reason.
     *
     * @param reason Why the exception was thrown.
     */
    public SGDBException(String reason) {
        super(reason);
    }

    /**
     * Create a new instance thrown because of the specified reason and exception.
     *
     * @param reason    Why the exception was thrown.
     * @param rootCause The exception that caused this exception to be thrown.
     */
    public SGDBException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
