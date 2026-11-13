package com.laptopspace.controller;

import com.laptopspace.entity.Order;
import com.laptopspace.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;

    // 1. Hiển thị danh sách đơn hàng
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAllByOrderByOrderDateDesc());
        return "admin/order/list";
    }

    // 2. Cập nhật trạng thái đơn hàng
    @PostMapping("/update-status/{id}")
    public String updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        Order order = orderRepository.findById(id).orElse(null);
        
        if (order != null) {
            // Chặn đứng: Đơn đã hủy hoặc đã hoàn thành thì cấm đổi trạng thái
            if ("Đã hủy".equalsIgnoreCase(order.getStatus()) || "Hoàn thành".equalsIgnoreCase(order.getStatus())) {
                return "redirect:/admin/orders?error=locked";
            }
            
            // Cập nhật trạng thái mới
            order.setStatus(status);
            orderRepository.save(order);
        }
        
        // Lưu xong thì quay lại trang danh sách đơn hàng
        return "redirect:/admin/orders";
    }
    
    // 3. Xem chi tiết một đơn hàng
    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable("id") Long id, Model model) {
        Order order = orderRepository.findById(id).orElse(null);
        
        if (order == null) {
            return "redirect:/admin/orders";
        }
        
        model.addAttribute("order", order);
        return "admin/order/detail"; 
    }
}