package com.laptopspace.controller;

import com.laptopspace.entity.User;
import com.laptopspace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Khai báo công cụ gửi Mail của Spring Boot
    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes ra) {
        User user = userRepository.findByEmail(email); 
        
        if (user == null) {
            ra.addFlashAttribute("error", "Không tìm thấy tài khoản nào đăng ký bằng Email này!");
            return "redirect:/forgot-password";
        }

        // 1. Tạo mật khẩu mới ngẫu nhiên 8 ký tự
        String newRandomPassword = UUID.randomUUID().toString().substring(0, 8);
        
        // 2. Lưu vào Database
        //user.setPassword(passwordEncoder.encode(newRandomPassword)); - hàm băm
        user.setPassword(newRandomPassword);
        userRepository.save(user);

        // 3. Gửi mật khẩu mới vào Email của khách
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Khôi phục mật khẩu - LaptopSpace");
            message.setText("Xin chào " + user.getFullName() + ",\n\n"
                    + "Mật khẩu của bạn đã được đặt lại thành công.\n"
                    + "Mật khẩu mới của bạn là: " + newRandomPassword + "\n\n"
                    + "Vui lòng đăng nhập và đổi lại mật khẩu ngay để đảm bảo an toàn.\n"
                    + "Trân trọng,\nĐội ngũ LaptopSpace.");
            
            mailSender.send(message);
            ra.addFlashAttribute("success", "Thành công! Mật khẩu mới đã được gửi vào hộp thư của bạn.");
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi gửi mail: Kiểm tra lại cấu hình kết nối Internet hoặc App Password!");
            return "redirect:/forgot-password";
        }
        
        return "redirect:/login";
    }
}