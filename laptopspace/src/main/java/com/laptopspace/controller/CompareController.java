package com.laptopspace.controller;

import com.laptopspace.entity.Laptop;
import com.laptopspace.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CompareController {

    @Autowired
    private LaptopRepository laptopRepository;

    @GetMapping("/compare")
    public String compareLaptops(
            @RequestParam(value = "id1", required = false) Long id1,
            @RequestParam(value = "id2", required = false) Long id2,
            @RequestParam(value = "id3", required = false) Long id3,
            Model model) {

        // Lấy máy 1 (Nếu có ID)
        if (id1 != null) {
            Laptop laptop1 = laptopRepository.findById(id1).orElse(null);
            model.addAttribute("laptop1", laptop1);
        }
        
        // Lấy máy 2
        if (id2 != null) {
            Laptop laptop2 = laptopRepository.findById(id2).orElse(null);
            model.addAttribute("laptop2", laptop2);
        }
        
        // Lấy máy 3
        if (id3 != null) {
            Laptop laptop3 = laptopRepository.findById(id3).orElse(null);
            model.addAttribute("laptop3", laptop3);
        }

        return "compare"; // Trỏ ra file HTML
    }
}