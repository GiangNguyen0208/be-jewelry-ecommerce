package com.example.Jewelry.controller;

import com.example.Jewelry.Utility.Constant;
import com.example.Jewelry.dao.CtvDAO;
import com.example.Jewelry.dto.request.RegisterCTVRequest;
import com.example.Jewelry.dto.request.UserLoginRequest;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.dto.request.RegisterUserRequest;
import com.example.Jewelry.dto.response.UserLoginResponse;
import com.example.Jewelry.entity.CTV;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.resource.UserResource;
import com.example.Jewelry.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @Autowired
    UserResource userResource;

    @Autowired
    UserService userService;

    @Autowired
    CtvDAO ctvDao;
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
    @PostMapping("/register-ctv")
    public ResponseEntity<CommonApiResponse> registerCTV(@RequestBody RegisterCTVRequest request) {
        return userResource.registerCTV(request);
    }
    @PostMapping("/{id}/confirm-CTV")
    public ResponseEntity<CommonApiResponse> confirmCTV(@PathVariable int id, @RequestParam boolean isConfirmed) {
        boolean result = userService.updateCTVStatus(id, isConfirmed);
        CommonApiResponse response = new CommonApiResponse();
        if (result) {
            response.setSuccess(true);
            response.setResponseMessage(isConfirmed ? "User đã được cấp quyền CTV." : "Yêu cầu đã bị từ chối.");
        } else {
            response.setSuccess(false);
            response.setResponseMessage("Không thể cập nhật trạng thái.");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ctv-pending")
    @Operation(summary = "List pending CTV registrations")
    public ResponseEntity<List<RegisterCTVRequest>> getPendingCTV() {
        List<CTV> pendingProfiles = ctvDao.findByStatus(Constant.CtvStatus.PENDING.value());

        List<RegisterCTVRequest> result = pendingProfiles.stream().map(ctv -> {
            User user = ctv.getUser();
            return new RegisterCTVRequest(
                    user.getId(),
                    user.getEmailId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNo(),
                    ctv.getLocation(),
                    ctv.getExperienceAndSkills(),
                    ctv.getSampleWorkLink(),
                    ctv.getReason()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

}
