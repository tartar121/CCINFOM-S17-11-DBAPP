package Model;

public class ProcurementReport {
    private int supplier_id;
    private String supplier_name;
    private int no_of_restocks;
    private double total_amount;
    public ProcurementReport(int supid, String supname, int norestocks, double total) {
        supplier_id=supid;
        supplier_name=supname;
        no_of_restocks=norestocks;
        total_amount=total;
    }
    // Getters
    public int getSID()
    {
        return supplier_id;
    }
    public String getSName()
    {
        return supplier_name;
    }
    public int getRestocks()
    {
        return no_of_restocks;
    }
    public double getTotal()
    {
        return total_amount;
    }
}
