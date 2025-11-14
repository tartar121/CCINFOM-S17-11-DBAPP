import java.time.LocalDate;
public class Medicine {
    private int medicine_id;
    private String medicine_name;
    private double price_bought;
    private double price_for_sale;
    private int quantity_in_stock;
    private LocalDate expiration_date;
    private boolean discontinued;

    public Medicine(int id, String name, double price_bought, double price_for_sale, int quantity, LocalDate expiration_date, boolean discontinued)
    {
        medicine_id=id;
        medicine_name=name;
        this.price_bought=price_bought;
        this.price_for_sale=price_for_sale;
        quantity_in_stock=quantity;
        this.expiration_date=expiration_date;
        this.discontinued=discontinued;
    }
    public int getId() { return medicine_id; }
    public String getName() { return medicine_name; }
    public double getPriceBought() { return price_bought; }
    public double getPriceForSale() { return price_for_sale; }
    public int getQuantity() { return quantity_in_stock; }
    public LocalDate getExpirationDate() { return expiration_date; }
    public boolean isDiscontinued() { return discontinued; }
}
