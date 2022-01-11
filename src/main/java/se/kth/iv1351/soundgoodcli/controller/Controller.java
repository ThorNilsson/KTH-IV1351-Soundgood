package se.kth.iv1351.soundgoodcli.controller;

import se.kth.iv1351.soundgoodcli.integration.SGDBException;
import se.kth.iv1351.soundgoodcli.integration.SoundGoodDAO;
import se.kth.iv1351.soundgoodcli.model.RentalException;
import se.kth.iv1351.soundgoodcli.model.RentalInstrument;

import java.util.List;

import static java.lang.Integer.parseInt;
import static se.kth.iv1351.soundgoodcli.model.Model.canStudentRentMoreInstruments;
import static se.kth.iv1351.soundgoodcli.model.Model.getPriceById;

/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {
    private final SoundGoodDAO soundgoodDb;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     *
     * @throws SGDBException If unable to connect to the database.
     */
    public Controller() throws SGDBException {
        soundgoodDb = new SoundGoodDAO();
    }

    /**
     * Retrieves all the available rental instruments for the specified time period and instrument type.
     *
     * @param startDate  The start date of the available check.
     * @param endDate    The end date of the available check.
     * @param instrument The instrument type to search for.
     * @return List of instruments available for rent.
     * @throws RentalException If failed to get rental instruments.
     */
    public List<RentalInstrument> getAvailableRentalInstruments(String startDate, String endDate, String instrument) throws RentalException {
        try {
            return soundgoodDb.findAvailableRentalInstruments(startDate, endDate, instrument);
        } catch (Exception e) {
            throw new RentalException("Unable to list " + instrument + "s for the period " + startDate + " to " + endDate + ".", e);
        }
    }

    /**
     * Retrieves all the available rental instruments for the specified time period.
     *
     * @param startDate The start date of the available check.
     * @param endDate   The end date of the available check.
     * @return List of instruments available for rent.
     * @throws RentalException If failed to get rental instruments.
     */
    public List<RentalInstrument> getAllAvailableRentalInstruments(String startDate, String endDate) throws RentalException {
        String failureMsg = "User did not enter start or end date.";
        if (startDate == null || endDate == null) {
            throw new RentalException(failureMsg);
        }
        try {
            return soundgoodDb.findAllAvailableRentalInstruments(startDate, endDate, false);
        } catch (Exception e) {
            throw new RentalException("Unable to list all available instruments for the period " + startDate + " to " + endDate + ".", e);
        }
    }

    /**
     * Creates an instrument rental for the specified student of the specified instrument.
     *
     * @param studentId          The id of student.
     * @param rentalInstrumentId The id of rental instrument to rent.
     * @param startDate          The start date of the rental.
     * @param endDate            The end date of the rental.
     * @param delivery           The delivery date of the instrument.
     * @param price              The price of the rental.
     * @param notes              The notes about the instrument or delivery.
     * @throws RentalException If failed to create the rental.
     */
    public void createRental(String studentId, String rentalInstrumentId, String startDate, String endDate, String delivery, String notes) throws RentalException {
        String failureMsg = "Could not create rental for student id: " + studentId;

        if (studentId == null || rentalInstrumentId == null || startDate == null || endDate == null) {
            throw new RentalException(failureMsg);
        }

        try {
            int activeRentals = soundgoodDb.getStudentInstrumentCount(studentId);
            List<RentalInstrument>  instruments = soundgoodDb.findAllAvailableRentalInstruments(startDate, endDate, true);
            double price = getPriceById(instruments, rentalInstrumentId);

            if (canStudentRentMoreInstruments(activeRentals)) {
                soundgoodDb.createRental(studentId, rentalInstrumentId, startDate, endDate, delivery, price, notes);
            }

        } catch (SGDBException sgDBe) {
            throw new RentalException(failureMsg, sgDBe);
        } catch (Exception e) {
            commit(failureMsg);
            throw e;
        }
    }

    /**
     * Terminates the rental specified.
     *
     * @param rentalInstrumentId The id of student.
     * @param studentId          The id of rental instrument.
     * @param startDate          The start date of the rental.
     * @throws RentalException If failed to terminate the rental.
     */
    public void terminateRental( String studentId, String rentalInstrumentId, String startDate) throws RentalException {
        String failureMsg = "Could not terminate rental for instrument " + rentalInstrumentId + ", student " + studentId + " and date " + startDate;

        if (studentId == null || rentalInstrumentId == null || startDate == null) {
            throw new RentalException(failureMsg);
        }

        try {
            soundgoodDb.terminateRental(studentId, rentalInstrumentId, startDate);
        } catch (Exception e) {
            throw new RentalException(failureMsg, e);
        }
    }

    private void commit(String failureMsg) throws RentalException {
        try {
            soundgoodDb.commit();
        } catch (SGDBException sgDBe) {
            throw new RentalException(failureMsg, sgDBe);
        }
    }
}
