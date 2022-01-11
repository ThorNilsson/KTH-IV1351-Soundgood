package se.kth.iv1351.soundgoodcli.view;

import se.kth.iv1351.soundgoodcli.controller.Controller;
import se.kth.iv1351.soundgoodcli.model.RentalInstrument;

import java.util.List;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

/**
 * Reads and interprets user commands. This command interpreter is blocking, the user
 * interface does not react to user input while a command is being executed.
 */
public class BlockingInterpreter {
    private static final String PROMPT = "> ";
    private static final String RENTAL_PREFIX = "RENTAL";
    private final Scanner console = new Scanner(System.in);
    private Controller ctrl;
    private boolean keepReceivingCmds = false;

    /**
     * Creates a new instance that will use the specified controller for all operations.
     *
     * @param ctrl The controller used by this instance.
     */
    public BlockingInterpreter(Controller ctrl) {
        this.ctrl = ctrl;
    }

    /**
     * Stops the commend interpreter.
     */
    public void stop() {
        keepReceivingCmds = false;
    }

    /**
     * Interprets and performs user commands. This method will not return until the
     * UI has been stopped. The UI is stopped either when the user gives the
     * "quit" command, or when the method <code>stop()</code> is called.
     */
    public void handleCmds() {
        keepReceivingCmds = true;
        while (keepReceivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine(""));
                switch (cmdLine.getCmd()) {
                    case HELP:
                        System.out.println("LIST:     Lists available instruments for specified time period, optionally filter by instrument");
                        System.out.println("  list 2022-01-16 2022-05-16 (guitar)");
                        System.out.println("  list start-date end-date   instrument\n");
                        System.out.println("RENT:     Starts the procedure of registering a rental for student with specified id");
                        System.out.println("  rent 18 16 2022-01-16 2022-05-16");
                        System.out.println("  rent student-id instrument_id start-date end-date \n");
                        System.out.println("TERMINATE:     Starts terminates a specific rental");
                        System.out.println("  terminate 18 16 2022-01-16");
                        System.out.println("  rent student-id instrument-id start-date\n");
                        for (Command command : Command.values()) {
                            if (command == Command.ILLEGAL_COMMAND) {
                                continue;
                            }
                            System.out.println(command.toString().toLowerCase());
                        }
                        break;
                    case QUIT:
                        keepReceivingCmds = false;
                        break;
                    case LIST:
                        //LIST StartDate endDate instrument
                        //list 2022-01-16 2022-05-16
                        List<RentalInstrument> rentalInstrumentList = null;
                        if (cmdLine.getParameter(2) != null) {
                            rentalInstrumentList = ctrl.getAvailableRentalInstruments(cmdLine.getParameter(0), cmdLine.getParameter(1), cmdLine.getParameter(2));
                        } else {
                            rentalInstrumentList = ctrl.getAllAvailableRentalInstruments(cmdLine.getParameter(0), cmdLine.getParameter(1));
                        }
                        displayInstrumentList(rentalInstrumentList);
                        break;
                    case RENT:
                        //RENT student instrument start end
                        //rent 18 16 2022-01-16 2022-05-16
                        ctrl.createRental(cmdLine.getParameter(0), cmdLine.getParameter(1), cmdLine.getParameter(2), cmdLine.getParameter(3), cmdLine.getParameter(4), cmdLine.getParameter(5));
                        System.out.println("The rental was made");
                        break;
                    case TERMINATE:
                        //TERMINATE student instrument start
                        //terminate 18 16 2022-01-16
                        ctrl.terminateRental(cmdLine.getParameter(0), cmdLine.getParameter(1), cmdLine.getParameter(2));
                        System.out.println("The rental was terminated");
                        break;
                    default:
                        System.out.println("illegal command");
                }
            } catch (Exception e) {
                System.out.println("Operation failed");
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void displayInstrumentList(List<RentalInstrument> avaliableRentalInstrumentList) {
        System.out.printf("%-5s | %-15s | %-15s | %-30s | %15s | %15s | %15s |\n", "ID", "Instrument", "Category", "Model", "Monthly Price", "Months", "Price");
        for (RentalInstrument instrument : avaliableRentalInstrumentList) {
            System.out.printf("%-5.5s | %-15.15s | %-15.15s | %-30.30s | %15.2f | %15d | %15.2f |\n",
                    instrument.getId(), instrument.getName(), instrument.getCategory(),
                    instrument.getModel(), instrument.getMonthlyPrice(), instrument.getMonths(),
                    instrument.getPrice());
        }
    }

    private String readNextLine(String prefix) {
        System.out.print(prefix + PROMPT);
        return console.nextLine();
    }
}
