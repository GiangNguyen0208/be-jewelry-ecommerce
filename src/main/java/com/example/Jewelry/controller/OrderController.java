package com.example.Jewelry.controller;

import com.example.Jewelry.dto.request.OrderRequestDTO;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.service.ServiceImpl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderServiceImpl orderService;
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
    public ResponseEntity<CommonApiResponse> createOrder(@RequestBody OrderRequestDTO dto) {
        CommonApiResponse response = orderService.createOrder(dto);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

}
