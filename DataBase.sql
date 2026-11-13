
IF DB_ID(N'laptopspace_db') IS NULL
    EXEC(N'CREATE DATABASE [laptopspace_db] COLLATE Vietnamese_100_CI_AS');
GO

USE [laptopspace_db];
GO

-- ---------------------------------------------------------------------
-- 1. users : tài khoản người dùng / quản trị viên
-- ---------------------------------------------------------------------
IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        id BIGINT IDENTITY(1,1) NOT NULL,
        username NVARCHAR(50) NOT NULL,
        password NVARCHAR(255) NOT NULL,
        full_name NVARCHAR(100) NOT NULL,
        email NVARCHAR(100) NOT NULL,
        phone NVARCHAR(15) NULL,
        address NVARCHAR(MAX) NULL,
        role NVARCHAR(20) NOT NULL,
        enabled BIT NOT NULL CONSTRAINT df_users_enabled DEFAULT (1),
        CONSTRAINT pk_users PRIMARY KEY (id),
        CONSTRAINT uk_users_username UNIQUE (username),
        CONSTRAINT uk_users_email UNIQUE (email)
    );
END;
GO

-- ---------------------------------------------------------------------
-- 2. categories : danh mục / thương hiệu
-- ---------------------------------------------------------------------
IF OBJECT_ID(N'dbo.categories', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.categories (
        id BIGINT IDENTITY(1,1) NOT NULL,
        name NVARCHAR(100) NOT NULL,
        description NVARCHAR(MAX) NULL,
        CONSTRAINT pk_categories PRIMARY KEY (id),
        CONSTRAINT uk_categories_name UNIQUE (name)
    );
END;
GO

-- ---------------------------------------------------------------------
-- 3. laptops : sản phẩm
-- ---------------------------------------------------------------------
IF OBJECT_ID(N'dbo.laptops', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.laptops (
        id BIGINT IDENTITY(1,1) NOT NULL,
        name NVARCHAR(200) NOT NULL,
        price FLOAT(53) NOT NULL,
        discount_price FLOAT(53) NULL,
        stock_quantity INT NOT NULL,
        active BIT NOT NULL CONSTRAINT df_laptops_active DEFAULT (1),
        cpu NVARCHAR(MAX) NULL,
        ram NVARCHAR(MAX) NULL,
        storage NVARCHAR(MAX) NULL,
        screen NVARCHAR(MAX) NULL,
        vga NVARCHAR(MAX) NULL,
        operating_system NVARCHAR(MAX) NULL,
        webcam NVARCHAR(MAX) NULL,
        weight NVARCHAR(MAX) NULL,
        battery NVARCHAR(MAX) NULL,
        ports NVARCHAR(MAX) NULL,
        description NVARCHAR(MAX) NULL,
        key_features NVARCHAR(MAX) NULL,
        image_url NVARCHAR(255) NULL,
        model_group NVARCHAR(100) NULL,
        category_id BIGINT NOT NULL,
        CONSTRAINT pk_laptops PRIMARY KEY (id),
        CONSTRAINT fk_laptops_category
            FOREIGN KEY (category_id) REFERENCES dbo.categories (id)
    );
END;
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = N'idx_laptops_model_group'
      AND object_id = OBJECT_ID(N'dbo.laptops')
)
    CREATE INDEX idx_laptops_model_group ON dbo.laptops (model_group);
GO

-- ---------------------------------------------------------------------
-- 4. laptop_images : thư viện ảnh phụ của sản phẩm
-- ---------------------------------------------------------------------
IF OBJECT_ID(N'dbo.laptop_images', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.laptop_images (
        id BIGINT IDENTITY(1,1) NOT NULL,
        image_url NVARCHAR(255) NULL,
        laptop_id BIGINT NULL,
        CONSTRAINT pk_laptop_images PRIMARY KEY (id),
        CONSTRAINT fk_laptop_images_laptop
            FOREIGN KEY (laptop_id) REFERENCES dbo.laptops (id)
    );
END;
GO

-- ---------------------------------------------------------------------
-- 5. carts : giỏ hàng (mỗi user đúng 1 giỏ - quan hệ 1-1)
-- ---------------------------------------------------------------------
IF OBJECT_ID(N'dbo.carts', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.carts (
        id BIGINT IDENTITY(1,1) NOT NULL,
        user_id BIGINT NOT NULL,
        CONSTRAINT pk_carts PRIMARY KEY (id),
        CONSTRAINT uk_carts_user UNIQUE (user_id),
        CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES dbo.users (id)
    );
END;
GO

-- ---------------------------------------------------------------------
-- 6. cart_items : các mặt hàng trong giỏ
-- ---------------------------------------------------------------------
IF OBJECT_ID(N'dbo.cart_items', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.cart_items (
        id BIGINT IDENTITY(1,1) NOT NULL,
        cart_id BIGINT NOT NULL,
        laptop_id BIGINT NOT NULL,
        quantity INT NOT NULL,
        CONSTRAINT pk_cart_items PRIMARY KEY (id),
        CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES dbo.carts (id),
        CONSTRAINT fk_cart_items_laptop FOREIGN KEY (laptop_id) REFERENCES dbo.laptops (id)
    );
END;
GO

-- ---------------------------------------------------------------------
-- 7. orders : đơn hàng (user_id = NULL nếu là khách vãng lai)
-- ---------------------------------------------------------------------
IF OBJECT_ID(N'dbo.orders', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.orders (
        id BIGINT IDENTITY(1,1) NOT NULL,
        customer_name NVARCHAR(100) NOT NULL,
        customer_phone NVARCHAR(20) NOT NULL,
        shipping_address NVARCHAR(255) NOT NULL,
        note NVARCHAR(MAX) NULL,
        total_amount FLOAT(53) NOT NULL,
        order_date DATETIME2(6) NOT NULL,
        status NVARCHAR(50) NOT NULL,
        payment_method NVARCHAR(20) NULL CONSTRAINT df_orders_payment_method DEFAULT (N'COD'),
        payment_status NVARCHAR(50) NULL CONSTRAINT df_orders_payment_status DEFAULT (N'CHƯA THANH TOÁN'),
        user_id BIGINT NULL,
        CONSTRAINT pk_orders PRIMARY KEY (id),
        CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES dbo.users (id)
    );
END;
GO

-- ---------------------------------------------------------------------
-- 8. order_details : chi tiết từng đơn hàng
-- ---------------------------------------------------------------------
IF OBJECT_ID(N'dbo.order_details', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.order_details (
        id BIGINT IDENTITY(1,1) NOT NULL,
        order_id BIGINT NOT NULL,
        laptop_id BIGINT NOT NULL,
        quantity INT NOT NULL,
        price FLOAT(53) NOT NULL,
        CONSTRAINT pk_order_details PRIMARY KEY (id),
        CONSTRAINT fk_order_details_order FOREIGN KEY (order_id) REFERENCES dbo.orders (id),
        CONSTRAINT fk_order_details_laptop FOREIGN KEY (laptop_id) REFERENCES dbo.laptops (id)
    );
END;
GO

-- ---------------------------------------------------------------------
-- 9. reviews : đánh giá sản phẩm (1-5 sao)
-- ---------------------------------------------------------------------
IF OBJECT_ID(N'dbo.reviews', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.reviews (
        id BIGINT IDENTITY(1,1) NOT NULL,
        user_id BIGINT NOT NULL,
        laptop_id BIGINT NOT NULL,
        rating INT NOT NULL,
        comment NVARCHAR(MAX) NULL,
        created_at DATETIME2(6) NOT NULL,
        CONSTRAINT pk_reviews PRIMARY KEY (id),
        CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES dbo.users (id),
        CONSTRAINT fk_reviews_laptop FOREIGN KEY (laptop_id) REFERENCES dbo.laptops (id)
    );
END;
GO

-- ---------------------------------------------------------------------
-- 10. Dữ liệu khởi tạo: tài khoản quản trị mặc định
--     (giống DatabaseSeeder.java - chỉ thêm nếu chưa tồn tại)
--     Đăng nhập:  admin@laptopspace.com  /  123456
-- ---------------------------------------------------------------------
IF NOT EXISTS (SELECT 1 FROM dbo.users WHERE email = N'admin@laptopspace.com')
BEGIN
    INSERT INTO dbo.users (username, password, full_name, email, phone, address, role, enabled)
    VALUES (
        N'admin@laptopspace.com',
        N'$2a$10$whlbn3yD/glbHcU7ZzDh4OIZoLgwASFrP1t3tFyESqNbhYq42WOoW',
        N'Quản trị viên Hệ thống',
        N'admin@laptopspace.com',
        N'0987654321',
        N'Hà Nội',
        N'ROLE_ADMIN',
        1
    );
END;