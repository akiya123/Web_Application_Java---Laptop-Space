package com.laptopspace.service;

import com.laptopspace.entity.Cart;
import com.laptopspace.entity.CartItem;
import com.laptopspace.entity.Laptop;
import com.laptopspace.entity.User;
import com.laptopspace.repository.CartItemRepository;
import com.laptopspace.repository.CartRepository;
import com.laptopspace.repository.LaptopRepository;
import com.laptopspace.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private LaptopRepository laptopRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private HttpSession session;

    private User getUser(Principal principal) {
        if (principal == null) return null;
        return userRepository.findByUsernameOrEmailOrPhone(principal.getName());
    }

    public Cart getCartByUser(User user) {
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    // Lấy danh sách sản phẩm (Dùng chung cho cả Đã đăng nhập và Vãng lai)
    public List<CartItem> getCartItems(Principal principal) {
        if (principal != null) {
            return getCartByUser(getUser(principal)).getItems();
        } else {
            List<CartItem> items = new ArrayList<>();
            Map<Long, Integer> guestCart = (Map<Long, Integer>) session.getAttribute("guestCart");
            if (guestCart != null) {
                for (Map.Entry<Long, Integer> entry : guestCart.entrySet()) {
                    Laptop laptop = laptopRepository.findById(entry.getKey()).orElse(null);
                    if (laptop != null && laptop.isActive()) {
                        CartItem item = new CartItem();
                        item.setId(laptop.getId()); // Mượn ID laptop làm ID item tạm
                        item.setLaptop(laptop);
                        item.setQuantity(entry.getValue());
                        items.add(item);
                    }
                }
            }
            return items;
        }
    }

    // Thêm vào giỏ
    public void addToCart(Principal principal, Long laptopId, int quantity) {
        Laptop laptop = laptopRepository.findById(laptopId).orElse(null);
        if (laptop == null || !laptop.isActive() || laptop.getStockQuantity() <= 0) return;

        if (principal != null) {
            Cart cart = getCartByUser(getUser(principal));
            Optional<CartItem> existingItem = cart.getItems().stream()
                    .filter(item -> item.getLaptop().getId().equals(laptopId)).findFirst();

            if (existingItem.isPresent()) {
                CartItem item = existingItem.get();
                item.setQuantity(Math.min(item.getQuantity() + quantity, laptop.getStockQuantity()));
                cartItemRepository.save(item);
            } else {
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setLaptop(laptop);
                newItem.setQuantity(Math.min(quantity, laptop.getStockQuantity()));
                cartItemRepository.save(newItem);
            }
        } else {
            Map<Long, Integer> guestCart = (Map<Long, Integer>) session.getAttribute("guestCart");
            if (guestCart == null) guestCart = new HashMap<>();
            
            int currentQty = guestCart.getOrDefault(laptopId, 0);
            guestCart.put(laptopId, Math.min(currentQty + quantity, laptop.getStockQuantity()));
            session.setAttribute("guestCart", guestCart);
        }
    }

    // Cập nhật số lượng
    public void updateQuantity(Principal principal, Long itemId, int quantity) {
        if (principal != null) {
            CartItem item = cartItemRepository.findById(itemId).orElse(null);
            if (item != null && item.getCart().getUser().getId().equals(getUser(principal).getId())) {
                if (quantity <= 0) cartItemRepository.delete(item);
                else {
                    item.setQuantity(Math.min(quantity, item.getLaptop().getStockQuantity()));
                    cartItemRepository.save(item);
                }
            }
        } else {
            Map<Long, Integer> guestCart = (Map<Long, Integer>) session.getAttribute("guestCart");
            if (guestCart != null) {
                Laptop laptop = laptopRepository.findById(itemId).orElse(null);
                if (laptop != null) {
                    if (quantity <= 0) guestCart.remove(itemId);
                    else guestCart.put(itemId, Math.min(quantity, laptop.getStockQuantity()));
                    session.setAttribute("guestCart", guestCart);
                }
            }
        }
    }

    // Xóa sản phẩm khỏi giỏ
    public void removeCartItem(Principal principal, Long itemId) {
        if (principal != null) {
            CartItem item = cartItemRepository.findById(itemId).orElse(null);
            if (item != null && item.getCart().getUser().getId().equals(getUser(principal).getId())) {
                cartItemRepository.delete(item);
            }
        } else {
            Map<Long, Integer> guestCart = (Map<Long, Integer>) session.getAttribute("guestCart");
            if (guestCart != null) {
                guestCart.remove(itemId);
                session.setAttribute("guestCart", guestCart);
            }
        }
    }

    // Xóa trắng giỏ sau khi thanh toán
    public void clearCart(Principal principal) {
        if (principal != null) {
            cartItemRepository.deleteAll(getCartByUser(getUser(principal)).getItems());
        } else {
            session.removeAttribute("guestCart");
        }
    }
}