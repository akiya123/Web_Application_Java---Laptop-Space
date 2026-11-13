package com.laptopspace.controller;

import com.laptopspace.entity.Laptop;
import com.laptopspace.repository.CategoryRepository;
import com.laptopspace.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ShopController {

    @Autowired
    private LaptopRepository laptopRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/shop")
    public String shop(
            @RequestParam(value = "keyword", required = false) String keyword, // Đã fix lỗi cú pháp ở đây
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "priceRange", required = false) String priceRange,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "ram", required = false) String ram,
            @RequestParam(value = "storage", required = false) String storage,
            @RequestParam(value = "sort", defaultValue = "newest") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page, 
            @RequestParam(value = "size", defaultValue = "9") int size, 
            Model model) {

        // 1. Tách khoảng giá
        Double minPrice = null; Double maxPrice = null;
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] parts = priceRange.split("-");
            minPrice = Double.parseDouble(parts[0]);
            maxPrice = Double.parseDouble(parts[1]);
        }

        // 2. Xử lý Sắp xếp (Sort)
        Sort sortObj = Sort.by("id").descending(); // Mặc định: Mới nhất
        if ("price_asc".equals(sort)) {
            sortObj = Sort.by("price").ascending();
        } else if ("price_desc".equals(sort)) {
            sortObj = Sort.by("price").descending();
        }

        Pageable pageable = PageRequest.of(page, size, sortObj);

        // 3. Gọi hàm lọc từ DB (Truyền biến keyword vào hàm filterLaptops)
        Page<Laptop> laptopPage = laptopRepository.filterLaptops(keyword, categoryId, minPrice, maxPrice, brand, ram, storage, pageable);

        // 4. Trả dữ liệu ra View
        model.addAttribute("laptops", laptopPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", laptopPage.getTotalPages());
        model.addAttribute("categories", categoryRepository.findAll());

        // Giữ lại các biến để hiển thị tick chọn trên màn hình
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedPriceRange", priceRange);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedRam", ram);
        model.addAttribute("selectedStorage", storage);
        model.addAttribute("selectedSort", sort);

        return "shop";
    }
}