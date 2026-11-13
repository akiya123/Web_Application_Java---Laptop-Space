package com.laptopspace.service;

import com.laptopspace.entity.User;
import com.laptopspace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Truyền identifier vào cả 2 vị trí email và phone để JPA quét cả 2 cột
        User user = userRepository.findByEmailOrPhone(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với Email hoặc SĐT: " + identifier));

        // Dùng constructor 7 tham số để Spring Security check trạng thái khóa
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(), // <--- Bắt buộc phải có dòng này (check từ DB)
                true,             // accountNonExpired (Tài khoản chưa hết hạn)
                true,             // credentialsNonExpired (Mật khẩu chưa hết hạn)
                true,             // accountNonLocked (Tài khoản không bị khóa)
                Collections.singleton(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}