package com.laptopspace.config;

import com.laptopspace.entity.CartItem;
import com.laptopspace.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CartService cartService;

    // Biến này sẽ tự động được inject vào mọi thẻ th:text="${cartCount}" trên Header
    @ModelAttribute("cartCount")
    public int getCartCount(Principal principal) {
        // Lấy danh sách item từ CartService (Thằng này giờ đã thông minh, tự biết check Session hay DB)
        List<CartItem> items = cartService.getCartItems(principal);
        
        // Trả về 0 nếu giỏ trống, ngược lại thì cộng dồn số lượng
        if (items == null || items.isEmpty()) {
            return 0;
        }
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }
}