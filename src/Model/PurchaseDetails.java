package Model;

public class PurchaseDetails {
    private int purchase_no;
    private int medicine_id;
    private int quantity_order;
    private Double discount;
    private double total;

    public PurchaseDetails(int no, int mId, int qtyOrder, Double discount, double total)
    {
        purchase_no=no;
        medicine_id=mId;
        quantity_order=qtyOrder;
        this.discount=discount;
        this.total=total;
    }
    public int getPurchaseNo() { return purchase_no; }
    public int getMedicineId() { return medicine_id; }
    public int getQuantityOrder() { return quantity_order; }
    public double getDiscount()  { return discount;  }
    public double getTotal() {   return total;   };
}
