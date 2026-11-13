package com.laptopspace.controller;

import com.laptopspace.entity.Laptop;
import com.laptopspace.service.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private LaptopService laptopService;

    @GetMapping("/")
    public String home(Model model, 
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "categoryId", required = false) Long categoryId) {
        
        List<Laptop> laptops;
        String title = "Khuyến Mãi Hot"; // Tiêu đề mặc định

        // Logic 1: Nếu người dùng có gõ tìm kiếm
        if (keyword != null && !keyword.trim().isEmpty()) {
            laptops = laptopService.searchLaptops(keyword);
            title = "Kết quả tìm kiếm cho: '" + keyword + "'";
        } 
        // Logic 2: Nếu người dùng click vào nút Danh mục
        else if (categoryId != null) {
            laptops = laptopService.getLaptopsByCategory(categoryId);
            title = "Sản phẩm theo danh mục";
        } 
        // Logic 3: Mặc định hiển thị tất cả (hoặc có thể viết hàm lấy top sản phẩm mới)
        else {
            laptops = laptopService.getAllLaptops();
        }

        model.addAttribute("laptops", laptops);
        model.addAttribute("sectionTitle", title); // Truyền tiêu đề ra giao diện
        return "index";
    }
}