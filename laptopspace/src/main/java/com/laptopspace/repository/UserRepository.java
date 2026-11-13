package com.laptopspace.repository;

import com.laptopspace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Đã thay thế findByEmail thành hàm này
    Optional<User> findByEmailOrPhone(String email, String phone);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.username = ?1 OR u.email = ?1 OR u.phone = ?1")
    User findByUsernameOrEmailOrPhone(String identifier);
    
    // Cứ khai báo thêm mấy hàm này phòng khi các Controller khác cần dùng
    User findByUsername(String username);
    User findByEmail(String email);
}