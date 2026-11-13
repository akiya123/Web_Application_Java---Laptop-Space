package com.laptopspace.config;

import com.laptopspace.entity.User;
import com.laptopspace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Tìm xem tài khoản admin đã tồn tại trong Database chưa
        User admin = userRepository.findByEmailOrPhone("admin@laptopspace.com", "admin@laptopspace.com").orElse(null);

        if (admin != null) {
            // NẾU ĐÃ CÓ: Ép buộc mở khóa tài khoản (phòng trường hợp DB đang lưu false)
            if (!admin.isEnabled()) {
                admin.setEnabled(true);
                userRepository.save(admin);
                System.out.println("====== ĐÃ MỞ KHÓA THÀNH CÔNG CHO TÀI KHOẢN ADMIN CŨ ======");
            }
        } else {
            // NẾU CHƯA CÓ: Tạo mới tinh
            User newAdmin = new User();
            newAdmin.setUsername("admin@laptopspace.com");
            newAdmin.setPassword("123456"); 
            //newAdmin.setPassword(passwordEncoder.encode("123456")); - hàm băm
            newAdmin.setFullName("Quản trị viên Hệ thống");
            newAdmin.setEmail("admin@laptopspace.com");
            newAdmin.setPhone("0987654321");
            newAdmin.setAddress("Hà Nội");
            newAdmin.setRole("ROLE_ADMIN");
            newAdmin.setEnabled(true); // Mở khóa sẵn

            userRepository.save(newAdmin);
            System.out.println("====== TẠO THÀNH CÔNG TÀI KHOẢN ADMIN MẶC ĐỊNH ======");
        }
    }
}