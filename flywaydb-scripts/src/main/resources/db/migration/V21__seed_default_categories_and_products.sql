-- Seed default categories (ignore if already present)
INSERT INTO public.categories (name)
SELECT c FROM (VALUES
  ('Electronics'),
  ('Clothing'),
  ('Home & Garden'),
  ('Sports'),
  ('Books'),
  ('Toys'),
  ('Health'),
  ('Automotive')
) AS t(c)
ON CONFLICT (name) DO NOTHING;

-- Seed 10,000 default products (runs once when Flyway applies this version)
WITH numbered_cats AS (
  SELECT id, row_number() OVER (ORDER BY id) AS rn FROM public.categories
),
cat_count AS (
  SELECT count(*) AS cnt FROM public.categories
)
INSERT INTO public.products (name, description, price, stock, category_id)
SELECT
  'Product ' || i,
  'Default product item ' || i || ' for catalog.',
  (10 + (random() * 490))::numeric(38,2),
  (1 + (random() * 99))::integer,
  (SELECT id FROM numbered_cats WHERE rn = 1 + ((i - 1) % greatest((SELECT cnt FROM cat_count), 1)))
FROM generate_series(1, 10000) AS i;
