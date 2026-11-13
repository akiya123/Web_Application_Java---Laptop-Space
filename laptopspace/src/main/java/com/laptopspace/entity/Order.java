package com.laptopspace.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thông tin người nhận hàng (Cho phép cả khách vãng lai tự nhập)
    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 20)
    private String customerPhone;

    @Column(nullable = false, length = 255)
    private String shippingAddress;

    @Column(columnDefinition = "TEXT")
    private String note; // Ghi chú của khách (vd: Giao giờ hành chính)

    @Column(nullable = false)
    private Double totalAmount; // Tổng tiền thanh toán

    @Column(nullable = false)
    private LocalDateTime orderDate; // Ngày giờ đặt hàng

    @Column(nullable = false, length = 50)
    private String status; // Trạng thái: "CHỜ XÁC NHẬN", "ĐANG GIAO", "HOÀN THÀNH"

    // Khóa ngoại liên kết với User (nullable = true để cho phép khách vãng lai mua hàng không cần đăng nhập)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    // Một đơn hàng sẽ có nhiều Chi tiết đơn hàng (Laptop A 2 cái, Laptop B 1 cái...)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderDetail> orderDetails = new ArrayList<>();
    
 // Phương thức thanh toán: "COD" (Nhận hàng trả tiền) hoặc "ONLINE" (Chuyển khoản QR)
    @Column(length = 20)
    private String paymentMethod = "COD"; 

    // Trạng thái thanh toán: "CHƯA THANH TOÁN" hoặc "ĐÃ THANH TOÁN"
    @Column(length = 50)
    private String paymentStatus = "CHƯA THANH TOÁN";

    // ==========================================
    // GETTERS VÀ SETTERS
    // ==========================================

    public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<OrderDetail> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetail> orderDetails) { this.orderDetails = orderDetails; }
}