package com.laptopspace.controller;

import com.laptopspace.entity.Order;
import com.laptopspace.repository.LaptopRepository;
import com.laptopspace.repository.OrderRepository;
import com.laptopspace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminDashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LaptopRepository laptopRepository;

    // Map thẳng vào /admin để lúc vừa đăng nhập xong nó đập ngay vào mắt
    @GetMapping("/admin")
    public String dashboard(Model model) {
        
        List<Order> allOrders = orderRepository.findAll();

        // 1. Tính tổng doanh thu (Chỉ cộng tiền của những đơn "Hoàn thành")
        double totalRevenue = allOrders.stream()
                .filter(o -> "Hoàn thành".equalsIgnoreCase(o.getStatus()) || "HOÀN THÀNH".equalsIgnoreCase(o.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();

        // 2. Tổng số đơn hàng trong hệ thống
        long totalOrders = allOrders.size();

        // 3. Tổng số khách hàng (Trừ đi 1 tài khoản Admin chính)
        long totalUsers = userRepository.count() > 0 ? userRepository.count() - 1 : 0;

        // 4. Tổng số sản phẩm đang bán
        long totalLaptops = laptopRepository.count();

        // 5. Lấy 5 đơn hàng gần nhất để hiện ra bảng thao tác nhanh
        List<Order> recentOrders = orderRepository.findAllByOrderByOrderDateDesc();
        if (recentOrders.size() > 5) {
            recentOrders = recentOrders.subList(0, 5);
        }

        // Đẩy hết sang giao diện
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalLaptops", totalLaptops);
        model.addAttribute("recentOrders", recentOrders);

        return "admin/dashboard"; // Trỏ tới file dashboard.html
    }
}