package com.laptopspace.repository;

import com.laptopspace.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Lấy list đánh giá theo ID laptop, sắp xếp thời gian giảm dần
    List<Review> findByLaptopIdOrderByCreatedAtDesc(Long laptopId);
    
 // 1. Kiểm tra xem User đã từng đánh giá Laptop này chưa (trả về true/false)
    boolean existsByUserIdAndLaptopId(Long userId, Long laptopId);
    
    // 2. Lấy ra một danh sách các ID Laptop mà User này đã đánh giá
    @Query("SELECT r.laptop.id FROM Review r WHERE r.user.id = :userId")
    List<Long> findReviewedLaptopIdsByUserId(@Param("userId") Long userId);
}