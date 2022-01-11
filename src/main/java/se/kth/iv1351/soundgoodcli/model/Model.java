package se.kth.iv1351.soundgoodcli.model;

import java.util.List;

public class Model {
    public static double getPriceById(List<RentalInstrument> instruments, String instrumentId) throws RentalException {
        String errorMessage = "No rental instrument with id " + instrumentId + " found";
        for(RentalInstrument instrument : instruments){
            if(instrument.getId().equals(instrumentId)){
                return instrument.getPrice();
            }
        }
        throw new RentalException(errorMessage, null);
    }
    public static boolean canStudentRentMoreInstruments(int activeRentals) throws RentalException {
        String errorMessage = "The student already has " + activeRentals + " active rentals.";

        if(activeRentals < 2)
            return true;
        else
            throw new RentalException(errorMessage, null);

    }
}
