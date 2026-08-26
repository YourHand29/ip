package yourhand.exceptions;

/**
 * Represents invalid task data found while loading YourHand's saved data file.
 */
public class CorruptFileException extends Exception {
    /**
     * Creates an exception describing why the saved data cannot be loaded.
     *
     * @param message Explanation of the invalid saved data.
     * @param cause The original parsing error.
     */
    public CorruptFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
