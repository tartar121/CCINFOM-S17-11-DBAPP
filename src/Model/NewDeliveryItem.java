package Model;

import java.time.LocalDate;

// This is the "Shopping List" 📝 helper class
// It holds the data from the JTable before it's processed
public class NewDeliveryItem {
    private int medicineId;
    private String name;
    private double priceBought;
    private double priceForSale;
    private int quantity;
    private LocalDate expDate;

    public NewDeliveryItem(int id, String name, double pBought, 
                           double pSale, int qty, LocalDate date) {
        this.medicineId = id;
        this.name = name;
        this.priceBought = pBought;
        this.priceForSale = pSale;
        this.quantity = qty;
        this.expDate = date;
    }

    // Getters
    public int getMedicineId() { return medicineId; }
    public String getName() { return name; }
    public double getPriceBought() { return priceBought; }
    public double getPriceForSale() { return priceForSale; }
    public int getQuantity() { return quantity; }
    public LocalDate getExpDate() { return expDate; }
}