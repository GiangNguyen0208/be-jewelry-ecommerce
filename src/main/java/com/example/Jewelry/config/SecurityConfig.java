package com.example.Jewelry.config;

import com.example.Jewelry.filter.JwtAuthFilter;
import com.example.Jewelry.service.ServiceImpl.CustomOAuth2FailureHandler;
import com.example.Jewelry.service.ServiceImpl.CustomOAuth2SuccessHandler;
import com.example.Jewelry.service.ServiceImpl.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter authFilter;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Autowired
    private CustomOAuth2FailureHandler customOAuth2FailureHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    // authentication
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http.csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.disable())
//
//                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/user/login", "/api/user/register", "/api/user/confirm", "/api/user/resend-confirmation").permitAll()
//
////						// this APIs are only accessible by ADMIN
//                        .requestMatchers("/api/user/admin/register", "/api/user/delete/seller", "/api/order/fetch/all",
//                                "/api/category/update", "/api/category/add", "/api/category/delete",
//                                "/api/user/fetch/role-wise", "/api/user/update/status")
//                        .hasAuthority(Constant.UserRole.ROLE_ADMIN.value())
////
////						// this APIs are only accessible by SELLER
//                        .requestMatchers("/api/user/fetch/seller/delivery-person", "/api/user/delete/seller/delivery-person", "/api/product/update/image",
//                                "/api/product/update/detail", "/api/product/add", "/api/product/delete",
//                                "/api/order/assign/delivery-person", "/api/order/fetch/seller-wise",
//                                "/api/product/review/seller")
//                        .hasAuthority(Constant.UserRole.ROLE_CTV.value())
////
////						// this APIs are only accessible by SELLER
//                        .requestMatchers("/api/order/add", "/api/order/fetch/user-wise", "/api/cart/update",
//                                "/api/cart/add", "/api/cart/fetch", "/api/cart/delete", "/api/product/review/add")
//                        .hasAuthority(Constant.UserRole.ROLE_USER.value())
////
////						// this APIs are only accessible by ADMIN & SELLER
//                        .requestMatchers("/api/user/fetch/role-wise", "/api/user/update/status")
//                        .hasAnyAuthority(Constant.UserRole.ROLE_ADMIN.value())
//
//                        .anyRequest().permitAll())
//
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//
//    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/user/login",
                                "/api/user/register",
                                "/api/user/confirm",
                                "/api/user/resend-confirmation",
                                "/oauth2/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customOAuth2FailureHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder.bCryptPasswordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")  // Đảm bảo cho phép frontend FE
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }


}

