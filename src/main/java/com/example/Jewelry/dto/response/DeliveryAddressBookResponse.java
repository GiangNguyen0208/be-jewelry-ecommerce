package com.example.Jewelry.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.example.Jewelry.entity.DeliveryAddress;

import lombok.Data;

@Data
public class DeliveryAddressBookResponse extends CommonApiResponse {

    private List<DeliveryAddress> addreses = new ArrayList<>();

}
