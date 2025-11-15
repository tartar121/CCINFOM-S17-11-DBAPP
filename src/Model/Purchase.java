package Model;

import java.time.LocalDate;
public class Purchase {
    private int purchase_no;
    private LocalDate purchase_date;
    private int customer_id;

    public Purchase(int no, LocalDate purchase_date, int cId)
    {
        purchase_no=no;
        this.purchase_date=purchase_date;
        customer_id=cId;
    }
    public int getPurchaseNo() { return purchase_no; }
    public int getCustomerId() { return customer_id; }
    public LocalDate getPurchaseDate() { return purchase_date; }
}
