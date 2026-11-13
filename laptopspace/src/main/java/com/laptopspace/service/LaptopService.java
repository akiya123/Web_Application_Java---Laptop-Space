package com.laptopspace.service;

import com.laptopspace.entity.Laptop;
import com.laptopspace.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LaptopService {

    @Autowired
    private LaptopRepository laptopRepository;

    // Lấy danh sách
 // Trong LaptopService.java
    public List<Laptop> getAllLaptops() {
        // Chỉ lấy máy đang active VÀ còn hàng
        return laptopRepository.findByActiveTrueAndStockQuantityGreaterThan(0);
    }

    // Lấy 1 laptop theo ID
    public Laptop getLaptopById(Long id) {
        return laptopRepository.findById(id).orElse(null);
    }

    // Thêm mới hoặc Cập nhật
    public void saveLaptop(Laptop laptop) {
        laptopRepository.save(laptop);
    }

    // Xóa
    public void deleteLaptop(Long id) {
        laptopRepository.deleteById(id);
    }
    
    public List<Laptop> searchLaptops(String keyword) {
        return laptopRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Laptop> getLaptopsByCategory(Long categoryId) {
        return laptopRepository.findByCategoryId(categoryId);
    }
}