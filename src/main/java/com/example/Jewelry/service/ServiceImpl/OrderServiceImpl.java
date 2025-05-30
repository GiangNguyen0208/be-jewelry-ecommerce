package com.example.Jewelry.service.ServiceImpl;

import com.example.Jewelry.dao.OrderDAO;
import com.example.Jewelry.dto.CartItemDTO;
import com.example.Jewelry.dto.request.OrderRequestDTO;
import com.example.Jewelry.dto.response.CommonAPIResForOrder;
import com.example.Jewelry.entity.*;
import com.example.Jewelry.entity.Order.OrderStatus;
import com.example.Jewelry.dao.*;
import com.example.Jewelry.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderRepo;
    private final ProductDAO productRepo;
    private final UserDAO userRepo;
    private final DeliveryAddressDAO deliveryAddressRepo;
    private final EmailServiceImpl otpService;

    @Override
    @Transactional
    public CommonAPIResForOrder createOrder(OrderRequestDTO dto) {
        if (dto.getCartItems() == null || dto.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng rỗng");
        }

        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        DeliveryAddress address = deliveryAddressRepo.findById(dto.getDeliveryAddressId())
                .orElseThrow(() -> new IllegalArgumentException("Địa chỉ giao hàng không tồn tại"));

        Order order = new Order();
        order.setUserEmail(user.getEmailId());
        order.setDeliveryAddress(address);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();
        double total = 0;

        for (CartItemDTO itemDTO : dto.getCartItems()) {
            Product product = productRepo.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setPrice(product.getPrice());
            item.setQuantity(itemDTO.getQuantity());

            total += product.getPrice() * itemDTO.getQuantity();
            items.add(item);
        }

        double discount = dto.getDiscount() != null ? dto.getDiscount() : 0.0;
        order.setItems(items);
        order.setTotalPrice(Math.max(0, total - discount));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING);
        payment.setPaymentMethod(dto.getPaymentMethod() ? Payment.PaymentMethod.COD : Payment.PaymentMethod.QR_Payment);
        payment.setOtpCode(otpService.generateOtp(user.getEmailId()));
        payment.setOtpGeneratedAt(LocalDateTime.now());

        order.setPayment(payment);

        orderRepo.save(order);

        return CommonAPIResForOrder.success("Tạo đơn hàng thành công, vui lòng kiểm tra email để xác thực thanh toán bằng OTP.", order.getId());
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    @Override
    public Order getSingleOrder(int orderID) {
        return orderRepo.findById(orderID).orElse(null);
    }


    public boolean updateOrderStatus(Order singleOrder, OrderStatus newStatus) {
        OrderStatus oldStatus = singleOrder.getStatus();
        // you can not update a cancelled or confirmed order
        if (oldStatus == OrderStatus.CANCELLED || oldStatus == OrderStatus.CONFIRM)
            return false;
        // you can not update to the same value
        if (oldStatus == newStatus)
            return false;
        // you can not update the unpaid order?
        singleOrder.setStatus(newStatus);
        orderRepo.save(singleOrder);
        return true;
    }




}
