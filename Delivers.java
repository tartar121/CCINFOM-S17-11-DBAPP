import java.time.LocalDate;
public class Delivers 
{
    private int delivery_no;
    private int supplier_id;
    private LocalDate request_date;
    private LocalDate shipped_date;
    private String delivery_status;

    public Delivers (int no, int id, LocalDate rdate, LocalDate sdate, String ds)
    {
        delivery_no=no;
        supplier_id=id;
        request_date=rdate;
        shipped_date=sdate;
        delivery_status=ds;
    }
    public int getdno()
    {
        return delivery_no;
    }
    public int getsid()
    {
        return supplier_id;
    }
    public LocalDate getrdate()
    {
        return request_date;
    }
    public LocalDate getsdate()
    {
        return shipped_date;
    }
    public String getStatus()
    {
        return delivery_status;
    }
}
