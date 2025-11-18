package Model;

// Helper class for the JTable "shopping cart"
public class CartItem {
    private int medicineId;
    private String medicineName;
    private int quantityOrdered;
    private double priceForSale;
    private double discount; // The discount amount (e.g., 4.00)
    private double lineTotal; // The final price for this line

    public CartItem(Medicine medicine, int quantity, boolean applyDiscount) {
        this.medicineId = medicine.getId();
        this.medicineName = medicine.getName();
        this.quantityOrdered = quantity;
        this.priceForSale = medicine.getPriceForSale();

        // Business Rules for Discount
        double baseTotal = this.priceForSale * this.quantityOrdered;
        if (applyDiscount) {
            this.discount = baseTotal * 0.20; // 20% discount
        } else {
            this.discount = 0.0;
        }
        this.lineTotal = baseTotal - this.discount;
    }

    // Getters
    public int getMedicineId() { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public int getQuantityOrdered() { return quantityOrdered; }
    public double getPriceForSale() { return priceForSale; }
    public double getDiscount() { return discount; }
    public double getLineTotal() { return lineTotal; }
}