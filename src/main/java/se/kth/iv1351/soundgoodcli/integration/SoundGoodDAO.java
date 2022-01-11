/*
 * The MIT License (MIT)
 * Copyright (c) 2020 Leif Lindbäck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction,including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so,subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package se.kth.iv1351.soundgoodcli.integration;

import se.kth.iv1351.soundgoodcli.model.RentalInstrument;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * This data access object (DAO) encapsulates all database calls in the sound good
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class SoundGoodDAO {
    private Connection connection;
    private PreparedStatement findAvaliableRentalInstruments;
    private PreparedStatement findAllAvaliableRentalInstruments;
    private PreparedStatement findAllAvaliableRentalInstrumentsLockingForUpdate;
    private PreparedStatement findStudentNrRentalInstrmentsLockingForUpdate;
    private PreparedStatement findStudentNrRentalInstrments;
    private PreparedStatement createRental;
    private PreparedStatement terminateRental;

    /**
     * Constructs a new DAO object connected to the sound good database.
     */
    public SoundGoodDAO() throws SGDBException {
        try {
            connectToSGDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new SGDBException("Could not connect to datasource.", exception);
        }
    }

    /**
     * Retrieves the number of active rental instruments of the student specified by id.
     * @param studentId The id of student.
     * @return The nr of active rentals.
     * @throws SGDBException If failed to retrieve active rentals.
     */
    public int getStudentInstrumentCount(String studentId) throws SGDBException {
        String failureMsg = "Could not get nr of active instrument rentals for student with id: " + studentId;
        ResultSet result = null;
        try {
            findStudentNrRentalInstrmentsLockingForUpdate.setInt(1, parseInt(studentId));
            result = findStudentNrRentalInstrmentsLockingForUpdate.executeQuery();

            if (result.next()) {
                return result.getInt("sum");
            } else {
                handleException(failureMsg, new Exception("No student with id " + studentId + " found."));
            }
            //connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
        return 0;
    }

    /**
     * Creates an instrument rental for the specified student of the specified instrument.
     * @param studentId The id of student.
     * @param rentalInstrumentId The id of rental instrument to rent.
     * @param startDate The start date of the rental.
     * @param endDate The end date of the rental.
     * @param delivery The delivery date of the instrument.
     * @param price The price of the rental.
     * @param notes The notes about the instrument or delivery.
     * @throws SGDBException If failed to create the rental.
     */
    public void createRental(String studentId, String rentalInstrumentId, String startDate, String endDate, String delivery, double price, String notes) throws SGDBException {
        String failureMsg = "Could not create the rental for student id " + studentId + " and instrument id " + rentalInstrumentId + ".";
        int updatedRows = 0;

        try {
            createRental.setInt(1, parseInt(studentId));
            createRental.setInt(2, parseInt(rentalInstrumentId));
            createRental.setString(3, startDate);
            createRental.setString(4, endDate);
            createRental.setString(5, delivery);
            createRental.setDouble(6, price);
            createRental.setString(7, notes);

            updatedRows = createRental.executeUpdate();

            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    /**
     * Finds all the available rental instruments for the specified time period and instrument type.
     * @param startDate The start date of the available check.
     * @param endDate The end date of the available check.
     * @param instrument The instrument type to search for.
     * @return List of instruments available for rent.
     * @throws SGDBException If failed to get rental instruments.
     */
    public List<RentalInstrument> findAvailableRentalInstruments(String startDate, String endDate, String instrument) throws SGDBException {
        String failureMsg = "Could not list available rental instruments.";
        ResultSet result = null;
        List<RentalInstrument> accounts = new ArrayList<>();
        try {
            findAvaliableRentalInstruments.setString(1, startDate);
            findAvaliableRentalInstruments.setString(2, endDate);
            findAvaliableRentalInstruments.setString(3, startDate);
            findAvaliableRentalInstruments.setString(4, endDate);
            findAvaliableRentalInstruments.setString(5, startDate);
            findAvaliableRentalInstruments.setString(6, endDate);
            findAvaliableRentalInstruments.setString(7, instrument);

            result = findAvaliableRentalInstruments.executeQuery();

            while (result.next()) {
                accounts.add(new RentalInstrument(
                                result.getString("rental_instrument_id"),
                                result.getString("name"),
                                result.getString("model"),
                                result.getString("cathegory"),
                                result.getDouble("monthly_price"),
                                result.getInt("nr_months"),
                                result.getDouble("total_price")
                        )
                );
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }finally {
            closeResultSet(failureMsg, result);
        }
        return accounts;
    }

    /**
     * Finds all the available rental instruments for the specified time period.
     * @param startDate The start date of the available check.
     * @param endDate The end date of the available check.
     * @return List of instruments available for rent.
     * @throws SGDBException If failed to get rental instruments.
     */
    public List<RentalInstrument> findAllAvailableRentalInstruments(String startDate, String endDate, boolean lockExclusive) throws SGDBException {
        PreparedStatement stmtToExecute;
        if (lockExclusive) {
            stmtToExecute = findAllAvaliableRentalInstrumentsLockingForUpdate;
        } else {
            stmtToExecute = findAllAvaliableRentalInstruments;
        }

        String failureMsg = "Could not list available rental instruments.";
        ResultSet result = null;
        List<RentalInstrument> accounts = new ArrayList<>();
        try {
            stmtToExecute.setString(1, startDate);
            stmtToExecute.setString(2, endDate);
            stmtToExecute.setString(3, startDate);
            stmtToExecute.setString(4, endDate);
            stmtToExecute.setString(5, startDate);
            stmtToExecute.setString(6, endDate);

            result = stmtToExecute.executeQuery();

            while (result.next()) {
                accounts.add(new RentalInstrument(
                                result.getString("rental_instrument_id"),
                                result.getString("name"),
                                result.getString("model"),
                                result.getString("cathegory"),
                                result.getDouble("monthly_price"),
                                result.getInt("nr_months"),
                                result.getDouble("total_price")
                        )
                );
            }
            if (!lockExclusive) {
                connection.commit();
            }
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }finally {
            closeResultSet(failureMsg, result);
        }
        return accounts;
    }

    /**
     * Terminates the rental specified.
     * @param studentId The id of student.
     * @param rentalInstrumentId The id of rental instrument.
     * @param startDate The start date of the rental.
     * @throws SGDBException If failed to terminate the rental.
     */
    public void terminateRental(String studentId, String rentalInstrumentId, String startDate) throws SGDBException {
        String failureMsg = "Could not terminate the rental.";
        int updatedRows = 0;
        try {
            terminateRental.setInt(1, parseInt(rentalInstrumentId));
            terminateRental.setInt(2, parseInt(studentId));
            terminateRental.setString(3, startDate);

            updatedRows = terminateRental.executeUpdate();

            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    private void connectToSGDB() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/soundgood",
                "postgres", "Nttl32121");
        //connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bankdb",
        //                                         "postgres", "postgres");
        // connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb",
        //                                          "mysql", "mysql");
        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {

        findAvaliableRentalInstruments = connection.prepareStatement(
                "SELECT rental_instrument_id, name, model, cathegory, monthly_price, EXTRACT(month FROM age((?)::date, (?)::date)) * -1 as nr_months, EXTRACT(month FROM age((?)::date, (?)::date)) * -1 * monthly_price as total_price, image, description from ( SELECT id as rental_instrument_id, count(*) as occurrences, SUM(case when s.status_bool or ter then 0 else 1 end) as booked FROM ( SELECT *, r.terminated as ter, case when ((?)::date <= r.end_date) and ((?)::date >= r.start_date) then false else true end as status_bool FROM rental_instrument ri FULL JOIN rental r on ri.id = r.rental_instrument_id) s group by id order by id) avaliable JOIN rental_instrument ri on avaliable.rental_instrument_id = ri.id JOIN instrument i on i.id = ri.instrument_id where booked = 0 and i.name = ? order by rental_instrument_id");

        findAllAvaliableRentalInstruments = connection.prepareStatement(
                "SELECT rental_instrument_id, name, model, cathegory, monthly_price, EXTRACT(month FROM age((?)::date, (?)::date)) * -1 as nr_months, EXTRACT(month FROM age((?)::date, (?)::date)) * -1 * monthly_price as total_price, image, description from ( SELECT id as rental_instrument_id, count(*) as occurrences, SUM(case when s.status_bool or ter then 0 else 1 end) as booked FROM ( SELECT *, r.terminated as ter, case when ((?)::date <= r.end_date) and ((?)::date >= r.start_date) then false else true end as status_bool FROM rental_instrument ri FULL JOIN rental r on ri.id = r.rental_instrument_id) s group by id order by id) avaliable JOIN rental_instrument ri on avaliable.rental_instrument_id = ri.id JOIN instrument i on i.id = ri.instrument_id where booked = 0 order by rental_instrument_id");

        findAllAvaliableRentalInstrumentsLockingForUpdate = connection.prepareStatement(
                "SELECT rental_instrument_id, name, model, cathegory, monthly_price, EXTRACT(month FROM age((?)::date, (?)::date)) * -1 as nr_months, EXTRACT(month FROM age((?)::date, (?)::date)) * -1 * monthly_price as total_price, image, description from (SELECT id as rental_instrument_id, count(*) as occurrences, SUM(case when s.status_bool or ter then 0 else 1 end) as booked FROM (SELECT *, r.terminated as ter, case when ((?)::date <= r.end_date) and ((?)::date >= r.start_date) then false else true end as status_bool FROM (SELECT * FROM rental_instrument FOR UPDATE) ri FULL JOIN (SELECT * FROM rental FOR UPDATE)  r on ri.id = r.rental_instrument_id) s group by id order by id) avaliable JOIN rental_instrument ri on avaliable.rental_instrument_id = ri.id JOIN instrument i on i.id = ri.instrument_id where booked = 0 order by rental_instrument_id;");

        findStudentNrRentalInstrments = connection.prepareStatement(
                "select sum(has_instrument) from (select s.student_id, end_date, r.terminated, case when s.student_id = r.student_id and r.terminated != true then 1 else 0 end as has_instrument from rental r FULL JOIN student s on r.student_id = s.student_id where approved = true and (end_date >= CURRENT_TIMESTAMP or start_date IS NULL) ) s where student_id = ? group by student_id");

        findStudentNrRentalInstrmentsLockingForUpdate = connection.prepareStatement(
                "select sum(has_instrument) from (select s.student_id, end_date, r.terminated, s.approved, case when s.student_id = r.student_id and r.terminated != true then 1 else 0 end as has_instrument from (SELECT * FROM rental FOR UPDATE) r FULL JOIN (SELECT * FROM student FOR UPDATE) s on r.student_id = s.student_id where approved = true and (end_date >= CURRENT_TIMESTAMP or start_date IS NULL)) s where student_id = ? group by student_id;");

        createRental = connection.prepareStatement(
                "INSERT INTO rental (student_id, rental_instrument_id, start_date, end_date, delivery, price, notes, terminated) VALUES (?, ?, (?)::date, (?)::date, ?::timestamp,?,?, false)");

        terminateRental = connection.prepareStatement(
                "UPDATE rental SET terminated = true, termination_date = CURRENT_DATE where rental_instrument_id = ? and student_id = ? and start_date = ?::date");
   }

    public void commit() throws SGDBException {
        try {
            connection.commit();
        } catch (SQLException e) {
            handleException("Failed to commit", e);
        }
    }
    /**
     * Handles the exception and roll back all changes.
     * @param failureMsg The message to throw with the exception.
     * @param cause The exception to handle.
     * @throws SGDBException If failed to rollback.
     */
    private void handleException(String failureMsg, Exception cause) throws SGDBException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg +
                    ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
        }

        if (cause != null) {
            throw new SGDBException(failureMsg, cause);
        } else {
            throw new SGDBException(failureMsg);
        }
    }

    private void closeResultSet(String failureMsg, ResultSet result) throws SGDBException {
        try {
            result.close();
        } catch (Exception e) {
            throw new SGDBException(failureMsg + " Could not close result set.", e);
        }
    }
}
