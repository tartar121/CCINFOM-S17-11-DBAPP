package Model;

import java.time.LocalDate;

// Helper class to store results of query
public class ReturnableItem {
    private int medicineId;
    private String medicineName;
    private int quantity;
    private LocalDate expirationDate;
    private int deliveryNo;
    private LocalDate shippedDate;
    private double priceBought;

    public ReturnableItem(int medicineId, String medName, int qty,
                          LocalDate expDate, int delNo,
                          LocalDate shipDate, double priceBought) {
        this.medicineId = medicineId;
        this.medicineName = medName;
        this.quantity = qty;
        this.expirationDate = expDate;
        this.deliveryNo = delNo;
        this.shippedDate = shipDate;
        this.priceBought = priceBought;
    }

    // Getters
    public int getMedicineId() { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public int getDeliveryNo() { return deliveryNo; }
    public LocalDate getShippedDate() { return shippedDate; }
    public double getPriceBought() { return priceBought; }
    public double getTotal() { return priceBought*quantity;}

}
