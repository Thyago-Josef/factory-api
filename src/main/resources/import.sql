-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;


INSERT INTO rawmaterial (id, code, name, stockquantity)
VALUES (1, 'RM01', 'Steel', 100);

INSERT INTO rawmaterial (id, code, name, stockquantity)
VALUES (2, 'RM02', 'Plastic', 50);

INSERT INTO product (id, code, name, price)
VALUES (1, 'PR01', 'Table', 500);

INSERT INTO productmaterial (id, product_id, rawmaterial_id, requiredquantity)
VALUES (1, 1, 1, 10);

INSERT INTO productmaterial (id, product_id, rawmaterial_id, requiredquantity)
VALUES (2, 1, 2, 5);
