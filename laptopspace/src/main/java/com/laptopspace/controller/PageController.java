package com.laptopspace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/khuyen-mai")
    public String khuyenMai() {
        return "khuyen-mai"; // Trỏ ra file khuyen-mai.html
    }

    @GetMapping("/tra-gop")
    public String traGop() {
        return "tra-gop"; // Trỏ ra file tra-gop.html
    }

    @GetMapping("/tin-tuc")
    public String tinTuc() {
        return "tin-tuc"; // Trỏ ra file tin-tuc.html
    }
}