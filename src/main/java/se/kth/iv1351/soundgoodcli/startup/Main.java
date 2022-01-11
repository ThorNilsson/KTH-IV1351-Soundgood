package se.kth.iv1351.soundgoodcli.startup;

import se.kth.iv1351.soundgoodcli.controller.Controller;
import se.kth.iv1351.soundgoodcli.integration.SGDBException;
import se.kth.iv1351.soundgoodcli.view.BlockingInterpreter;

/**
 * Starts the bank client.
 */
public class Main {
    /**
     * @param args There are no command line arguments.
     */
    public static void main(String[] args) {
        try {
        new BlockingInterpreter(new Controller()).handleCmds();
        } catch(SGDBException bdbe) {
            System.out.println("Could not connect to Bank db.");
            bdbe.printStackTrace();
        }
    }
}
