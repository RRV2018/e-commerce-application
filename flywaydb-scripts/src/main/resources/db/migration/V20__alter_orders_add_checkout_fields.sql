-- Add coupon, discount, shipping to orders
ALTER TABLE public.orders ADD COLUMN IF NOT EXISTS coupon_code varchar(50);
ALTER TABLE public.orders ADD COLUMN IF NOT EXISTS discount_amount numeric(10, 2) DEFAULT 0;
ALTER TABLE public.orders ADD COLUMN IF NOT EXISTS shipping_amount numeric(10, 2) DEFAULT 0;
ALTER TABLE public.orders ADD COLUMN IF NOT EXISTS shipping_option_id bigint;

-- Add DELIVERED to order status if not present (alter check constraint)
-- PostgreSQL: drop and recreate check. First check current constraint name.
ALTER TABLE public.orders DROP CONSTRAINT IF EXISTS orders_status_check;
ALTER TABLE public.orders ADD CONSTRAINT orders_status_check CHECK (
    status::text IN ('CREATED', 'PAID', 'CANCELLED', 'SHIPPED', 'DELIVERED')
);
