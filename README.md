# Pharmacy Management System 💊

A comprehensive database application built to manage the daily operations of a pharmacy. This project fulfills the requirements for the **CCINFOM (Information Management)** course at **De La Salle University**.

The system shows inventory tracking, sales processing, and supplier interactions using a structured **MySQL** database and a professional **Java Swing** interface.

---

## 🚀 Key Features

The application is designed with distinct roles in mind: the **Pharmacist (User)** who performs daily transactions, and the **Manager (Admin)** who maintains records and generates reports.

### 1. Records Management 🗄️ (Admin View)
_Centralized management for the pharmacy's core data._
* **Medicine Management:** Create and update medicine batches (Batch ID, Price, Expiry).
* **Supplier Management:** Maintain a directory of active and inactive suppliers.
* **Customer Management:** Track customer details and Senior/PWD status for automatic discounts.

### 2. Transactions 🛒 (User View)
_Complex business processes with validation and automated stock adjustments._
* **Point of Sale (Purchase):**
    * "Cash Register" style interface for pharmacists.
    * **Instant Processing:** Automatically deducts stock and saves transaction details upon sale.
    * **Smart Discounts:** Automatically applies a 20% discount for registered Senior/PWD customers.
* **Delivery Request:**
    * **2-Step Workflow:** Pharmacists _request_ a delivery (status: 'Requested', stock: 0).
    * Prevents "ghost inventory" from appearing before it physically arrives.
* **Return Request:**
    * **Validation Logic:** Automatically identifies items valid for return (Expired or Discontinued).
    * **2-Step Workflow:** Pharmacists _request_ a return, waiting for Admin approval before removal.

### 3. Transaction Management 📋 (Admin View)
_Oversight tools for the Manager/Admin._
* **Delivery Completion:** Admin verifies received goods and updates status to **'Delivered'**, which officially adds the items to inventory.
* **Return Confirmation:** Admin approves returns (status: **'Returned'**), which triggers the official removal of stock.

### 4. Reports 📈 (Manager View)
_Insights for decision making._
* **Procurement Report:** View total restocks and costs per supplier for a specific month.
* **Customer Purchase Report:** Track spending habits of customers.
* **Medicine Return Report:** Monitor the value and quantity of returned/expired goods.

---

## 🛠️ Technologies Used
* **Language:** Java (JDK 21)
* **GUI Framework:** Java Swing (with Nimbus Look & Feel)
* **Database:** MySQL

---

## ⚙️ Setup & Installation

1.  **Database Setup:**
    * Open MySQL Workbench.
    * Import and run the `CCINFOM S17-11-sql.sql` script.
    * This will create the `dbpharmacy` database and populate it with 10 sample records for every table.

2.  **Application Setup:**
    * Open the project folder (`CCINFOM S17-11-DBAPP`) in your IDE (VS Code, IntelliJ, etc.).
    * Ensure the `mysql-connector-j-9.5.0.jar` (located in the `lib` folder) is added to your project's classpath.
    * Verify database credentials in `src/DB/Database.java`.

3.  **Run:**
    * Run `src/Main.java`.

---

## 📂 Database Schema (3NF)
The system relies on a normalized relational schema to ensure data integrity:
* **Core Entities:** `medicine`, `supplier`, `customer`
* **Transaction Headers:** `purchase`, `delivers`, `return`
* **Transaction Details:** `purchase_details`, `delivery_details`, `return_details`

---

## 👥 Contributors
* **Guo, Anie H.**
* **Tan, Andrea Jadyn K.**
* **Uy, Tara Ysabel P.**
