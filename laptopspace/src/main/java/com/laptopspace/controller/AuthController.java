package com.laptopspace.controller;

import com.laptopspace.entity.User;
import com.laptopspace.repository.UserRepository;
import com.laptopspace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // 1. Mở trang Đăng nhập
    @GetMapping("/login")
    public String login() {
        return "login"; // Trỏ tới file templates/login.html
    }

    // 2. Mở trang Đăng ký
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Trỏ tới file templates/register.html
    }

    // 3. Xử lý form Đăng ký
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, 
                               @org.springframework.web.bind.annotation.RequestParam("confirmPassword") String confirmPassword, 
                               Model model) {
        
        // 1. Kiểm tra 2 mật khẩu có khớp nhau không
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không trùng khớp!");
            return "register";
        }

        // 2. Kiểm tra xem email đã tồn tại chưa
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email này đã được đăng ký tài khoản!");
            return "register";
        }

        // Vì ta dùng Email làm tài khoản đăng nhập, ta gán luôn Username = Email để không bị lỗi trống dữ liệu DB
        user.setUsername(user.getEmail());

        // 3. Lưu tài khoản
        userService.registerUser(user);
        
        return "redirect:/login?registered=true";
    }
}