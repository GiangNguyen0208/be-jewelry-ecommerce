package com.example.Jewelry.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.Jewelry.dto.response.DeliveryAddressBookResponse;
import com.example.Jewelry.entity.DeliveryAddress;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.service.DeliveryAddressService;
import com.example.Jewelry.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/delivery")
@CrossOrigin(origins = "http://localhost:3000")
public class DeliveryAddressController {

    @Autowired
    private UserService userService;

    /** Sổ địa chỉ */
    @Autowired
    private DeliveryAddressService deliveryAddressService;
    
    /** lấy sổ địa chỉ */
    @GetMapping("/{userID}")
    public ResponseEntity<DeliveryAddressBookResponse> getDeliveryAddresses(@PathVariable int userID) {
        DeliveryAddressBookResponse response = new DeliveryAddressBookResponse();
        User user = userService.getUserById(userID);
        if (user == null) {
            response.setSuccess(false);
            response.setResponseMessage("User is not found.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<DeliveryAddress> addresses = deliveryAddressService.getByUserID(userID);
        response.setSuccess(true);
        response.setResponseMessage("Lấy sổ địa chỉ thành công!");
        response.setAddreses(addresses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

}
