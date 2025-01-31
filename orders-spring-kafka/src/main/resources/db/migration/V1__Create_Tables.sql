CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    sku VARCHAR(12) NOT NULL UNIQUE,
    barcode VARCHAR(12) UNIQUE,
    name VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    thumbnail_url TEXT NOT NULL,
    price DECIMAL NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    number VARCHAR(36) NOT NULL UNIQUE,
    subtotal_amount DECIMAL,
    total_amount DECIMAL
);

CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY,
    index INTEGER,
    description TEXT,
    quantity INTEGER NOT NULL,
    total_amount DECIMAL,
    order_id UUID NOT NULL REFERENCES orders(id),
    product_id UUID NOT NULL REFERENCES products(id)
);

-- duplicate inserts will fail.
CREATE UNIQUE INDEX unique_order_item_key ON order_items(
    LEAST(order_id, product_id),
    GREATEST(order_id, product_id)
);
