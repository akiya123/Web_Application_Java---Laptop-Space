package com.laptopspace.repository;

import com.laptopspace.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Lấy tất cả đơn hàng, sắp xếp ngày tạo giảm dần (mới nhất lên trên)
    List<Order> findAllByOrderByOrderDateDesc();
 // Lấy toàn bộ đơn của user, sắp xếp mới nhất lên đầu
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    
    @Query("SELECT COUNT(o) FROM Order o JOIN o.orderDetails od WHERE o.user.id = :userId AND od.laptop.id = :laptopId AND UPPER(o.status) = 'HOÀN THÀNH'")
    long countPurchasedLaptop(@Param("userId") Long userId, @Param("laptopId") Long laptopId);
}