package com.example.Jewelry.controller;

import com.example.Jewelry.dao.ProductDAO;
import com.example.Jewelry.dao.UserDAO;
import com.example.Jewelry.dto.DeliveryAddressDTO;
import com.example.Jewelry.dto.OrderDTO;
import com.example.Jewelry.dto.OrderItemDTO;
import com.example.Jewelry.dto.request.OrderRequestDTO;
import com.example.Jewelry.dto.response.CommonAPIResForOrder;
import com.example.Jewelry.dto.response.OrderDTOResponse;
import com.example.Jewelry.entity.Image;
import com.example.Jewelry.entity.Order;
import com.example.Jewelry.entity.Product;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.entity.Order.OrderStatus;
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
    private final ProductDAO productController;
    // this is
    // {
    // "userId": 3,
    // "deliveryAddressId": 1,
    // "cartItems": [
    // {
    // "productId": 2,
    // "quantity": 2
    // },
    // {
    // "productId": 1,
    // "quantity": 1
    // }
    // ],
    // "totalAmount": 1000.0,
    // "discount": 50.0,
    // "paymentMethod": true
    // }

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

            List<OrderItemDTO> itemLists = OrderItemDTO.convertItemList(o.getItems());
            itemLists.forEach((item) -> {
                Product p = productController.findById(item.getProductId()).orElse(new Product());
                Image pImages = p.getImages() != null ? p.getImages().get(0) : null;
                item.setProductName(p.getName());
                item.setProductImageUrl(pImages == null ? null : pImages.getUrl());
            });
            result.setItems(itemLists);
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

    @GetMapping("/fetch/detail/{orderID}")
    public ResponseEntity<OrderDTOResponse> getSingleOrderDetail(@PathVariable int orderID) {
        Order singleOrder = orderService.getSingleOrder(orderID);
        OrderDTO result = new OrderDTO();
        User user = userController.findByEmailId(singleOrder.getUserEmail());
        BeanUtils.copyProperties(singleOrder, result);
        result.setDeliveryAddress(DeliveryAddressDTO.convertDeliveryAddress(singleOrder.getDeliveryAddress()));
            List<OrderItemDTO> itemLists = OrderItemDTO.convertItemList(singleOrder.getItems());
            itemLists.forEach((item) -> {
                Product p = productController.findById(item.getProductId()).orElse(new Product());
                Image pImages = p.getImages() != null ? p.getImages().get(0) : null;
                item.setProductName(p.getName());
                item.setProductImageUrl(pImages == null ? null : pImages.getUrl());
                item.setProductId(p.getId());
            });
            result.setItems(itemLists);
        result.setOwnerName(user.getLastName() + " " + user.getFirstName());
        OrderDTOResponse response = new OrderDTOResponse();
        response.setSuccess(true);
        response.setResponseMessage("Yipee");
        response.setOrder(result);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update/{orderID}")
    public ResponseEntity<OrderDTOResponse> updateOrderStatus(@PathVariable int orderID, @RequestParam OrderStatus status) {
        OrderDTOResponse response = new OrderDTOResponse();
        // nếu ko lấy được, thì báo lỗi thất bại
        Order singleOrder = orderService.getSingleOrder(orderID);
        if (singleOrder == null) {
            response.setSuccess(false);
            response.setResponseMessage("Could not get the order to set status to");
        }
        else {
            boolean state = orderService.updateOrderStatus(singleOrder, status);
            response.setSuccess(state);
            response.setResponseMessage(state ? "Cập nhật thành công" : "Cập nhật không thành công");
        }

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

}
