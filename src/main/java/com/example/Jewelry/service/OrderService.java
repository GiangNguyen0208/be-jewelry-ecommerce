package com.example.Jewelry.service;

import java.util.List;

import com.example.Jewelry.dto.request.OrderRequestDTO;
import com.example.Jewelry.dto.response.CommonAPIResForOrder;
import com.example.Jewelry.entity.Order;

public interface OrderService {
    CommonAPIResForOrder createOrder(OrderRequestDTO dto);
    List<Order> getAllOrders();
    Order getSingleOrder(int orderID);
}
