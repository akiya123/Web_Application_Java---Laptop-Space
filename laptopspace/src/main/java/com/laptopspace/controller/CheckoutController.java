package com.laptopspace.controller;

import com.laptopspace.entity.CartItem;
import com.laptopspace.entity.Laptop;
import com.laptopspace.entity.Order;
import com.laptopspace.entity.OrderDetail;
import com.laptopspace.entity.User;
import com.laptopspace.repository.LaptopRepository;
import com.laptopspace.repository.OrderRepository;
import com.laptopspace.repository.UserRepository;
import com.laptopspace.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired private CartService cartService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LaptopRepository laptopRepository;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, Principal principal) {
        List<CartItem> cartItems = cartService.getCartItems(principal);
        if (cartItems.isEmpty()) return "redirect:/";

        Order order = new Order();
        order.setPaymentMethod("COD"); 
        
        // Chỉ tự động điền nếu đã đăng nhập
        if (principal != null) {
            User user = userRepository.findByUsernameOrEmailOrPhone(principal.getName());
            if (user != null) {
                order.setCustomerName(user.getFullName());
                order.setCustomerPhone(user.getPhone());
                order.setShippingAddress(user.getAddress());
            }
        }

        double totalAmount = cartItems.stream()
            .mapToDouble(item -> (item.getLaptop().getDiscountPrice() != null ? 
                                  item.getLaptop().getDiscountPrice() : item.getLaptop().getPrice()) * item.getQuantity())
            .sum();

        model.addAttribute("order", order);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        
        return "checkout"; 
    }

    @PostMapping("/checkout")
    public String processOrder(@ModelAttribute("order") Order order, Principal principal) {
        List<CartItem> cartItems = cartService.getCartItems(principal);
        if (cartItems.isEmpty()) return "redirect:/";

        order.setOrderDate(LocalDateTime.now());
        order.setStatus("CHỜ XÁC NHẬN");
        order.setPaymentStatus("CHƯA THANH TOÁN");
        
        // Gắn User nếu đã đăng nhập (Khách vãng lai thì user = null, vẫn lưu bình thường)
        if (principal != null) {
            order.setUser(userRepository.findByUsernameOrEmailOrPhone(principal.getName()));
        }

        double totalAmount = 0;
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setLaptop(item.getLaptop());
            detail.setQuantity(item.getQuantity());
            
            double finalPrice = (item.getLaptop().getDiscountPrice() != null) ? 
                                item.getLaptop().getDiscountPrice() : item.getLaptop().getPrice();
            detail.setPrice(finalPrice);
            totalAmount += (finalPrice * item.getQuantity());
            
            order.getOrderDetails().add(detail);

            // Trừ tồn kho
            Laptop laptop = item.getLaptop();
            laptop.setStockQuantity(Math.max(laptop.getStockQuantity() - item.getQuantity(), 0));
            laptopRepository.save(laptop);
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
        cartService.clearCart(principal);

        if ("ONLINE".equals(order.getPaymentMethod())) {
            return "redirect:/payment/qr/" + order.getId();
        }
        return "redirect:/payment/done";
    }
}