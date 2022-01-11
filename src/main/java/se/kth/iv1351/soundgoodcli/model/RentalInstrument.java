package se.kth.iv1351.soundgoodcli.model;

public class RentalInstrument {
    private final String id;
    private final String name;
    private final String model;
    private final String cathegory;
    private final double monthlyPrice;
    private final int months;
    private final double price;

    public RentalInstrument(String id, String name, String model, String cathegory, double monthlyPrice, int months, double price) {
        this.id = id;
        this.name = name;
        this.model = model;
        this.cathegory = cathegory;
        this.monthlyPrice = monthlyPrice;
        this.months = months;
        this.price = price;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getModel() {
        return this.model;
    }

    public String getCategory() {
        return this.cathegory;
    }

    public double getMonthlyPrice() {
        return this.monthlyPrice;
    }

    public int getMonths() {
        return this.months;
    }

    public double getPrice() {
        return this.price;
    }

}
