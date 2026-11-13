package com.laptopspace.controller;

import com.laptopspace.entity.User;
import com.laptopspace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/user/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Hiển thị trang Profile
    @GetMapping
    public String showProfile(Principal principal, Model model) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsernameOrEmailOrPhone(principal.getName());
        model.addAttribute("user", user);
        return "user/profile";
    }

    // 2. Cập nhật thông tin cá nhân
    @PostMapping("/update")
    public String updateInfo(@RequestParam("fullName") String fullName,
                             @RequestParam("phone") String phone,
                             @RequestParam("address") String address,
                             Principal principal, RedirectAttributes ra) {
        User user = userRepository.findByUsernameOrEmailOrPhone(principal.getName());
        if (user != null) {
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setAddress(address);
            userRepository.save(user);
            ra.addFlashAttribute("msg_success", "Cập nhật thông tin thành công!");
        }
        return "redirect:/user/profile";
    }

    // 3. Đổi mật khẩu
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Principal principal, RedirectAttributes ra) {
        User user = userRepository.findByUsernameOrEmailOrPhone(principal.getName());
        
        // Kiểm tra mật khẩu cũ có khớp trong DB không
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            ra.addFlashAttribute("msg_error", "Mật khẩu cũ không chính xác!");
            return "redirect:/user/profile";
        }
        
        // Kiểm tra mật khẩu mới và xác nhận
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("msg_error", "Mật khẩu xác nhận không khớp!");
            return "redirect:/user/profile";
        }
        
        // Mã hóa pass mới và lưu
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        ra.addFlashAttribute("msg_success", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại lần sau với mật khẩu mới.");
        
        return "redirect:/user/profile";
    }
}