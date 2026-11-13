package com.laptopspace.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chi tiết này thuộc về đơn hàng nào?
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Mua chiếc Laptop nào?
    @ManyToOne
    @JoinColumn(name = "laptop_id", nullable = false)
    private Laptop laptop;

    @Column(nullable = false)
    private Integer quantity; // Số lượng mua

    @Column(nullable = false)
    private Double price; // Giá của 1 sản phẩm tại ĐÚNG THỜI ĐIỂM ĐẶT HÀNG

    // ==========================================
    // GETTERS VÀ SETTERS
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Laptop getLaptop() { return laptop; }
    public void setLaptop(Laptop laptop) { this.laptop = laptop; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}