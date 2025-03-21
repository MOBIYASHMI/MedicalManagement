package com.example.medical.service.impl.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        var authorities=authentication.getAuthorities();
        var roles=authorities.stream().map(r -> r.getAuthority()).findFirst();

        if(roles.orElse("").equals("DOCTOR")){
            response.sendRedirect("/doctor-page");
        }else if(roles.orElse("").equals("PATIENT")) {
            response.sendRedirect("/patient-page");
        }else {
            response.sendRedirect("/error");
        }
    }
}
