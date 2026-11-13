package com.laptopspace.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "laptops")
public class Laptop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private Double price;
    
    private Double discountPrice;

    @Column(nullable = false)
    private Integer stockQuantity;
    
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    // --- CÁC THÔNG SỐ KỸ THUẬT ---
    
    @Column(columnDefinition = "TEXT")
    private String cpu;

    @Column(columnDefinition = "TEXT")
    private String ram;

    @Column(columnDefinition = "TEXT")
    private String storage; 

    @Column(columnDefinition = "TEXT")
    private String screen; 
    
    @Column(columnDefinition = "TEXT")
    private String vga; 
    
    @Column(columnDefinition = "TEXT")
    private String operatingSystem; 

    @Column(columnDefinition = "TEXT")
    private String webcam;

    @Column(columnDefinition = "TEXT")
    private String weight; 

    @Column(columnDefinition = "TEXT")
    private String battery; 

    @Column(columnDefinition = "TEXT")
    private String ports; 

    // --- MÔ TẢ CHI TIẾT ---
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String keyFeatures;

    private String imageUrl;

    // Quan hệ N-1: Nhiều Laptop thuộc về 1 Category
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false) 
    private Category category;

    // QUAN HỆ 1-N VỚI THƯ VIỆN ẢNH PHỤ
    @OneToMany(mappedBy = "laptop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LaptopImage> images = new ArrayList<>();
    
 // MÃ NHÓM SẢN PHẨM (Để gom cấu hình giống LaptopAZ)
    @Column(length = 100)
    private String modelGroup; 
    
    // ... Nhớ Generate Getter/Setter cho modelGroup nhé!
    public String getModelGroup() { return modelGroup; }
    public void setModelGroup(String modelGroup) { this.modelGroup = modelGroup; }

    // ==========================================
    // GETTERS VÀ SETTERS
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(Double discountPrice) { this.discountPrice = discountPrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }

    public String getRam() { return ram; }
    public void setRam(String ram) { this.ram = ram; }

    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }

    public String getScreen() { return screen; }
    public void setScreen(String screen) { this.screen = screen; }

    public String getVga() { return vga; }
    public void setVga(String vga) { this.vga = vga; }

    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }

    public String getWebcam() { return webcam; }
    public void setWebcam(String webcam) { this.webcam = webcam; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getBattery() { return battery; }
    public void setBattery(String battery) { this.battery = battery; }

    public String getPorts() { return ports; }
    public void setPorts(String ports) { this.ports = ports; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getKeyFeatures() { return keyFeatures; }
    public void setKeyFeatures(String keyFeatures) { this.keyFeatures = keyFeatures; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<LaptopImage> getImages() { return images; }
    public void setImages(List<LaptopImage> images) { this.images = images; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
