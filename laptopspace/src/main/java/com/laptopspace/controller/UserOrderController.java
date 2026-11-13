package com.laptopspace.controller;

import com.laptopspace.entity.Order;
import com.laptopspace.entity.User;
import com.laptopspace.repository.OrderRepository;
import com.laptopspace.repository.ReviewRepository;
import com.laptopspace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user/orders")
public class UserOrderController {
	
    @Autowired
    private ReviewRepository reviewRepository; 

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Xem danh sách lịch sử đơn hàng
    @GetMapping
    public String myOrders(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";
        
        User user = userRepository.findByUsernameOrEmailOrPhone(principal.getName());
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(user.getId());
        
        model.addAttribute("orders", orders);
        return "user/orders";
    }

    // 2. Xem chi tiết 1 đơn hàng
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable("id") Long id, Principal principal, Model model) {
        if (principal == null) return "redirect:/login";
        
        User user = userRepository.findByUsernameOrEmailOrPhone(principal.getName());
        Order order = orderRepository.findById(id).orElse(null);
        
        // 1. BẢO MẬT CHECK TRƯỚC: Đơn không tồn tại hoặc không phải của User này thì đá về trang danh sách
        if (order == null || !order.getUser().getId().equals(user.getId())) {
            return "redirect:/user/orders";
        }
        
        // 2. NẾU CHECK PASS: Kéo danh sách ID laptop mà khách đã đánh giá truyền ra HTML
        List<Long> reviewedLaptopIds = reviewRepository.findReviewedLaptopIdsByUserId(user.getId());
        model.addAttribute("reviewedLaptopIds", reviewedLaptopIds);
        
        model.addAttribute("order", order);
        
        // 3. RETURN Ở CUỐI CÙNG
        return "user/order-detail";
    }

    // 3. Xử lý Hủy đơn hàng
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable("id") Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        
        User user = userRepository.findByUsernameOrEmailOrPhone(principal.getName());
        Order order = orderRepository.findById(id).orElse(null);
        
        if (order != null && order.getUser().getId().equals(user.getId())) {
            // Logic quan trọng: Chỉ cho hủy khi đang "Chờ xác nhận"
            if ("Chờ xác nhận".equalsIgnoreCase(order.getStatus()) || "CHỜ XÁC NHẬN".equalsIgnoreCase(order.getStatus())) {
                order.setStatus("Đã hủy");
                orderRepository.save(order);
            }
        }
        return "redirect:/user/orders/" + id; 
    }
}