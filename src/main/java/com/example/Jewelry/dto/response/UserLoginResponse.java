package com.example.Jewelry.dto.response;

import com.example.Jewelry.dto.UserDTO;
import lombok.Data;

@Data
public class UserLoginResponse extends CommonApiResponse {

    private UserDTO user;

    private String jwtToken;

}
