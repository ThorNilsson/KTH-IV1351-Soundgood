package se.kth.iv1351.soundgoodcli.view;

/**
 * Defines all commands that can be performed by a user of the chat application.
 */
public enum Command {
    /**
     *  Lists available instruments for specified time period, optionally filter by instrument
     */
    LIST,
    /**
     * Starts the procedure of registering a rental for student with specified id.
     */
    RENT,
    RENTAL,

    /**
     *  Terminates a specific rental
     */
    TERMINATE,
    /**
     * Displays some help to get started
     */
    HELP,
    /**
     * Quits the program
     */
    QUIT,
    /**
     * None of the valid commands above was specified.
     */
    ILLEGAL_COMMAND;;
}
