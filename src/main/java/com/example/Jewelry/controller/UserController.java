package com.example.Jewelry.controller;

import com.example.Jewelry.dto.UserDTO;
import com.example.Jewelry.dto.request.UserLoginRequest;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.dto.response.RegisterUserRequest;
import com.example.Jewelry.dto.response.UserDTOResponse;
import com.example.Jewelry.dto.response.UserLoginResponse;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.resource.UserResource;
import com.example.Jewelry.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @Autowired
    UserResource userResource;

    @Autowired
    UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Api to login any User")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        return userResource.login(userLoginRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<CommonApiResponse> register(@RequestBody RegisterUserRequest request){
        return userResource.registerUser(request);
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<CommonApiResponse> confirm(@RequestParam("token") String token) {
        return userResource.confirmToken(token);
    }

    @GetMapping(path = "/resend-confirmation")
    public ResponseEntity<CommonApiResponse> resendConfirm(@RequestParam("token") String email) {
        return userResource.resendConfirmToken(email);
    }

    @GetMapping(value = "/{userImageName}", produces = "image/*")
    public void fetchTourImage(@PathVariable("userImageName") String userImageName, HttpServletResponse resp) {
        this.userResource.fetchUserImage(userImageName, resp);
    }

    @GetMapping(value = "/users")
    public  ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    @GetMapping(value = "/info/{userID}")
    public ResponseEntity<UserDTOResponse> getUserByID(@PathVariable int userID) {
        UserDTOResponse response = new UserDTOResponse();
        User user = userService.getUserById(userID);
        if (user == null) {
            response.setSuccess(false);
            response.setResponseMessage("Error: No user founded with this id!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        UserDTO userDTO = UserDTO.toUserDtoEntity(user);
        response.setSuccess(true);
        response.setResponseMessage("Get user detail successfully!");
        response.setData(userDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/upload-avatar")
    public ResponseEntity<CommonApiResponse> uploadAvatar(
            @PathVariable int id,
            @RequestParam("avatar") MultipartFile file) {

        CommonApiResponse response = new CommonApiResponse();

        if (file.isEmpty()) {
            response.setResponseMessage("File is empty.");
            response.setSuccess(false);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            userResource.updateUserAvatar(id, file);
            response.setResponseMessage("Avatar updated successfully");
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setResponseMessage("Error saving avatar: " + e.getMessage());
            response.setSuccess(false);
            return ResponseEntity.status(500).body(response);
        }
    }


}
