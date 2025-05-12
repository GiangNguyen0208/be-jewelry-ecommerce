package com.example.Jewelry.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.Jewelry.dto.response.DeliveryAddressBookResponse;
import com.example.Jewelry.resource.UserResource;

import java.net.http.HttpResponse.ResponseInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/delivery")
@CrossOrigin(origins = "http://localhost:3000")
public class DeliveryAddressController {

    
    @Autowired
    UserResource userResource;

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryAddressBookResponse> getAddressesFromUser(@PathVariable int id) {
        return userResource.getDeliveryAddresses(id);
    }
    

}
