package com.laptopspace.dto;

import com.laptopspace.entity.Laptop;

public class CartItem {
    
    // Lưu nguyên toàn bộ thông tin chiếc laptop
    private Laptop laptop;
    
    // Số lượng khách muốn mua
    private int quantity;

    // Khởi tạo
    public CartItem() {
    }

    public CartItem(Laptop laptop, int quantity) {
        this.laptop = laptop;
        this.quantity = quantity;
    }

    // Getters và Setters
    public Laptop getLaptop() {
        return laptop;
    }

    public void setLaptop(Laptop laptop) {
        this.laptop = laptop;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Hàm tính tổng tiền của sản phẩm này (Số lượng * Giá)
    public double getTotalPrice() {
        // Ưu tiên lấy giá khuyến mãi, nếu không có thì lấy giá gốc
        double priceToUse = (laptop.getDiscountPrice() != null) ? laptop.getDiscountPrice() : laptop.getPrice();
        return priceToUse * quantity;
    }
}