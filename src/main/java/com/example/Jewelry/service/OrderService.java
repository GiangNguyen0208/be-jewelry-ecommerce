package com.example.Jewelry.service;

import com.example.Jewelry.dto.request.OrderRequestDTO;
import com.example.Jewelry.dto.response.CommonApiResponse;

public interface OrderService {
    CommonApiResponse createOrder(OrderRequestDTO dto);
}
