package com.laptopspace.controller;

import com.laptopspace.entity.Laptop;
import com.laptopspace.entity.Review;
import com.laptopspace.entity.User;
import com.laptopspace.repository.LaptopRepository;
import com.laptopspace.repository.OrderRepository; // Nhúng thêm thằng này
import com.laptopspace.repository.ReviewRepository;
import com.laptopspace.repository.UserRepository; // Và thằng này
import com.laptopspace.service.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
public class FrontendLaptopController {

    @Autowired private LaptopService laptopService;
    @Autowired private LaptopRepository laptopRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/laptop/{id}")
    public String showLaptopDetail(@PathVariable("id") Long id, Model model, Principal principal) {
        Laptop laptop = laptopService.getLaptopById(id);
        if (laptop == null) return "redirect:/";

        model.addAttribute("laptop", laptop);

        // Load các phiên bản cấu hình khác
        if (laptop.getModelGroup() != null && !laptop.getModelGroup().trim().isEmpty()) {
            List<Laptop> variants = laptopRepository.findByModelGroup(laptop.getModelGroup());
            model.addAttribute("variants", variants);
        }

        // Kéo danh sách Review
        List<Review> reviews = reviewRepository.findByLaptopIdOrderByCreatedAtDesc(id);
        model.addAttribute("reviews", reviews);

        // --- BẮT ĐẦU THÊM LOGIC MỚI Ở ĐÂY ---

        // 1. Tính số sao trung bình
        double averageRating = 0.0;
        if (!reviews.isEmpty()) {
            double sum = 0;
            for (Review r : reviews) sum += r.getRating();
            averageRating = Math.round((sum / reviews.size()) * 10.0) / 10.0; // Làm tròn 1 chữ số thập phân
        }
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("totalReviews", reviews.size());

        // 2. Check quyền được đánh giá (Phải đăng nhập + Đã mua hàng)
        boolean canReview = false;
        if (principal != null) {
            User user = userRepository.findByUsernameOrEmailOrPhone(principal.getName());
            if (user != null) {
                long count = orderRepository.countPurchasedLaptop(user.getId(), id);
                if (count > 0) canReview = true;
            }
        }
        model.addAttribute("canReview", canReview);

        return "detail"; 
    }
}