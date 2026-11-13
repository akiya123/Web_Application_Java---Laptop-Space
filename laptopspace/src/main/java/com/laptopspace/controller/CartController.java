package com.laptopspace.controller;

import com.laptopspace.entity.CartItem;
import com.laptopspace.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String viewCart(Principal principal, Model model) {
        List<CartItem> cartItems = cartService.getCartItems(principal);
        double totalAmount = cartItems.stream()
            .mapToDouble(item -> (item.getLaptop().getDiscountPrice() != null ? 
                                  item.getLaptop().getDiscountPrice() : item.getLaptop().getPrice()) * item.getQuantity())
            .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "cart"; 
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable("id") Long laptopId, Principal principal, HttpServletRequest request) {
        cartService.addToCart(principal, laptopId, 1);
        
        // MẸO: Lấy link của trang hiện tại để đá về lại đúng chỗ đó (không bị văng ra /cart nữa)
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/cart");
    }

    // Hàm cho nút "Mua ngay"
    @GetMapping("/buynow/{id}")
    public String buyNow(@PathVariable("id") Long laptopId, Principal principal) {
        cartService.addToCart(principal, laptopId, 1);
        return "redirect:/checkout"; // Mua ngay thì phi thẳng ra thanh toán
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity, Principal principal) {
        cartService.updateQuantity(principal, itemId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable("itemId") Long itemId, Principal principal) {
        cartService.removeCartItem(principal, itemId);
        return "redirect:/cart";
    }
}