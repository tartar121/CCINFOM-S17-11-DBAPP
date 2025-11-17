package Model;

public class ReturnDetails {
    private int returnNo;
    private int medicineId;
    private int deliveryNo;
    private double priceReturned;
    private int quantityReturned;

    public ReturnDetails(int returnNo, int medicineId, int deliveryNo, double priceReturned, int quantityReturned) {
        this.returnNo = returnNo;
        this.medicineId = medicineId;
        this.deliveryNo = deliveryNo;
        this.priceReturned = priceReturned;
        this.quantityReturned = quantityReturned;
    }

    // Getters
    public int getReturnNo() { return returnNo; }
    public int getMedicineId() { return medicineId; }
    public int getDeliveryNo() { return deliveryNo; }
    public double getPriceReturned() { return priceReturned; }
    public int getQuantityReturned() { return quantityReturned; }
}