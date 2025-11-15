package Model;

import java.time.LocalDate;

// Helper class to store results of query
// NOT a database table
public class ReturnableItem {
    private int medicineId; // Also the Batch ID
    private String medicineName;
    private int quantity;
    private LocalDate expirationDate;
    private int deliveryNo;
    private LocalDate shippedDate;
    private double priceBought;
    private int return_no;
    private double total;

    public ReturnableItem(int medicineId, String medName, int qty,
                          LocalDate expDate, int delNo,
                          LocalDate shipDate, double priceBought, int return_no) {
        this.medicineId = medicineId;
        this.medicineName = medName;
        this.quantity = qty;
        this.expirationDate = expDate;
        this.deliveryNo = delNo;
        this.shippedDate = shipDate;
        this.priceBought = priceBought;
        this.return_no = return_no;
        total=priceBought*quantity;
    }
    // Getters
    public int getMedicineId() { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public int getDeliveryNo() { return deliveryNo; }
    public LocalDate getShippedDate() { return shippedDate; }
    public double getPriceBought() { return priceBought; }
    public int getreturn_no()
    {
        return return_no;
    }
    public double gettotal()
    {
        return total;
    }
}
