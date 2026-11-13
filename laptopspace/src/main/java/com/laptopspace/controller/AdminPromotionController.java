package com.laptopspace.controller;

import com.laptopspace.entity.Laptop;
import com.laptopspace.repository.CategoryRepository;
import com.laptopspace.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/promotions")
public class AdminPromotionController {

    @Autowired
    private LaptopRepository laptopRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // 1. Hiển thị trang Quản lý Khuyến mãi
    @GetMapping
    public String showPromotionPage(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/promotion/index";
    }

    // 2. Xử lý Áp dụng giảm giá theo Danh mục
    @PostMapping("/apply-category")
    public String applyDiscountToCategory(@RequestParam("categoryId") Long categoryId, 
                                          @RequestParam("discountPercent") Double discountPercent) {
        // Kéo toàn bộ laptop thuộc danh mục này ra
        List<Laptop> laptops = laptopRepository.findByCategoryId(categoryId);
        
        for (Laptop laptop : laptops) {
            if (laptop.getPrice() != null) {
                // Tính giá mới: Giá gốc - (Giá gốc * % giảm / 100)
                double discountAmount = laptop.getPrice() * (discountPercent / 100.0);
                double newPrice = laptop.getPrice() - discountAmount;
                laptop.setDiscountPrice(newPrice);
            }
        }
        
        // Lưu hàng loạt xuống DB
        laptopRepository.saveAll(laptops);
        return "redirect:/admin/promotions?success=true";
    }

    // 3. Xử lý Hủy toàn bộ Sale của một Danh mục
    @PostMapping("/clear-category")
    public String clearDiscountCategory(@RequestParam("categoryId") Long categoryId) {
        List<Laptop> laptops = laptopRepository.findByCategoryId(categoryId);
        for (Laptop laptop : laptops) {
            laptop.setDiscountPrice(null); // Đưa về null là hết sale
        }
        laptopRepository.saveAll(laptops);
        return "redirect:/admin/promotions?cleared=true";
    }
}