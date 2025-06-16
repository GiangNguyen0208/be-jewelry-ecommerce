package com.example.Jewelry.service;

import com.example.Jewelry.dto.request.OrderRequestDTO;
import com.example.Jewelry.dto.response.CommonAPIResForOrder;

public interface OrderService {
    CommonAPIResForOrder createOrder(OrderRequestDTO dto);

}
