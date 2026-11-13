package com.laptopspace.controller;

import com.laptopspace.entity.Laptop;
import com.laptopspace.entity.Review;
import com.laptopspace.entity.User;
import com.laptopspace.repository.LaptopRepository;
import com.laptopspace.repository.ReviewRepository;
import com.laptopspace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LaptopRepository laptopRepository;

    @PostMapping("/review/add")
    public String addReview(@RequestParam("laptopId") Long laptopId,
                            @RequestParam("rating") int rating,
                            @RequestParam("comment") String comment,
                            Principal principal) {
        
        // Bắt buộc đăng nhập
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsernameOrEmailOrPhone(principal.getName());
        Laptop laptop = laptopRepository.findById(laptopId).orElse(null);

     // ... code tìm user và laptop
        if (user != null && laptop != null) {
            // CHẶN: Chỉ lưu khi User chưa từng đánh giá con Laptop này
            if (!reviewRepository.existsByUserIdAndLaptopId(user.getId(), laptopId)) {
                Review review = new Review();
                review.setUser(user);
                review.setLaptop(laptop);
                review.setRating(rating);
                review.setComment(comment);
                review.setCreatedAt(LocalDateTime.now());
                reviewRepository.save(review);
            }
        }
        return "redirect:/laptop/" + laptopId; // Đánh giá xong là hệ thống tự đá về trang Chi tiết SP luôn!
    }
}