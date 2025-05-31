package com.example.Jewelry.controller;

import com.example.Jewelry.dao.UserDAO;
import com.example.Jewelry.dto.DeliveryAddressDTO;
import com.example.Jewelry.dto.OrderDTO;
import com.example.Jewelry.dto.OrderItemDTO;
import com.example.Jewelry.dto.request.OrderRequestDTO;
import com.example.Jewelry.dto.response.CommonAPIResForOrder;
import com.example.Jewelry.dto.response.OrderDTOResponse;
import com.example.Jewelry.entity.Order;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.exception.ResourceNotFoundException;
import com.example.Jewelry.service.ServiceImpl.OrderServiceImpl;
import com.example.Jewelry.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderServiceImpl orderService;
    private final UserDAO userDao;
//  this is
//    {
//        "userId": 3,
//            "deliveryAddressId": 1,
//            "cartItems": [
//        {
//            "productId": 2,
//                "quantity": 2
//        },
//        {
//            "productId": 1,
//                "quantity": 1
//        }
//  ],
//        "totalAmount": 1000.0,
//            "discount": 50.0,
//            "paymentMethod": true
//    }


    @PostMapping("/create")
    public ResponseEntity<CommonAPIResForOrder> createOrder(@RequestBody OrderRequestDTO dto) {
        CommonAPIResForOrder response = orderService.createOrder(dto);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/fetch/full")
    public ResponseEntity<OrderDTOResponse> getAllOrders() {
        List<Order> list = orderService.getAllOrders();
        List<OrderDTO> uh = list.stream().map((o) -> {
            OrderDTO result = new OrderDTO();
            User user = userDao.findByEmailId(o.getUserEmail());
            BeanUtils.copyProperties(o, result);
            result.setDeliveryAddress(DeliveryAddressDTO.convertDeliveryAddress(o.getDeliveryAddress()));
            result.setItems(OrderItemDTO.convertItemList(o.getItems()));
            result.setOwnerName(user.getLastName() + " " + user.getFirstName());
            return result;
        }).toList();
        OrderDTOResponse response = new OrderDTOResponse();
        response.setSuccess(true);
        response.setResponseMessage("Yipee");
        response.setOrderDTOList(uh);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }


    @GetMapping("/fetch/all")
    public ResponseEntity<OrderDTOResponse> getAllOrderDTOs() {
        List<Order> list = orderService.getAllOrders();
        List<OrderDTO> uh = list.stream().map((o) -> {
            OrderDTO result = new OrderDTO();
            User user = userDao.findByEmailId(o.getUserEmail());
            BeanUtils.copyProperties(o, result);
            result.setDeliveryAddress(DeliveryAddressDTO.convertDeliveryAddress(o.getDeliveryAddress()));
            result.setOwnerName(user.getLastName() + " " + user.getFirstName());
            return result;
        }).toList();
        OrderDTOResponse response = new OrderDTOResponse();
        response.setSuccess(true);
        response.setResponseMessage("Yipee");
        response.setOrderDTOList(uh);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserOrderHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        Pageable pageable;
        try {
            Sort.Direction direction = Sort.Direction.fromString(sort.length > 1 ? sort[1].toUpperCase() : "DESC");
            Sort sortOrder = Sort.by(direction, sort[0]);
            pageable = PageRequest.of(page, size, sortOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Tham số sắp xếp không hợp lệ. Ví dụ: 'createdAt,desc' hoặc 'totalPrice,asc'.");
        }

        try {
            Page<OrderDTO> orderHistoryPage = orderService.getCurrentUserOrderHistory(pageable);
            return ResponseEntity.ok(orderHistoryPage);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã có lỗi xảy ra, vui lòng thử lại sau.");
        }
    }
}
