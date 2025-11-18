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

CREATE TABLE medicine (
	medicine_id INT NOT NULL auto_increment,
    medicine_name VARCHAR(50) NOT NULL,
    price_bought DECIMAL(10, 2) NOT NULL CHECK (price_bought>=0),
    price_for_sale DECIMAL(10,2) NOT NULL CHECK (price_for_sale>=0),
    quantity_in_stock INT NOT NULL CHECK (quantity_in_stock>=0),
    expiration_date DATE NOT NULL,
    discontinued BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (medicine_id),
    UNIQUE (medicine_name, expiration_date)
);

CREATE TABLE supplier (
	supplier_id INT NOT NULL auto_increment,
    supplier_name VARCHAR(100) NOT NULL,
    supplier_address VARCHAR(150) NOT NULL,
    supplier_contact_info VARCHAR(100) NOT NULL, -- email of supplier
    supplier_status ENUM('active', 'inactive') DEFAULT 'active',
    PRIMARY KEY (supplier_id)
);

CREATE TABLE customer(
	customer_id INT NOT NULL auto_increment,
    customer_name VARCHAR(100) NOT NULL,
    customer_contact_info VARCHAR(100) NOT NULL, -- email of customer
    senior_pwd_id INT UNIQUE,
    customer_status ENUM('active', 'inactive') DEFAULT 'active',
    PRIMARY KEY (customer_id)
);

CREATE TABLE purchase(
	purchase_no INT NOT NULL auto_increment,
    purchase_date DATE NOT NULL,
    customer_id INT NOT NULL,
    PRIMARY KEY (purchase_no),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

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

CREATE TABLE delivers(
delivery_no INT NOT NULL auto_increment,
supplier_id INT NOT NULL,
request_date DATE NOT NULL,
shipped_date DATE,
delivery_status ENUM('Delivered','Cancelled'),
PRIMARY KEY (delivery_no),
FOREIGN KEY (supplier_id) REFERENCES supplier(supplier_id)
);

CREATE TABLE delivery_details(
delivery_no INT NOT NULL,
medicine_id INT NOT NULL,
quantity INT NOT NULL CHECK (quantity>0),
total DECIMAL(10,2),
PRIMARY KEY (delivery_no, medicine_id),
FOREIGN KEY (delivery_no) REFERENCES delivers (delivery_no),
FOREIGN KEY (medicine_id) REFERENCES medicine (medicine_id)
);

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
-- 10 Customer Records
INSERT INTO customer (customer_id, customer_name, customer_contact_info, senior_pwd_id, customer_status)
VALUES
(1, 'Anie Guo', 'anie@gmail.com', 12345, 'active'),
(2, 'Tara Uy', 'tara@gmail.com', NULL, 'active'),
(3, 'Andrea Tan', 'andrea@gmail.com', 67890, 'inactive'),
(4, 'Juan Dela Cruz', 'juan.dc@email.com', NULL, 'active'),
(5, 'Maria Clara', 'mc@email.com', 98765, 'active'),
(6, 'Crisostomo Ibarra', 'crisos@email.com', NULL, 'active'),
(7, 'Leni Robredo', 'leni@email.com', 11111, 'active'),
(8, 'Rodrigo Duterte', 'digong@email.com', 22222, 'active'),
(9, 'Bongbong Marcos', 'bbm@email.com', 33333, 'active'),
(10, 'Kiko Pangilinan', 'kiko@email.com', NULL, 'inactive');

-- 10 Supplier Records
INSERT INTO supplier (supplier_id, supplier_name, supplier_address, supplier_contact_info, supplier_status)
VALUES
(1, 'PharmaCo Manila', '123 Main St, Manila', 'contact@pharmaco.com', 'active'),
(2, 'MedSupply Inc.', '456 Ayala Ave, Makati', 'sales@medsupply.com', 'active'),
(3, 'HealthWest Distrib', '789 Taft Ave, Pasay', 'info@healthwest.com', 'inactive'),
(4, 'BioGenerics PH', '101 pharma bldg, Quezon City', 'orders@biogenerics.ph', 'active'),
(5, 'MetroDrug', '202 Drugway, Pasig', 'metro@drug.com', 'active'),
(6, 'AsiaMed', '303 Asia Tower, Alabang', 'info@asiamed.com', 'active'),
(7, 'PharmaSolutions', '404 Sol Bldg, Manila', 'solutions@pharma.com', 'active'),
(8, 'Unilab', '505 United St, Mandaluyong', 'contact@unilab.com.ph', 'active'),
(9, 'Zuellig Pharma', '606 Zuellig Bldg, Makati', 'ask@zuelligpharma.com', 'active'),
(10, 'DefunctMed', '1 Old Rd, Cavite', 'n/a', 'inactive');

-- 15 Medicine (Batch) Records
-- We need more than 10 to cover all cases (sellable, expired, OOS, etc.)
INSERT INTO medicine (medicine_id, medicine_name, price_bought, price_for_sale, quantity_in_stock, expiration_date, discontinued)
VALUES
-- Sellable Batches
(101, 'Biogesic 500mg', 8.00, 12.00, 100, '2026-12-31', false),
(102, 'Neozep Forte', 12.00, 18.00, 75, '2027-01-01', false),
(103, 'Aspirin 100mg', 20.00, 25.00, 100, '2026-12-01', false),
(104, 'Advil 200mg', 15.00, 20.00, 50, '2026-01-01', false),
(105, 'Amoxicillin 250mg', 10.00, 15.00, 80, '2026-05-01', false),

-- Expired / Discontinued Batches (For Return Tests)
(201, 'Expired Paracetamol', 10.00, 15.00, 50, '2025-01-01', false), -- Expired
(202, 'Old Cough Syrup', 30.00, 45.00, 20, '2026-06-01', true),  -- Discontinued
(203, 'Expired Vitamin C', 5.00, 8.00, 200, '2025-02-01', false), -- Expired
(204, 'Expired Aspirin', 20.00, 25.00, 100, '2024-01-01', false), -- Expired
(205, 'Discontinued Cream', 50.00, 75.00, 10, '2026-01-01', true),  -- Discontinued

-- Batches for "Requested" Deliveries (Stock is 0)
(301, 'Bioflu Tab', 10.00, 15.00, 0, '2027-01-01', false),
(302, 'Solmux 500mg', 20.00, 28.00, 0, '2027-02-01', false),
(303, 'Alaxan FR', 13.00, 19.00, 0, '2027-03-01', false),

-- Batches for "Requested" Returns (Stock > 0)
(401, 'Expired Advil', 15.00, 20.00, 50, '2025-03-01', false),
(402, 'Expired Neozep', 12.00, 18.00, 60, '2025-04-01', false);

-- 10 Delivery Records
INSERT INTO delivers (delivery_no, supplier_id, request_date, shipped_date, delivery_status)
VALUES
(1, 1, '2024-12-01', '2024-12-05', 'Delivered'), -- Linked to batches 101, 103
(2, 2, '2025-01-15', '2025-01-20', 'Delivered'), -- Linked to batches 102, 104
(3, 4, '2025-02-01', '2025-02-05', 'Delivered'), -- Linked to 105
(4, 5, '2024-10-01', '2024-10-05', 'Delivered'), -- Linked to 201, 202
(5, 6, '2024-11-01', '2024-11-05', 'Delivered'), -- Linked to 203
(6, 8, '2024-08-01', '2024-08-05', 'Delivered'), -- Linked to 204, 205
(7, 9, '2025-02-15', '2025-02-20', 'Delivered'), -- Linked to 401
(8, 1, '2025-03-01', '2025-03-05', 'Delivered'), -- Linked to 402
(9, 2, '2025-11-15', NULL, 'Cancelled'), -- For "Manage Delivery" test (Linked to 301, 302)
(10, 4, '2025-11-16', NULL, 'Cancelled'); -- For "Manage Delivery" test (Linked to 303)

-- Delivery Details Records (Linking Deliveries to Medicine Batches)
INSERT INTO delivery_details (delivery_no, medicine_id, quantity, total)
VALUES
(1, 101, 100, 800.00),
(1, 103, 100, 2000.00),
(2, 102, 75, 900.00),
(2, 104, 50, 750.00),
(3, 105, 80, 800.00),
(4, 201, 50, 500.00),
(4, 202, 20, 600.00),
(5, 203, 200, 1000.00),
(6, 204, 100, 2000.00),
(6, 205, 10, 500.00),
(7, 401, 50, 750.00),
(8, 402, 60, 720.00),
(9, 301, 100, 1000.00), -- Linked to "Requested" delivery
(9, 302, 100, 2000.00), -- Linked to "Requested" delivery
(10, 303, 50, 650.00);  -- Linked to "Cancelled" delivery

-- 10 Purchase Records
INSERT INTO purchase (purchase_no, purchase_date, customer_id)
VALUES
(1, '2025-10-01', 1), -- Anie (Senior)
(2, '2025-10-02', 2), -- Tara (Regular)
(3, '2025-10-03', 4), -- Juan
(4, '2025-10-04', 5), -- Maria (Senior)
(5, '2025-10-05', 6), -- Crisostomo
(6, '2025-11-01', 1), -- Anie (Senior)
(7, '2025-11-02', 2), -- Tara
(8, '2025-11-03', 7), -- Leni (Senior)
(9, '2025-11-04', 8), -- Rodrigo (Senior)
(10, '2025-11-05', 5); -- Maria (Senior)

-- Purchase Details Records (Linking Purchases to Medicine Batches)
INSERT INTO purchase_details (purchase_no, medicine_id, quantity_ordered, discount, total)
VALUES
(1, 101, 2, 2.40, 9.60),   -- Anie (Senior) buys Biogesic
(2, 102, 5, 0.00, 90.00),   -- Tara (Regular) buys Neozep
(3, 104, 10, 0.00, 200.00), -- Juan buys Advil
(4, 105, 10, 30.00, 120.00), -- Maria (Senior) buys Amoxicillin
(5, 101, 5, 0.00, 60.00),   -- Crisostomo buys Biogesic
(6, 102, 3, 10.80, 43.20),  -- Anie (Senior) buys Neozep
(7, 103, 1, 0.00, 25.00),   -- Tara buys Aspirin
(8, 104, 2, 8.00, 32.00),   -- Leni (Senior) buys Advil
(9, 101, 10, 24.00, 96.00), -- Rodrigo (Senior) buys Biogesic
(10, 105, 4, 12.00, 48.00); -- Maria (Senior) buys Amoxicillin

-- 10 Return Records
INSERT INTO `return` (return_no, supplier_id, reason, request_date, shipped_date, return_status)
VALUES
(1, 1, 'Expired Batch', '2025-05-01', '2025-05-03', 'Returned'), -- Linked to 201
(2, 1, 'Discontinued', '2025-05-02', '2025-05-04', 'Returned'), -- Linked to 202
(3, 2, 'Expired Batch', '2025-05-03', NULL, 'Cancelled'),     -- Linked to 203
(4, 8, 'Expired Batch', '2025-05-04', '2025-05-06', 'Returned'), -- Linked to 204
(5, 8, 'Discontinued', '2025-05-05', '2025-05-07', 'Returned'), -- Linked to 205
(6, 9, 'Expired Batch', '2025-11-10', NULL, 'Cancelled'),     -- For "Manage Return" test (Linked to 401)
(7, 1, 'Expired Batch', '2025-11-11', NULL, 'Cancelled'),     -- For "Manage Return" test (Linked to 402)
(8, 1, 'Expired Batch', '2025-01-02', '2025-01-04', 'Returned'), -- For Report
(9, 2, 'Expired Batch', '2025-02-02', '2025-02-04', 'Returned'), -- For Report
(10, 1, 'Discontinued', '2025-06-02', NULL, 'Cancelled');    -- For Report

-- Return Details Records (Linking Returns to Batches & Deliveries)
INSERT INTO return_details (return_no, medicine_id, delivery_no, price_returned, quantity_returned)
VALUES
(1, 201, 4, 10.00, 50),  -- Return 1
(2, 202, 4, 30.00, 20),  -- Return 2
(3, 203, 5, 5.00, 200), -- Return 3 (Cancelled)
(4, 204, 6, 20.00, 100), -- Return 4
(5, 205, 6, 50.00, 10),  -- Return 5
(6, 401, 7, 15.00, 50),  -- Return 6 (Requested)
(7, 402, 8, 12.00, 60),  -- Return 7 (Requested)
(8, 101, 1, 8.00, 10),   -- Return 8 (Partial Return)
(9, 103, 1, 20.00, 20),  -- <-- FIX: Batch 103 came from Delivery 1
(10, 104, 2, 15.00, 10); -- <-- FIX: Batch 104 came from Delivery 2
