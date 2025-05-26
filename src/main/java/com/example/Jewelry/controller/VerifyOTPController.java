package com.example.Jewelry.controller;
import com.example.Jewelry.dto.request.OtpVerificationRequestDTO;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.service.ServiceImpl.OTPVerifyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verify")
public class VerifyOTPController {
    private final OTPVerifyServiceImpl orderService;

    @PostMapping("/verify-otp")
    public ResponseEntity<CommonApiResponse> verifyOtp(@RequestBody OtpVerificationRequestDTO dto) {
        return ResponseEntity.ok(orderService.verifyOtp(dto));
    }

}
