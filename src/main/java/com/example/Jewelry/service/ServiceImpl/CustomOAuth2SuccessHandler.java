package com.example.Jewelry.service.ServiceImpl;

import com.example.Jewelry.Utility.Constant;
import com.example.Jewelry.Utility.JwtUtils;
import com.example.Jewelry.dao.UserDAO;
import com.example.Jewelry.dto.UserDTO;
import com.example.Jewelry.dto.response.UserLoginResponse;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.filter.JwtAuthFilter;
import com.example.Jewelry.resource.UserResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();

        String email = (String) attributes.get("email");
        String oauth2Id = (String) attributes.get("sub"); // Google ID
        String provider = "google"; // Hardcoded nếu bạn dùng mỗi Google

        LOG.info("OAuth2 login - email: {}", email);

        User user = userDAO.findByEmailId(email);
        if (user == null) {
            user = new User();
            user.setEmailId(email);
            user.setFirstName((String) attributes.get("given_name"));
            user.setLastName((String) attributes.get("family_name"));
            user.setAvatar((String) attributes.get("picture"));
            user.setRole(Constant.UserRole.ROLE_USER.value());
            user.setStatus(Constant.ActiveStatus.ACTIVE.value());
            user.setOauth2_provider(provider);
            user.setOauth2_id(oauth2Id);

            // Kiểm tra email_verified
            Object verifiedObj = attributes.get("email_verified");
            boolean isVerified = (verifiedObj instanceof Boolean) ? (Boolean) verifiedObj : false;
            user.setEmail_verified(isVerified);

            user = userDAO.save(user);
        }

        // Tạo JWT
        String jwt = jwtUtils.generateToken(user.getUsername());

        // Encode user info để gửi về frontend
        UserDTO userDTO = UserDTO.toUserDtoEntity(user);
        String userJson = objectMapper.writeValueAsString(userDTO);
        String userBase64 = Base64.getEncoder().encodeToString(userJson.getBytes(StandardCharsets.UTF_8));

        // Build redirect URL
        String redirectURL = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("token", jwt)
                .queryParam("user", userBase64)
                .build().toUriString();

        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", redirectURL);
    }


}
