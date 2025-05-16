package com.example.Jewelry.resource;

import com.example.Jewelry.Utility.Constant;
import com.example.Jewelry.Utility.JwtUtils;
import com.example.Jewelry.dao.UserDAO;
import com.example.Jewelry.dto.request.UserLoginRequest;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.dto.response.DeliveryAddressBookResponse;
import com.example.Jewelry.dto.response.RegisterUserRequest;
import com.example.Jewelry.dto.response.UserDTOResponse;
import com.example.Jewelry.dto.response.UserLoginResponse;
import com.example.Jewelry.entity.ConfirmationToken;
import com.example.Jewelry.entity.DeliveryAddress;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.exception.UserSaveFailedException;
import com.example.Jewelry.service.DeliveryAddressService;
import com.example.Jewelry.service.EmailService;
import com.example.Jewelry.service.ServiceImpl.ConfirmationTokenService;
import com.example.Jewelry.service.StorageService;
import com.example.Jewelry.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Transactional
public class UserResource {

    private final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private StorageService storageService;

    @Autowired
    private JwtUtils jwtUtils;

    /** đăng nhập */
    public ResponseEntity<UserLoginResponse> login(UserLoginRequest loginRequest) {

        LOG.info("Received request for User Login");

        UserLoginResponse response = new UserLoginResponse();

        if (loginRequest == null) {
            response.setResponseMessage("Missing Input");
            response.setSuccess(false);

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String jwtToken = null;
        User user = null;

        user = this.userService.getUserByEmailid(loginRequest.getEmailId());

        if (user == null) {
            response.setResponseMessage("User with this Email Id not registered in System!!!");
            response.setSuccess(false);

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(user.getRole()));

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmailId(),
                    loginRequest.getPassword(), authorities));
        } catch (Exception ex) {
            response.setResponseMessage("Invalid email or password.");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        jwtToken = jwtUtils.generateToken(loginRequest.getEmailId());

        if (!user.getStatus().equals(Constant.ActiveStatus.ACTIVE.value())) {
            response.setResponseMessage("User is not active");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // user is authenticated
        if (jwtToken != null) {
            response.setUserID(user.getId());
            response.setUsername(user.getUsername());
            response.setResponseMessage("Logged in sucessful");
            response.setSuccess(true);
            response.setJwtToken(jwtToken);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        else {
            response.setResponseMessage("Failed to login");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }

    /** đăng kí */
    public ResponseEntity<CommonApiResponse> registerUser(RegisterUserRequest request) {
        LOG.info("Request received for Register User");

        CommonApiResponse response = new CommonApiResponse();

        if (request == null) {
            response.setResponseMessage("user is null");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getEmailId() == null || request.getPassword() == null) {
            response.setResponseMessage("missing input");
            response.setSuccess(false);

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // User existed and actived.
        User existingUser = this.userService.getUserByEmailAndStatus(request.getEmailId(), Constant.ActiveStatus.ACTIVE.value());

        if (existingUser != null) {
            response.setResponseMessage("User adready register with this Email");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getRole() == null) {
            response.setResponseMessage("bad request ,Role is missing");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        // User existed but deactived.
        User userDeactive = this.userService.getUserByEmailAndStatus(request.getEmailId(), Constant.ActiveStatus.DEACTIVATED.value());
        if (userDeactive != null) {
            response.setResponseMessage("Please Confirm Email to active account to login");
            response.setSuccess(false);
        }


        // Register User Input Form
        User user = RegisterUserRequest.toUserEntity(request);
        LocalDateTime now = LocalDateTime.now();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setAmount(BigDecimal.ZERO);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(Constant.ActiveStatus.DEACTIVATED.value());
        user.setCreatedAt(now);
        user.setRole(Constant.UserRole.ROLE_USER.value());
        User userActive = userService.addUser(user);

        String token = userService.generateToken(user);
        String frontendUrl = "http://localhost:3000/verify-email?token=" + token;
        emailService.send(
                user.getEmailId(),
                buildEmail(user.getUsername(), frontendUrl));

        if (userActive == null) {
            throw new UserSaveFailedException("Registration Failed because of Technical issue:(");
        }

        response.setResponseMessage("User registered Successfully");
        response.setSuccess(true);

        return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
    }

    /** tạo chuỗi email  */
    private String buildEmail(String username, String link) {
        return "<div style=\"font-family: Arial, sans-serif; font-size: 16px; color: #333; line-height: 1.6;\">"
                + "<h2 style=\"color: #1a73e8;\">Chào " + username + ",</h2>"
                + "<p>Cảm ơn bạn đã đăng ký tài khoản tại hệ thống của chúng tôi.</p>"
                + "<p>Vui lòng xác nhận email của bạn bằng cách nhấp vào nút bên dưới:</p>"
                + "<p style=\"text-align: center;\">"
                + "<a href=\"" + link + "\" style=\"display: inline-block; padding: 12px 24px; color: #fff; background-color: #1a73e8; text-decoration: none; border-radius: 5px; font-weight: bold;\">Xác nhận email</a>"
                + "</p>"
                + "<p>Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email này.</p>"
                + "<p>Trân trọng,<br><strong>Đội ngũ hỗ trợ Jewelry</strong></p>"
                + "</div>";
    }

    /** xác thực token */
    public ResponseEntity<CommonApiResponse> confirmToken(String token) {
        LOG.info("Confirm mail");

        CommonApiResponse response = new CommonApiResponse();

        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.activeUser(
                confirmationToken.getUser().getEmailId());

        response.setResponseMessage("Confirm Email Successfully, token is: " + token);
        response.setSuccess(true);

        return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
    }

    /** gửi lại token xác thực */
    public ResponseEntity<CommonApiResponse> resendConfirmToken(String email) {
        LOG.info("Resending confirmation email for: " + email);

        CommonApiResponse response = new CommonApiResponse();
        User user = userService.getUserByEmailAndStatus(email, Constant.ActiveStatus.DEACTIVATED.value());

        if (user == null) {
            response.setResponseMessage("Email không tồn tại hoặc đã được xác nhận.");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String token = userService.generateToken(user);
        String frontendUrl = "http://localhost:5173/verify-email?token=" + token;

        emailService.send(user.getEmailId(), buildEmail(user.getUsername(), frontendUrl));

        response.setResponseMessage("Email xác nhận đã được gửi lại.");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** lấy ảnh và lưu vào máy? */
    public void fetchUserImage(String userImageName, HttpServletResponse resp) {
        Resource resource = storageService.load(userImageName);
        if (resource != null) {
            try (InputStream in = resource.getInputStream()) {
                ServletOutputStream out = resp.getOutputStream();
                FileCopyUtils.copy(in, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** cập nhật ảnh đại diện */
    public void updateUserAvatar(int userId, MultipartFile avatarFile) {
        LOG.info("Check file avatar: " + avatarFile);
        if (avatarFile == null || avatarFile.isEmpty()) {
            throw new IllegalArgumentException("Avatar file must not be empty");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Kiểm tra loại file (tùy chọn)
        if (!avatarFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }

        // Xóa file cũ nếu tồn tại
        if (user.getAvatar() != null && !user.getAvatar().isBlank()) {
            storageService.delete(user.getAvatar());
        }

        // Lưu file mới
        String savedFileName = storageService.store(avatarFile);

        // Cập nhật user
        user.setAvatar(savedFileName);
        userDAO.save(user);
    }


}
