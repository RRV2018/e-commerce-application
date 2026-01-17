
CREATE TABLE IF NOT EXISTS public.inventory
(
    product_id bigint NOT NULL,
    available integer,
    reserved integer,
    version bigint,
    CONSTRAINT inventory_pkey PRIMARY KEY (product_id)
)
