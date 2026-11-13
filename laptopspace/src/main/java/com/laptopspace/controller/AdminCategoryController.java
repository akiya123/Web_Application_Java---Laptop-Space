package com.laptopspace.controller;

import com.laptopspace.entity.Category;
import com.laptopspace.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    // Hiển thị danh sách và form thêm mới
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("newCategory", new Category());
        return "admin/category/list";
    }

    // Lưu danh mục mới
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("newCategory") Category category) {
        categoryRepository.save(category);
        return "redirect:/admin/categories";
    }

    // Xóa danh mục
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categories";
    }
}