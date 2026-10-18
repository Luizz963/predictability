package com.bacteria_resistant.project.controller;

import com.bacteria_resistant.project.dto.request.AuthenticationDTO;
import com.bacteria_resistant.project.dto.request.ForgotPasswordDTO;
import com.bacteria_resistant.project.dto.request.RegisterDTO;
import com.bacteria_resistant.project.dto.request.ResetPasswordDTO;
import com.bacteria_resistant.project.dto.response.LoginResponseDTO;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.repository.LaboratoryRepository;
import com.bacteria_resistant.project.service.auth.PasswordResetService;
import com.bacteria_resistant.project.service.auth.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LaboratoryRepository repository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.taxId(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((Laboratory) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        if (this.repository.findByTaxId(data.taxId()) != null) {
            return ResponseEntity.badRequest().build();
        }
        if (this.repository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Laboratory newLaboratory = new Laboratory(data.taxId(), data.email(), encryptedPassword, data.name());

        this.repository.save(newLaboratory);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordDTO data) {
        passwordResetService.processForgotPassword(data.identifier());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO data) {
        passwordResetService.resetPassword(data.token(), data.newPassword());
        return ResponseEntity.ok().build();
    }
}