package Model;

import java.time.LocalDate;

public class Return {
    private int return_no;
    private int supplier_id;
    private String reason;
    private LocalDate request_date;
    private LocalDate shipped_date;
    private String return_status;

    public Return(int no, int sId, String reason, LocalDate reqDate, LocalDate shipDate, String status)
    {
        return_no=no;
        supplier_id=sId;
        this.reason=reason;
        request_date=reqDate;
        shipped_date=shipDate;
        return_status=status;
    }

    // Getters
    public int getReturnNo() { return return_no; }
    public int getSupplierId() { return supplier_id; }
    public String getReason() { return reason; }
    public LocalDate getRequestDate() { return request_date; }
    public LocalDate getShippedDate() { return shipped_date; }
    public String getReturnStatus() { return return_status; }
}
