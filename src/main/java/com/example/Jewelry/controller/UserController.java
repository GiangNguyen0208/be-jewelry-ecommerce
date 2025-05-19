package com.example.Jewelry.controller;

import com.example.Jewelry.dto.request.ChangePasswordRequestDTO;
import com.example.Jewelry.dto.request.UserLoginRequest;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.dto.request.RegisterUserRequest;
import com.example.Jewelry.dto.response.UserLoginResponse;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.resource.UserResource;
import com.example.Jewelry.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    // Change Password
    @PutMapping("/change-password")
    @Operation(summary = "Api to change password")
    public ResponseEntity<CommonApiResponse> changePassword(@RequestBody ChangePasswordRequestDTO request) {
        return userResource.changePassword(request);
    }


    // Forget password.
    @GetMapping("/forget-password")
    @Operation(summary = "Api to login any User")
    public ResponseEntity<CommonApiResponse> forgetPassword(@RequestParam String email) {
        return userResource.forgetPassword(email);
    }

    @GetMapping("/verify-reset-token")
    public ResponseEntity<?> verifyResetToken(@RequestParam("token") String token) {
        Optional<User> userOpt = userService.verifyResetPasswordToken(token);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token không hợp lệ hoặc đã hết hạn.");
        }

        User user = userOpt.get();

        return ResponseEntity.ok(Map.of(
                "email", user.getEmailId(),
                "username", user.getUsername()
        ));
    }

    @PutMapping("/reset-password")
    @Operation(summary = "Api to reset password")
    public ResponseEntity<CommonApiResponse> resetPassword(@RequestBody ChangePasswordRequestDTO request) {
        return userResource.resetPassword(request);
    }


}
