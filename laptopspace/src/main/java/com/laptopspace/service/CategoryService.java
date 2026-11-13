package com.laptopspace.service;

import com.laptopspace.entity.Category;
import com.laptopspace.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Hàm lấy toàn bộ danh mục từ MySQL
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }
    
 // ... các hàm cũ giữ nguyên ...

    // Tìm 1 danh mục dựa vào ID
    public Category getCategoryById(Long id) {
        // findById trả về Optional, dùng orElse(null) để tránh lỗi nếu không tìm thấy
        return categoryRepository.findById(id).orElse(null);
    }

    // Xóa 1 danh mục dựa vào ID
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}