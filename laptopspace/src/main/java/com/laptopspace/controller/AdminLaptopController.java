package com.laptopspace.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.laptopspace.entity.Laptop;
import com.laptopspace.entity.LaptopImage;
import com.laptopspace.service.CategoryService;
import com.laptopspace.service.LaptopService;

@Controller
@RequestMapping("/admin/laptops")
public class AdminLaptopController {

    @Autowired
    private LaptopService laptopService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listLaptops(Model model) {
        model.addAttribute("laptops", laptopService.getAllLaptops());
        return "admin/laptop/list"; 
    }

    // ==========================================
    // 1. Luồng GET: Mở form thêm mới Laptop
    // ==========================================
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("laptop", new Laptop());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/laptop/add";
    }

    // ==========================================
    // 2. Luồng POST: Hứng dữ liệu, Lưu Nhiều Ảnh, Lưu DB
    // ==========================================
    @PostMapping("/save")
    public String saveLaptop(@ModelAttribute("laptop") Laptop laptop, 
                             @RequestParam("mainImage") MultipartFile mainImage,
                             @RequestParam("extraImages") MultipartFile[] extraImages) throws IOException {
        
        String uploadDir = "uploads/laptops/";
        java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
        
        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }
        
        // 1. Lưu ảnh chính
        if (!mainImage.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + mainImage.getOriginalFilename();
            java.nio.file.Path filePath = uploadPath.resolve(fileName);
            java.nio.file.Files.copy(mainImage.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            laptop.setImageUrl("/uploads/laptops/" + fileName);
        }

        // 2. Lưu các ảnh phụ
        if (extraImages != null && extraImages.length > 0) {
            for (MultipartFile file : extraImages) {
                if (!file.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    java.nio.file.Path filePath = uploadPath.resolve(fileName);
                    java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    
                    LaptopImage img = new LaptopImage();
                    img.setImageUrl("/uploads/laptops/" + fileName);
                    img.setLaptop(laptop);
                    laptop.getImages().add(img);
                }
            }
        }

        laptopService.saveLaptop(laptop);
        return "redirect:/admin/laptops";
    }
    
    // ==========================================
    // 3. Luồng SỬA (GET)
    // ==========================================
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Laptop laptop = laptopService.getLaptopById(id);
        if (laptop != null) {
            model.addAttribute("laptop", laptop);
            model.addAttribute("categories", categoryService.getAllCategories()); 
            return "admin/laptop/edit"; 
        }
        return "redirect:/admin/laptops";
    }

    // Luồng SỬA (POST)
    @PostMapping("/edit/{id}")
    public String updateLaptop(@PathVariable("id") Long id, 
                               @ModelAttribute("laptop") Laptop laptop,
                               @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
                               @RequestParam(value = "extraImages", required = false) MultipartFile[] extraImages) throws IOException {
        
        // 1. Lấy laptop cũ từ Database ra để bảo lưu ảnh và trạng thái
        Laptop existingLaptop = laptopService.getLaptopById(id);
        if (existingLaptop != null) {
            laptop.setImageUrl(existingLaptop.getImageUrl());
            laptop.setImages(existingLaptop.getImages()); 
            
            // QUAN TRỌNG: Bảo lưu cờ active để không bị vô tình ẩn máy khi edit
            laptop.setActive(existingLaptop.isActive());
        }

        String uploadDir = "uploads/laptops/";
        java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }

        // 2. Nếu Admin có chọn file ảnh chính mới -> Xử lý lưu đè
        if (mainImage != null && !mainImage.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + mainImage.getOriginalFilename();
            java.nio.file.Path filePath = uploadPath.resolve(fileName);
            java.nio.file.Files.copy(mainImage.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            laptop.setImageUrl("/uploads/laptops/" + fileName);
        }

        // 3. Nếu Admin up thêm ảnh phụ
        if (extraImages != null && extraImages.length > 0) {
            for (MultipartFile file : extraImages) {
                if (!file.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    java.nio.file.Path filePath = uploadPath.resolve(fileName);
                    java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    
                    LaptopImage img = new LaptopImage();
                    img.setImageUrl("/uploads/laptops/" + fileName);
                    img.setLaptop(laptop);
                    laptop.getImages().add(img);
                }
            }
        }

        laptop.setId(id);
        laptopService.saveLaptop(laptop);
        return "redirect:/admin/laptops";
    }

    // ==========================================
    // 4. Luồng XÓA MỀM (Tránh lỗi Foreign Key)
    // ==========================================
    @GetMapping("/delete/{id}")
    public String deleteLaptop(@PathVariable("id") Long id) {
        Laptop laptop = laptopService.getLaptopById(id);
        if (laptop != null) {
            // Đổi trạng thái thành false thay vì xóa cứng khỏi DB
            laptop.setActive(false);
            laptopService.saveLaptop(laptop);
        }
        return "redirect:/admin/laptops";
    }
}