package com.example.Jewelry.service.ServiceImpl;

import com.example.Jewelry.Utility.Constant;
import com.example.Jewelry.dao.UserDAO;
import com.example.Jewelry.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserDAO userDAO;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String oauth2_id = (String) attributes.get("sub");
        String oauth2_provider = userRequest.getClientRegistration().getRegistrationId(); // "google"
        String email = (String) attributes.get("email");
        Object verifiedObj = attributes.get("email_verified");

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        User user = userDAO.findByEmailId(email);
        if (user == null) {
            user = new User();
            user.setEmailId(email);
            user.setFirstName((String) attributes.get("given_name"));
            user.setLastName((String) attributes.get("family_name"));
            user.setAvatar((String) attributes.get("picture"));
            user.setRole(Constant.UserRole.ROLE_USER.value());
            user.setStatus(Constant.ActiveStatus.ACTIVE.value());
            user.setOauth2_provider(oauth2_provider);
            user.setEmail_verified(verifiedObj instanceof Boolean ? (Boolean) verifiedObj : false);
            user.setOauth2_id(oauth2_id);
            user = userDAO.save(user);
        }

        attributes.put("user", user); // thêm vào attributes để sau lấy ra dễ

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                attributes,
                userNameAttributeName
        );
    }

}
