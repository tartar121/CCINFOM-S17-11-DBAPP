package Model;

public class Supplier {
    private int supplier_id;
    private String supplier_name;
    private String supplier_address;
    private String supplier_contact_info;
    private String supplier_status;

    public Supplier(int id, String name, String address, String contactInfo, String status)
    {
        supplier_id=id;
        supplier_name=name;
        supplier_address=address;
        supplier_contact_info=contactInfo;
        supplier_status=status;
    }

    // Getters
    public int getId() { return supplier_id; }
    public String getName() { return supplier_name; }
    public String getAddress() { return supplier_address; }
    public String getContactInfo() { return supplier_contact_info; }
    public String getStatus() { return supplier_status; }
}
