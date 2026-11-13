package com.laptopspace.repository;

import com.laptopspace.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Chỉ cần kế thừa JpaRepository là bạn đã có sẵn các hàm: findAll(), findById(), save(), delete()...
}