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
        return "login";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied(){
        return "access-denied";
    }

    @PostMapping("/register")
    void register(@Valid @RequestBody AuthRequest body){
        authService.register(body.getEmail(), body.getPassword());
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

    @PostMapping("/refresh")
    TokenPair refresh(@RequestBody RefreshRequest body) {
        return authService.refresh(body.getRefreshToken());
    }
}
