package Model;

// Helper class to display delivery details with the medicine name
public class DeliveryDetailsDisplay {
    private int medicineId;
    private String medicineName;
    private int quantity;
    private double total;

    public DeliveryDetailsDisplay(int medId, String medName, int qty, double total) {
        this.medicineId = medId;
        this.medicineName = medName;
        this.quantity = qty;
        this.total = total;
    }

    // Getters
    public int getMedicineId() { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return total; }
}