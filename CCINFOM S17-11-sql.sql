CREATE DATABASE IF NOT EXISTS dbpharmacy;
USE dbpharmacy;

-- Create Tables (existing table structure)
DROP TABLE IF EXISTS return_details;
DROP TABLE IF EXISTS `return`;
DROP TABLE IF EXISTS delivery_details;
DROP TABLE IF EXISTS delivers;
DROP TABLE IF EXISTS purchase_details;
DROP TABLE IF EXISTS purchase;
DROP TABLE IF EXISTS medicine;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS customer;

-- Medicine
CREATE TABLE medicine (
	medicine_id INT NOT NULL auto_increment,
    medicine_name VARCHAR(50) NOT NULL,
    price_bought DECIMAL(10, 2) NOT NULL CHECK (price_bought>=0),
    price_for_sale DECIMAL(10,2) NOT NULL CHECK (price_for_sale>=0),
    quantity_in_stock INT NOT NULL CHECK (quantity_in_stock>=0),
    expiration_date DATE NOT NULL,
    discontinued BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (medicine_id)
);

-- Supplier
CREATE TABLE supplier (
	supplier_id INT NOT NULL auto_increment,
    supplier_name VARCHAR(100) NOT NULL,
    supplier_address VARCHAR(150) NOT NULL,
    supplier_contact_info VARCHAR(100) NOT NULL, -- email of supplier
    supplier_status ENUM('active', 'inactive') DEFAULT 'active',
    PRIMARY KEY (supplier_id)
);

-- Customer
CREATE TABLE customer(
	customer_id INT NOT NULL auto_increment,
    customer_name VARCHAR(100) NOT NULL,
    customer_contact_info VARCHAR(100) NOT NULL, -- email of customer
    senior_pwd_id INT UNIQUE,
    customer_status ENUM('active', 'inactive') DEFAULT 'active',
    PRIMARY KEY (customer_id)
);

-- Purchase
CREATE TABLE purchase(
	purchase_no INT NOT NULL auto_increment,
    purchase_date DATE NOT NULL,
    customer_id INT NOT NULL,
    PRIMARY KEY (purchase_no),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

-- Purchase Details
CREATE TABLE purchase_details(
	purchase_no INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity_ordered INT NOT NULL CHECK (quantity_ordered>0), 
    discount DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    total DECIMAL (10,2) NOT NULL,
    PRIMARY KEY (purchase_no, medicine_id),
    FOREIGN KEY (purchase_no) REFERENCES purchase(purchase_no),
    FOREIGN KEY (medicine_id) REFERENCES medicine(medicine_id)
);

-- Delivery
CREATE TABLE delivers(
	delivery_no INT NOT NULL auto_increment,
	supplier_id INT NOT NULL,
	request_date DATE NOT NULL,
	shipped_date DATE,
	delivery_status ENUM('Delivered','Cancelled'),
	PRIMARY KEY (delivery_no),
	FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id)
);

-- Delivery Details
CREATE TABLE delivery_details(
	delivery_no INT NOT NULL,
	medicine_id INT NOT NULL,
	quantity INT NOT NULL CHECK (quantity>0),
	total DECIMAL(10,2),
	PRIMARY KEY (delivery_no, medicine_id),
	FOREIGN KEY (delivery_no) REFERENCES delivers (delivery_no),
	FOREIGN KEY (medicine_id) REFERENCES medicine (medicine_id)
);

-- Return
CREATE TABLE `return`(
	return_no INT NOT NULL auto_increment,
	supplier_id INT NOT NULL,
	reason VARCHAR(250) NOT NULL,
	request_date DATE,
	shipped_date DATE,
	return_status ENUM('Returned','Cancelled'),
	PRIMARY KEY (return_no),
	FOREIGN KEY (supplier_id) REFERENCES supplier (supplier_id)
);

-- Return Details
CREATE TABLE return_details(
	return_no INT NOT NULL,
	delivery_no INT NOT NULL,
	medicine_id INT NOT NULL,
	price_returned DECIMAL(10,2) NOT NULL CHECK (price_returned>0),
	quantity_returned INT NOT NULL CHECK (quantity_returned>0),
	PRIMARY KEY (return_no, delivery_no, medicine_id),
	FOREIGN KEY (return_no) REFERENCES `return`(return_no),
	FOREIGN KEY (delivery_no, medicine_id) REFERENCES delivery_details(delivery_no, medicine_id)
);

-- Prevent selling discontinued or expired medicine
DELIMITER $$
CREATE TRIGGER prevent_expired_or_discontinued_sale
BEFORE INSERT ON purchase_details
FOR EACH ROW
BEGIN
	DECLARE med_exp_date DATE;
    DECLARE med_discontinued BOOLEAN;
    SELECT expiration_date, discontinued
    INTO med_exp_date, med_discontinued
    FROM medicine
    WHERE medicine_id=NEW.medicine_id;
    IF med_discontinued=TRUE OR med_exp_date<= CURDATE() THEN
		SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT='Cannot sell expired or discontinued medicine';
	END IF;
END $$
DELIMITER ;

-- -----------------------------------------------------------------
-- Clean Test Data
-- Commented out for safety
-- Easily reset the data by highlighting below and executing
-- -----------------------------------------------------------------
/*
SET FOREIGN_KEY_CHECKS = 0; -- Disable foreign key checks to allow truncating
TRUNCATE TABLE return_details;
TRUNCATE TABLE `return`;
TRUNCATE TABLE purchase_details;
TRUNCATE TABLE purchase;
TRUNCATE TABLE delivery_details;
TRUNCATE TABLE delivers;
TRUNCATE TABLE medicine;
TRUNCATE TABLE supplier;
TRUNCATE TABLE customer;
SET FOREIGN_KEY_CHECKS = 1; -- Re-enable foreign key checks
*/

-- ----------------------------------------------------------------- 
-- SELECT STATEMENTS IF NEEDED
-- -----------------------------------------------------------------
/*
SELECT * FROM customer;
SELECT * FROM medicine;
SELECT * FROM supplier;
SELECT * FROM delivers;
SELECT * FROM delivery_details;
SELECT * FROM `return`;
SELECT * FROM return_details;
SELECT * FROM purchase;
SELECT * FROM purchase_details;
*/

-- -----------------------------------------------------------------
-- Sample Data (Insert 10 Records for Each Table)
-- -----------------------------------------------------------------
-- 10 Customer
INSERT INTO customer (customer_id, customer_name, customer_contact_info, senior_pwd_id, customer_status)
VALUES
(1, 'Cinnamoroll', 'cinna@sanrio.com', 12345, 'active'),
(2, 'Sabine Callas', 'viper@riot.com', NULL, 'active'),
(3, 'Wonhee Lee', 'illit@belift.com', 67890, 'inactive'),
(4, 'Tony Stark', 'stark@marvel.com', NULL, 'active'),
(5, 'Lara Croft', 'lc@tombraider.com', 98765, 'active'),
(6, 'Crisostomo Ibarra', 'simoun@email.com', NULL, 'active'),
(7, 'Cal Kestis', 'cal@starwars.com', 11111, 'active'),
(8, 'Ann Takamaki', 'panther@persona.com', 22222, 'active'),
(9, 'Kanao Tsuyuri', 'kanao@kny.com', 33333, 'active'),
(10, 'Jose Rizal', 'pepe@email.com', NULL, 'inactive');

-- 10 Supplier
INSERT INTO supplier (supplier_id, supplier_name, supplier_address, supplier_contact_info, supplier_status)
VALUES
(1, 'PharmaCo', '1 Main St', 'contact@pharmaco.com', 'active'),
(2, 'MedSupply', '2 Ayala Ave', 'sales@medsupply.com', 'active'),
(3, 'Valve Corp', '3 Steam Rd', 'valve@email.com', 'active'),
(4, 'BioGenerics', '4 QC Blvd', 'orders@biogen.com', 'active'),
(5, 'MetroDrug', '5 Pasig Way', 'metro@drug.com', 'active'),
(6, 'Nintendo', '6 Kyoto St', 'nintendo@email.com', 'active'),
(7, 'PharmaSolutions', '7 Sol Bldg', 'solutions@pharma.com', 'active'),
(8, 'Unilab', '8 United St', 'contact@unilab.com', 'active'),
(9, 'Sony', '9 San Mateo', 's9@email.com', 'active'),
(10, 'HealthWest', '10 Taft Ave', 'hw@email.com', 'inactive');

-- 10 Medicine
-- Cover cases (sellable, expired, OOS, etc.)
INSERT INTO medicine (medicine_id, medicine_name, price_bought, price_for_sale, quantity_in_stock, expiration_date, discontinued)
VALUES
(1, 'Biogesic 500mg', 8.00, 12.00, 100, '2026-12-31', false),
(2, 'Neozep Forte', 12.00, 18.00, 75, '2027-01-01', false),
(3, 'Aspirin 100mg', 20.00, 25.00, 100, '2026-12-01', false),
(4, 'Advil 200mg', 15.00, 20.00, 50, '2026-01-01', false),
(5, 'Amoxicillin 250mg', 10.00, 15.00, 80, '2026-05-01', false),
(6, 'Paracetamol 500mg', 10.00, 15.00, 50, '2025-11-01', false), -- EXPIRED (for Return test)
(7, 'Cough Syrup', 30.00, 45.00, 20, '2026-06-01', true),  -- DISCONTINUED (for Return test)
(8, 'Bioflu Tab', 10.00, 15.00, 0, '2027-01-01', false),  -- Stock 0
(9, 'Solmux 500mg', 20.00, 28.00, 0, '2027-02-01', false),  -- Stock 0
(10, 'Vicks Vaporub', 10.00, 20.00, 5, '2024-01-01', false); -- EXPIRED

-- 10 Delivery
INSERT INTO delivers (delivery_no, supplier_id, request_date, shipped_date, delivery_status)
VALUES
(1, 1, '2025-01-05', '2025-01-10', 'Delivered'),
(2, 2, '2025-01-06', '2025-01-11', 'Delivered'),
(3, 3, '2025-01-07', '2025-01-12', 'Delivered'),
(4, 4, '2025-02-01', '2025-02-05', 'Delivered'),
(5, 5, '2025-02-02', '2025-02-06', 'Delivered'),
(6, 6, '2025-11-20', '2025-11-22', 'Delivered'), -- **RECENT** (<7 days from Nov 26)
(7, 7, '2025-11-20', '2025-11-22', 'Delivered'), -- **RECENT** (<7 days from Nov 26)
(8, 8, '2024-04-01', '2024-04-05', 'Delivered'),
(9, 9, '2025-11-25', NULL, 'Cancelled'),
(10, 10, '2025-11-01', NULL, 'Cancelled');

-- 10 Delivery Details (Linking Deliveries to Medicine Batches)
INSERT INTO delivery_details (delivery_no, medicine_id, quantity, total)
VALUES
(1, 1, 100, 800.00),
(2, 2, 75, 900.00),
(3, 3, 100, 2000.00),
(4, 4, 50, 750.00),
(5, 5, 80, 800.00),
(6, 6, 50, 500.00),   
(7, 7, 20, 600.00),  
(8, 8, 100, 1000.00),  
(9, 9, 100, 1000.00), 
(10, 10, 50, 500.00);

-- 10 Purchase
INSERT INTO purchase (purchase_no, purchase_date, customer_id)
VALUES
(1, '2025-10-01', 1), 
(2, '2025-10-02', 2), 
(3, '2025-10-03', 4), 
(4, '2025-10-04', 5), 
(5, '2025-10-05', 6),
(6, '2025-11-01', 1), 
(7, '2025-11-02', 7), 
(8, '2025-11-03', 8), 
(9, '2025-11-04', 9), 
(10, '2025-11-05', 2);

-- 10 Purchase Details (Linking Purchases to Medicine Batches)
INSERT INTO purchase_details (purchase_no, medicine_id, quantity_ordered, discount, total)
VALUES
(1, 1, 2, 4.80, 19.20), 
(2, 2, 5, 0.00, 90.00), 
(3, 4, 1, 4.00, 16.00), 
(4, 5, 10, 30.00, 120.00),
(5, 1, 5, 0.00, 60.00), 
(6, 2, 3, 10.80, 43.20), 
(7, 3, 1, 5.00, 20.00), 
(8, 4, 2, 0.00, 40.00),
(9, 1, 10, 0.00, 120.00), 
(10, 5, 4, 0.00, 60.00);

-- 10 Return
INSERT INTO `return` (return_no, supplier_id, reason, request_date, shipped_date, return_status)
VALUES
(1, 1, 'Expired', '2025-05-01', '2025-05-03', 'Returned'),
(2, 2, 'Discontinued', '2025-05-02', '2025-05-04', 'Returned'),
(3, 3, 'Expired', '2025-05-03', NULL, 'Cancelled'),
(4, 4, 'Expired', '2025-05-04', '2025-05-06', 'Returned'),
(5, 5, 'Discontinued', '2025-05-05', '2025-05-07', 'Returned'),
(6, 6, 'Expired', '2025-11-25', NULL, 'Returned'), -- Linked to Med 6
(7, 7, 'Expired', '2025-11-25', NULL, 'Returned'), -- Linked to Med 7
(8, 8, 'Expired', '2025-01-02', '2025-01-04', 'Returned'),
(9, 9, 'Expired', '2025-02-02', '2025-02-04', 'Returned'),
(10, 10, 'Discontinued', '2025-06-02', NULL, 'Cancelled');

-- Return Details (Linking Returns to Batches & Deliveries)
INSERT INTO return_details (return_no, medicine_id, delivery_no, price_returned, quantity_returned)
VALUES
(1, 6, 6, 10.00, 50), 
(2, 7, 7, 30.00, 20), 
(3, 8, 8, 5.00, 200), 
(4, 4, 4, 15.00, 10), 
(5, 5, 5, 10.00, 15),
(6, 1, 1, 8.00, 5), 
(7, 2, 2, 12.00, 10), 
(8, 3, 3, 20.00, 20), 
(9, 9, 9, 20.00, 10), 
(10, 10, 10, 10.00, 50);