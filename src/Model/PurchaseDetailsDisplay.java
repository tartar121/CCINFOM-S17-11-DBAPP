package Model;

// Helper class to display purchase details *with* the medicine name
public class PurchaseDetailsDisplay {
    private int medicineId;
    private String medicineName;
    private int quantity;
    private double discount;
    private double total;

    public PurchaseDetailsDisplay(int medId, String medName, int qty, double discount, double total) {
        this.medicineId = medId;
        this.medicineName = medName;
        this.quantity = qty;
        this.discount = discount;
        this.total = total;
    }

    // Getters
    public int getMedicineId() { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public double getDiscount() { return discount; }
    public double getTotal() { return total; }
}