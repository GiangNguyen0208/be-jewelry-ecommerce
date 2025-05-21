package com.example.Jewelry.service.ServiceImpl;

import com.example.Jewelry.Utility.Constant;
import com.example.Jewelry.Utility.JwtUtils;
import com.example.Jewelry.dao.UserDAO;
import com.example.Jewelry.dto.UserDTO;
import com.example.Jewelry.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger LOG = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String provider = oauthToken.getAuthorizedClientRegistrationId(); // "google" hoặc "facebook"
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();

        String email = null;
        String firstName = null;
        String lastName = null;
        String avatarUrl = null;
        String oauth2Id = null;
        boolean isVerified = false;

        // Xử lý theo provider
        if ("google".equals(provider)) {
            email = (String) attributes.get("email");
            firstName = (String) attributes.get("given_name");
            lastName = (String) attributes.get("family_name");
            avatarUrl = (String) attributes.get("picture");
            oauth2Id = (String) attributes.get("sub");

            Object verifiedObj = attributes.get("email_verified");
            isVerified = verifiedObj instanceof Boolean && (Boolean) verifiedObj;

        } else if ("facebook".equals(provider)) {
            email = (String) attributes.get("email");
            firstName = (String) attributes.get("first_name");
            lastName = (String) attributes.get("last_name");
            oauth2Id = (String) attributes.get("id");

            try {
                Map<String, Object> picture = (Map<String, Object>) attributes.get("picture");
                if (picture != null) {
                    Map<String, Object> data = (Map<String, Object>) picture.get("data");
                    if (data != null) {
                        avatarUrl = (String) data.get("url");
                    }
                }
            } catch (Exception e) {
                LOG.warn("Cannot extract Facebook avatar", e);
            }

            isVerified = true; // Facebook mặc định email là verified nếu cung cấp được
        }

        if (email == null || oauth2Id == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email or OAuth2 ID missing.");
            return;
        }

        // Kiểm tra user
        User user = userDAO.findByEmailId(email);
        if (user == null) {
            user = new User();
            user.setEmailId(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAvatar(avatarUrl);
            user.setRole(Constant.UserRole.ROLE_USER.value());
            user.setStatus(Constant.ActiveStatus.ACTIVE.value());
            user.setOauth2_provider(provider);
            user.setOauth2_id(oauth2Id);
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
