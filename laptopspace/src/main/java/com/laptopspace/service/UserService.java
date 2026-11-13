package com.laptopspace.service;

import com.laptopspace.entity.User;
import com.laptopspace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Lưu người dùng mới (Dành cho chức năng Đăng ký)
    public void registerUser(User user) {
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Mặc định khách đăng ký sẽ có quyền USER
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }
}