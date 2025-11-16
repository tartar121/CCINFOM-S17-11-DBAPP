package Model;

public class MedicineReturnReport {
    private int medicine_id;
    private String medicine_name;
    private int no_of_returns;
    private int total_quantity;
    private double total_returns;
    public MedicineReturnReport(int medID, String medName, int noReturns, int totQuan, double totRet)
    {
        medicine_id=medID;
        medicine_name=medName;
        no_of_returns=noReturns;
        total_quantity=totQuan;
        total_returns=totRet;
    }
    public int getMedID()
    {
        return medicine_id;
    }
    public String getName()
    {
        return medicine_name;
    }
    public int getNoReturns()
    {
        return no_of_returns;
    }
    public int getTotalQ()
    {
        return total_quantity;
    }
    public double getTotalR()
    {
        return total_returns;
    }
}
