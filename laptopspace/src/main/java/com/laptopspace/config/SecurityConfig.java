package com.laptopspace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Khai báo công cụ mã hóa mật khẩu (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // ĐÃ BỔ SUNG THÊM: /cart, /checkout, /payment, /compare, /uploads
                .requestMatchers(
                        "/", "/laptop/**", "/images/**", "/css/**", "/js/**", 
                        "/register", "/forgot-password", "/shop",
                        "/cart/**", "/checkout/**", "/payment/**", "/compare/**", "/uploads/**"
                ).permitAll()
                
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Các đường dẫn còn lại (ví dụ: thông tin cá nhân, lịch sử đơn hàng, thêm review) mới bắt đăng nhập
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/login") // Đường dẫn trỏ tới trang UI của chúng ta
                    .loginProcessingUrl("/login") // Spring Security sẽ tự động hứng data từ form submit vào đây
                    .defaultSuccessUrl("/", true) // Đăng nhập thành công thì đá về trang chủ
                    .failureUrl("/login?error=true") // Đăng nhập sai thì load lại trang kèm tham số báo lỗi
                    .permitAll()
                )
            .logout(logout -> logout
                // Đăng xuất thành công thì đá về trang chủ
                .logoutSuccessUrl("/")
                .permitAll()
            )
            // Tắt CSRF tạm thời để dễ test form
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}