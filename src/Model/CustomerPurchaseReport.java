package Model;

public class CustomerPurchaseReport {
    private int customer_id;
    private String customer_name;
    private int no_of_selling;
    private double total_amount;
    public CustomerPurchaseReport(int cusId, String cusName, int noOfSell, double total) {
        customer_id=cusId;
        customer_name=cusName;
        no_of_selling=noOfSell;
        total_amount=total;
    }
    public int getCustomerId()
    {
        return customer_id;
    }
    public String getCustomerName()
    {
        return customer_name;
    }
    public int getSelling()
    {
        return no_of_selling;
    }
    public double getTotal()
    {
        return total_amount;
    }
}
