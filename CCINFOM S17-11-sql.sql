CREATE DATABASE IF NOT EXISTS dbpharmacy;
USE dbpharmacy;
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
	medicine_id INT NOT NULL,
    medicine_name VARCHAR(50) NOT NULL,
    price_bought DECIMAL(10, 2) NOT NULL CHECK (price_bought>=0),
    price_for_sale DECIMAL(10,2) NOT NULL CHECK (price_for_sale>=0),
    quantity_in_stock INT NOT NULL CHECK (quantity_in_stock>=0),
    expiration_date DATE NOT NULL,
    discontinued BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (medicine_id)
);

CREATE TABLE supplier (
	supplier_id INT NOT NULL,
    supplier_name VARCHAR(100) NOT NULL,
    supplier_address VARCHAR(150) NOT NULL,
    supplier_contact_info VARCHAR(100) NOT NULL, -- email of supplier
    supplier_status ENUM('active', 'inactive') DEFAULT 'active',
    PRIMARY KEY (supplier_id)
);

CREATE TABLE customer(
	customer_id INT NOT NULL,
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
delivery_status ENUM('Requested','Shipped','Delivered','Cancelled'),
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
return_status ENUM('Requested','Approved','Rejected','Returned','Cancelled'),
PRIMARY KEY (return_no),
FOREIGN KEY (supplier_id) REFERENCES supplier (supplier_id)
);

CREATE TABLE return_details(
return_no INT NOT NULL,
medicine_id INT NOT NULL,
delivery_no INT NOT NULL,
price_returned DECIMAL(10,2) NOT NULL CHECK (price_returned>0),
quantity_returned INT NOT NULL CHECK (quantity_returned>0),
PRIMARY KEY (return_no, medicine_id, delivery_no),
FOREIGN KEY (return_no) REFERENCES `return`(return_no),
FOREIGN KEY (medicine_id, delivery_no) REFERENCES delivery_details(medicine_id, delivery_no) 
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
