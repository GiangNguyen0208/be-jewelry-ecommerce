package com.example.Jewelry.controller;

import com.example.Jewelry.dao.UserDAO;
import com.example.Jewelry.dto.DeliveryAddressDTO;
import com.example.Jewelry.dto.OrderDTO;
import com.example.Jewelry.dto.OrderItemDTO;
import com.example.Jewelry.dto.request.OrderRequestDTO;
import com.example.Jewelry.dto.response.CommonAPIResForOrder;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.dto.response.OrderDTOResponse;
import com.example.Jewelry.entity.Order;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.service.ServiceImpl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    private final OrderServiceImpl orderService;
    private final UserDAO userController;
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
            User user = userController.findByEmailId(o.getUserEmail());
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
            User user = userController.findByEmailId(o.getUserEmail());
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

}
