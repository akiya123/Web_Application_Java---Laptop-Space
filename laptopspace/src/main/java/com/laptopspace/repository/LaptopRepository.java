package com.laptopspace.repository;

import com.laptopspace.entity.Laptop;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, Long> {
    
    // Gắn cứng điều kiện active = true và stock > 0 vào các hàm cũ bằng @Query
    // Giúp ông không cần đi sửa lại code ở các file Controller/Service
    
    @Query("SELECT l FROM Laptop l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND l.active = true AND l.stockQuantity > 0")
    List<Laptop> findByNameContainingIgnoreCase(@Param("keyword") String keyword);
    
    @Query("SELECT l FROM Laptop l WHERE l.category.id = :categoryId AND l.active = true AND l.stockQuantity > 0")
    List<Laptop> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT l FROM Laptop l WHERE l.modelGroup = :modelGroup AND l.active = true AND l.stockQuantity > 0")
    List<Laptop> findByModelGroup(@Param("modelGroup") String modelGroup);
    
    // Hàm này mới thêm, dùng để lấy tất cả sản phẩm hợp lệ cho trang chủ
    List<Laptop> findByActiveTrueAndStockQuantityGreaterThan(int quantity);
    
    // Đã fix lỗi dư dấu ngoặc và chèn thêm 2 điều kiện lọc ở cuối cùng
    @Query("SELECT l FROM Laptop l WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR l.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR l.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR l.price <= :maxPrice) AND " +
            "(:brand IS NULL OR :brand = '' OR LOWER(l.name) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
            "(:ram IS NULL OR :ram = '' OR LOWER(l.ram) LIKE LOWER(CONCAT('%', :ram, '%'))) AND " +
            "(:storage IS NULL OR :storage = '' OR LOWER(l.storage) LIKE LOWER(CONCAT('%', :storage, '%'))) AND " +
            "l.active = true AND l.stockQuantity > 0")
     Page<Laptop> filterLaptops(@Param("keyword") String keyword,
                                @Param("categoryId") Long categoryId,
                                @Param("minPrice") Double minPrice,
                                @Param("maxPrice") Double maxPrice,
                                @Param("brand") String brand,
                                @Param("ram") String ram,
                                @Param("storage") String storage,
                                Pageable pageable);
}