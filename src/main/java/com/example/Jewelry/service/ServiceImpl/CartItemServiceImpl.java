package com.example.Jewelry.service.ServiceImpl;

import com.example.Jewelry.dao.CartItemDAO;
import com.example.Jewelry.dao.ProductDAO;
import com.example.Jewelry.dao.UserDAO;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.entity.CartItem;
import com.example.Jewelry.entity.Product;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {

    @Autowired
    private CartItemDAO cartItemDAO;

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    public CartItem addToCart(int userId, int productId, int quantity) {
        Optional<User> userOpt = userDAO.findById(userId);
        Optional<Product> productOpt = productDAO.findById(productId);

        if (userOpt.isEmpty() || productOpt.isEmpty()) {
            throw new RuntimeException("Người dùng hoặc sản phẩm không tồn tại");
        }

        User user = userOpt.get();
        Product product = productOpt.get();

        Optional<CartItem> existing = cartItemDAO.findByUserAndProduct(user, product);
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            return cartItemDAO.save(item);
        }

        CartItem newItem = new CartItem();
        newItem.setUser(user);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        return cartItemDAO.save(newItem);
    }

    @Override
    public List<CartItem> getCartItems(int userId) {
        User user = userDAO.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return cartItemDAO.findByUserAndDeletedFalse(user);
    }

    @Override
    public CommonApiResponse removeFromCart(int userId, int productId) {
        CommonApiResponse response = new CommonApiResponse();
        Optional<User> userOpt = userDAO.findById(userId);
        Optional<Product> productOpt = productDAO.findById(productId);

        if (userOpt.isPresent() && productOpt.isPresent()) {
            Optional<CartItem> cartOpt = cartItemDAO.findByUserAndProductAndDeletedFalse(userOpt.get(), productOpt.get());

            if (cartOpt.isPresent()) {
                CartItem cart = cartOpt.get();
                cart.setDeleted(true);
                cartItemDAO.save(cart);
                response.setSuccess(true);
                response.setResponseMessage("Đã xóa khỏi giỏ hàng");
            } else {
                response.setSuccess(false);
                response.setResponseMessage("Không tìm thấy sản phẩm trong giỏ hàng");
            }
        }

        return response;
    }

    @Override
    public CommonApiResponse clearCart(int userId) {
        List<CartItem> cartList = cartItemDAO.findByUserAndDeletedFalse(userDAO.findById(userId).orElseThrow());
        for (CartItem cart : cartList) {
            cart.setDeleted(true);
        }
        cartItemDAO.saveAll(cartList);

        CommonApiResponse response = new CommonApiResponse();
        response.setSuccess(true);
        response.setResponseMessage("Đã xóa toàn bộ giỏ hàng");
        return response;
    }
}
