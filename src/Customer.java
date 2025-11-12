public class Customer {
    private int customer_id;
    private String customer_name;
    private String customer_contact_info;
    private int senior_pwd_id;
    private String customer_status;

    public Customer(int id,String name, String contactInfo, int pwdId, String status)
    {
        customer_id=id;
        customer_name=name;
        customer_contact_info=contactInfo;
        senior_pwd_id=pwdId;
        customer_status=status;
    }
    public int getId()
    {
        return customer_id;
    }
    public String getcustomer_name()
    {
        return customer_name;
    }
    public String getContactInfo()
    {
        return customer_contact_info;
    }
    public int getPwdId()
    {
        return senior_pwd_id;
    }
    public String getStatus()
    {
        return customer_status;
    }
}
