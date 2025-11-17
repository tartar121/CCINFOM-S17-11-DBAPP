package Model;

public class ReturnDetailsDisplay {
    private int medicineId;
    private String medicineName;
    private int quantity;
    private double price;

    public ReturnDetailsDisplay(int medId, String medName, int qty, double price) {
        this.medicineId = medId;
        this.medicineName = medName;
        this.quantity = qty;
        this.price = price;
    }

    // Getters
    public int getMedicineId() { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}
