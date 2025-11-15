CREATE TABLE categories
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_name ON categories (name);

CREATE TABLE furniture
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(200)   NOT NULL,
    price           DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    material        VARCHAR(20)    NOT NULL CHECK (material IN ('GLASS', 'PLASTIC')),
    description     TEXT,
    image_url       VARCHAR(500),
    production_days INTEGER CHECK (production_days > 0),
    created_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_furniture_name ON furniture (name);
CREATE INDEX idx_furniture_price ON furniture (price);
CREATE INDEX idx_furniture_material ON furniture (material);


CREATE TABLE furniture_category
(
    furniture_id UUID NOT NULL,
    category_id  UUID NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (furniture_id, category_id),
    FOREIGN KEY (furniture_id) REFERENCES furniture (id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);

CREATE INDEX idx_furniture_category_furniture_id ON furniture_category (furniture_id);
CREATE INDEX idx_furniture_category_category_id ON furniture_category (category_id);

CREATE TABLE users
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_name  VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    phone      VARCHAR(20)  NOT NULL,
    address    TEXT         NOT NULL,
    role       VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    discount   DECIMAL(5, 2)    DEFAULT 0.00 CHECK (discount >= 0 AND discount <= 100),
    created_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP        DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_user_name ON users (user_name);

CREATE TABLE orders
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sum_cost       DECIMAL(10, 2) NOT NULL CHECK (sum_cost >= 0),
    delivery_date  DATE           NOT NULL CHECK (delivery_date >= CURRENT_DATE),
    user_id        UUID           NOT NULL,
    created_at     TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    status         VARCHAR(255)    NOT NULL CHECK (status IN ('NEW', 'IN_PRODUCTION', 'READY', 'DELIVERED', 'CANCELLED')),
    customer_phone VARCHAR(255)    NOT NULL,
    comment        TEXT,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_delivery_date ON orders (delivery_date);
CREATE INDEX idx_orders_created_at ON orders (created_at);

CREATE TABLE order_furniture
(
    order_id     UUID           NOT NULL,
    furniture_id UUID           NOT NULL,
    quantity     INTEGER   DEFAULT 1 CHECK (quantity > 0),
    unit_price   DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (order_id, furniture_id),
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (furniture_id) REFERENCES furniture (id) ON DELETE CASCADE
);

CREATE INDEX idx_order_furniture_order_id ON order_furniture (order_id);
CREATE INDEX idx_order_furniture_furniture_id ON order_furniture (furniture_id);

CREATE TABLE favorites
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id      UUID NOT NULL,
    furniture_id UUID NOT NULL,
    added_at     TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (furniture_id) REFERENCES furniture (id) ON DELETE CASCADE,
    UNIQUE (user_id, furniture_id) -- Один пользователь не может добавить одну мебель дважды
);

CREATE INDEX idx_favorites_user_id ON favorites (user_id);
CREATE INDEX idx_favorites_furniture_id ON favorites (furniture_id);
CREATE INDEX idx_favorites_added_at ON favorites (added_at);

-- Вставляем начальные категории
INSERT INTO categories (id, name, description)
VALUES (gen_random_uuid(), 'Стулья', 'Различные виды стульев для дома и офиса'),
       (gen_random_uuid(), 'Столы', 'Обеденные, рабочие и кофейные столы'),
       (gen_random_uuid(), 'Диваны', 'Удобные диваны для гостиной'),
       (gen_random_uuid(), 'Кровати', 'Спальные места различных размеров'),
       (gen_random_uuid(), 'Шкафы', 'Мебель для хранения вещей');

-- Вставляем начальную мебель
INSERT INTO furniture (id, name, price, material, description, image_url, production_days)
VALUES (gen_random_uuid(), 'Офисный стул', 5000.00, 'PLASTIC', 'Эргономичный стул для работы в офисе',
        '/images/office_chair.jpg', 5),
       (gen_random_uuid(), 'Обеденный стол', 15000.00, 'GLASS', 'Стеклянный стол для столовой',
        '/images/dining_table.jpg', 10),
       (gen_random_uuid(), 'Компьютерный стол', 12000.00, 'PLASTIC', 'Стол для работы за компьютером',
        '/images/computer_table.jpg', 7),
       (gen_random_uuid(), 'Барный стул', 3500.00, 'PLASTIC', 'Стул для барной стойки', '/images/bar_stool.jpg', 3),
       (gen_random_uuid(), 'Журнальный стол', 8000.00, 'GLASS', 'Небольшой стол для гостиной',
        '/images/coffee_table.jpg', 6);

-- Связываем мебель с категориями (предполагая что мы знаем ID из предыдущих вставок)
-- На практике лучше сначала получить ID, но для примера используем подзапросы
INSERT INTO furniture_category (furniture_id, category_id)
SELECT f.id, c.id
FROM furniture f,
     categories c
WHERE f.name = 'Офисный стул'
  AND c.name = 'Стулья';

INSERT INTO furniture_category (furniture_id, category_id)
SELECT f.id, c.id
FROM furniture f,
     categories c
WHERE f.name = 'Обеденный стол'
  AND c.name = 'Столы';

INSERT INTO furniture_category (furniture_id, category_id)
SELECT f.id, c.id
FROM furniture f,
     categories c
WHERE f.name = 'Компьютерный стол'
  AND c.name = 'Столы';

INSERT INTO furniture_category (furniture_id, category_id)
SELECT f.id, c.id
FROM furniture f,
     categories c
WHERE f.name = 'Барный стул'
  AND c.name = 'Стулья';

INSERT INTO furniture_category (furniture_id, category_id)
SELECT f.id, c.id
FROM furniture f,
     categories c
WHERE f.name = 'Журнальный стол'
  AND c.name = 'Столы';

;