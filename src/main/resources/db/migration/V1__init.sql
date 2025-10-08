-- ============ USERS & ROLES ============
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(100),
    last_name  VARCHAR(100)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role    VARCHAR(32) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB;

-- ============ CATEGORIES ============
CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          name VARCHAR(100) NOT NULL UNIQUE
    ) ENGINE=InnoDB;

-- ============ PRODUCTS ============
CREATE TABLE IF NOT EXISTS products (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    sku VARCHAR(64) NOT NULL UNIQUE,
    stock INT NOT NULL,
    image_url VARCHAR(512),
    active TINYINT(1) NOT NULL DEFAULT 1,
    category_id BIGINT NOT NULL,
    seller_id BIGINT NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_products_seller   FOREIGN KEY (seller_id)   REFERENCES users(id)
    ) ENGINE=InnoDB;

-- ============ CARTS ============
-- Note: service assumes 1 cart per user; enforce UNIQUE(user_id)
CREATE TABLE IF NOT EXISTS cart (
                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    user_id BIGINT NOT NULL UNIQUE,
                                    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cart_items (
                                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          cart_id BIGINT NOT NULL,
                                          product_id BIGINT NOT NULL,
                                          quantity INT NOT NULL,
                                          CONSTRAINT fk_ci_cart    FOREIGN KEY (cart_id)    REFERENCES cart(id)     ON DELETE CASCADE,
    CONSTRAINT fk_ci_product FOREIGN KEY (product_id) REFERENCES products(id)
    ) ENGINE=InnoDB;
CREATE INDEX idx_ci_cart   ON cart_items(cart_id);
CREATE INDEX idx_ci_prod   ON cart_items(product_id);

-- ============ ORDERS ============
CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      user_id BIGINT NOT NULL,
                                      created_at TIMESTAMP(6) NOT NULL,
    total DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
    ) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                           order_id BIGINT NOT NULL,
                                           product_id BIGINT NOT NULL,
                                           quantity INT NOT NULL,
                                           price_at_purchase DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_oi_order   FOREIGN KEY (order_id)   REFERENCES orders(id)   ON DELETE CASCADE,
    CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES products(id)
    ) ENGINE=InnoDB;
CREATE INDEX idx_oi_order ON order_items(order_id);

-- ============ PAYMENTS ============
CREATE TABLE IF NOT EXISTS payments (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        order_id BIGINT NOT NULL,
                                        provider VARCHAR(32) NOT NULL,
    status   VARCHAR(32) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT fk_pay_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    ) ENGINE=InnoDB;

-- ================== SEED DATA ==================
-- passwords below are bcrypt for the literal string: 'password'
-- ($2a$10$7EqJtq98hPqEX7fNZaFWoO7S5VtKqS3EvhczFMMz9Yx8uW9oOPXG6)
INSERT INTO users (id,email,password,first_name,last_name) VALUES
                                                               (1,'admin@store.com','$2a$10$7EqJtq98hPqEX7fNZaFWoO7S5VtKqS3EvhczFMMz9Yx8uW9oOPXG6','Admin','User'),
                                                               (2,'seller@example.com','$2a$10$7EqJtq98hPqEX7fNZaFWoO7S5VtKqS3EvhczFMMz9Yx8uW9oOPXG6','Seller','User'),
                                                               (3,'buyer@example.com','$2a$10$7EqJtq98hPqEX7fNZaFWoO7S5VtKqS3EvhczFMMz9Yx8uW9oOPXG6','Buyer','User')
    ON DUPLICATE KEY UPDATE email=email;

INSERT INTO user_roles (user_id, role) VALUES
                                           (1,'ROLE_ADMIN'),
                                           (2,'ROLE_SELLER'),
                                           (3,'ROLE_BUYER')
    ON DUPLICATE KEY UPDATE role=VALUES(role);

INSERT INTO categories (id,name) VALUES
                                     (1,'Men'),
                                     (2,'Women')
    ON DUPLICATE KEY UPDATE name=VALUES(name);
ALTER TABLE categories AUTO_INCREMENT = 100;

-- A couple of demo products (one by seller, one by admin)
INSERT INTO products (name,description,price,sku,stock,image_url,active,category_id,seller_id) VALUES
                                                                                                   ('Seller Hoodie','Warm',49.99,'HOODIE-001',25,'https://picsum.photos/200/400',1,1,2),
                                                                                                   ('Admin Jacket','Desc',59.99,'ADMIN-001',10,'https://picsum.photos/200/400',1,1,1)
    ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), stock=VALUES(stock), image_url=VALUES(image_url), active=VALUES(active), category_id=VALUES(category_id), seller_id=VALUES(seller_id);
