public class DeliveryDetails {
    private int delivery_no;
    private int medicine_id;
    private int quantity;
    private double total;
    public DeliveryDetails (int dno, int medid, int quan, double t)
    {
        delivery_no=dno;
        medicine_id=medid;
        quantity=quan;
        total=t;
    }
    public int getDno()
    {
        return delivery_no;
    }
    public int getMedid()
    {
        return medicine_id;
    }
    public int getQuan()
    {
        return quantity;
    }
    public double getTotal()
    {
        return total;
    }
}
