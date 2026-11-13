//package com.laptopspace.controller;
//
//import com.laptopspace.entity.Laptop;
//import com.laptopspace.service.LaptopService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@Controller
//public class ProductController {
//
//    @Autowired
//    private LaptopService laptopService;
//
//    // Đường dẫn có chứa ID động, ví dụ: /laptop/1, /laptop/2
//    @GetMapping("/laptop/{id}")
//    public String showProductDetail(@PathVariable("id") Long id, Model model) {
//        // Lấy thông tin laptop từ Database dựa vào ID
//        Laptop laptop = laptopService.getLaptopById(id);
//        
//        // Nếu không tìm thấy máy (hoặc ID bịa), đẩy về trang chủ
//        if (laptop == null) {
//            return "redirect:/";
//        }
//        
//        // Đẩy dữ liệu ra giao diện với tên biến là "laptop"
//        model.addAttribute("laptop", laptop);
//        return "detail"; 
//    }
//}