package com.laptopspace.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories") // Tên bảng trong MySQL
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng (Auto Increment)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Quan hệ 1-N: 1 Category có nhiều Laptops
    // mappedBy = "category" trỏ tới tên biến category trong class Laptop
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Laptop> laptops;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Laptop> getLaptops() {
		return laptops;
	}

	public void setLaptops(List<Laptop> laptops) {
		this.laptops = laptops;
	}

    // ==========================================
    // YÊU CẦU: TẠO GETTER VÀ SETTER
    // ==========================================
    // Để tiết kiệm thời gian và code không bị dài dòng, bạn hãy để STS tự tạo bằng cách:
    // 1. Click chuột phải vào màn hình code (hoặc bấm Alt + Shift + S)
    // 2. Chọn "Source" -> "Generate Getters and Setters..."
    // 3. Tick chọn tất cả các biến (id, name, description, laptops) -> Bấm Generate
    
}