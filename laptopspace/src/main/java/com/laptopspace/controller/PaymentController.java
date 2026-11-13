package com.laptopspace.controller;

import com.laptopspace.entity.Order;
import com.laptopspace.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PaymentController {

    @Autowired
    private OrderRepository orderRepository;

    // 1. Hiển thị trang Quét mã QR
    @GetMapping("/payment/qr/{orderId}")
    public String showQRPage(@PathVariable("orderId") Long orderId, Model model) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null || order.getPaymentStatus().equals("ĐÃ THANH TOÁN")) {
            return "redirect:/"; // Nếu đơn không tồn tại hoặc đã trả tiền rồi thì về trang chủ
        }
        
        model.addAttribute("order", order);
        return "qr-payment"; // Trỏ tới file qr-payment.html
    }

    // 2. Nút "Xác nhận đã chuyển khoản" (Dành cho bản Demo Đồ án)
    // Trong thực tế, hệ thống ngân hàng sẽ gọi một webhook ẩn vào hàm này. 
    // Nhưng làm đồ án thì mình dùng nút bấm mô phỏng cho nhanh.
    @GetMapping("/payment/success/{orderId}")
    public String paymentSuccess(@PathVariable("orderId") Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setPaymentStatus("ĐÃ THANH TOÁN"); // Cập nhật trạng thái
            orderRepository.save(order);
        }
        return "redirect:/payment/done"; // Chuyển sang trang Cảm ơn
    }

    @GetMapping("/payment/done")
    public String paymentDone() {
        return "payment-success"; // Giao diện báo đặt hàng thành công
    }
}