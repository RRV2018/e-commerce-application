INSERT INTO INVENTORY(product_id, available, reserved, version)
select p.id, p.stock, 0, 0 from products p;