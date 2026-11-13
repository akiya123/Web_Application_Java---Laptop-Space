package com.laptopspace.controller;

import com.laptopspace.entity.User;
import com.laptopspace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    // Hiển thị danh sách khách hàng
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/user/list";
    }

    // Xử lý Khóa / Mở khóa tài khoản
    @GetMapping("/toggle-lock/{id}")
    public String toggleUserLock(@PathVariable("id") Long id) {
        User user = userRepository.findById(id).orElse(null);
        
        // Không cho phép khóa tài khoản Admin chính (tránh tự hủy)
        if (user != null && !user.getEmail().equals("admin@laptopspace.com")) {
            // Đảo trạng thái: Đang true thành false (khóa) và ngược lại
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
        }
        
        return "redirect:/admin/users";
    }
    
 // Xem chi tiết thông tin Khách hàng
    @GetMapping("/{id}")
    public String viewUserDetail(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "admin/user/detail";
    }
}