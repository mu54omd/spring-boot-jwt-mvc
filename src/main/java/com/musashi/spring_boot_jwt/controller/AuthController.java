package com.musashi.spring_boot_jwt.controller;

import com.musashi.spring_boot_jwt.model.AuthRequest;
import com.musashi.spring_boot_jwt.model.RefreshRequest;
import com.musashi.spring_boot_jwt.model.TokenPair;
import com.musashi.spring_boot_jwt.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping("/login")
    public String showLoginForm(Model theModel){
        theModel.addAttribute("authRequest", new AuthRequest());
        return "login-form";
    }
    @GetMapping("/register")
    public String showRegisterForm(Model theModel){
        theModel.addAttribute("authRequest", new AuthRequest());
        return "register-form";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied(){
        return "access-denied";
    }

    @PostMapping("/performRegister")
    public String register(@Valid @ModelAttribute("authRequest") AuthRequest authRequest){
        try {
            authService.register(authRequest.getEmail(), authRequest.getPassword());
        } catch (ResponseStatusException e) {
            return "redirect:/auth/register?conflict";
        }
        return "redirect:/auth/login?successful";
    }

    @PostMapping("/performLogin")
    public String login(
            @ModelAttribute("authRequest") AuthRequest authRequest,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        try {
            TokenPair tokenPair = authService.login(
                    authRequest.getEmail(),
                    authRequest.getPassword(),
                    response, request);
            return "redirect:/user/home";
        }catch (BadCredentialsException e){
            return "redirect:/auth/login?error=true";
        }
    }

    @GetMapping("/performLogout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        authService.logout(request, response);
        return "redirect:/auth/login?logout";
    }
}
