package Model;

import java.time.LocalDate;

// This is the "Shopping List" 📝 helper class
// It holds the data from the JTable before it's processed
public class NewDeliveryItem {
    private int medicineId; // This is the Batch ID
    private String medicineName;
    private int quantityOrdered;
    private double priceBought;
    private double priceForSale;
    private LocalDate expDate;
    private double lineTotal;

    public NewDeliveryItem(Medicine medicine, int quantity) {
        this.medicineId = medicine.getId();
        this.medicineName = medicine.getName();
        this.priceBought = medicine.getPriceBought();
        this.priceForSale = medicine.getPriceForSale();
        this.expDate = medicine.getExpirationDate();
        this.quantityOrdered = quantity;
        lineTotal=quantityOrdered*priceBought;
    }

    // Getters
    public int getMedicineId() { return medicineId; }
    public String getName() { return medicineName; }
    public double getPriceBought() { return priceBought; }
    public double getPriceForSale() { return priceForSale; }
    public int getQuantity() { return quantityOrdered; }
    public LocalDate getExpDate() { return expDate; }
    public double getTotal() { return lineTotal; }
}