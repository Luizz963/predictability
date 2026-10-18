package com.bacteria_resistant.project.service.auth;

import com.bacteria_resistant.project.exception.InvalidTokenException;
import com.bacteria_resistant.project.model.Laboratory;
import com.bacteria_resistant.project.model.PasswordResetToken;
import com.bacteria_resistant.project.repository.LaboratoryRepository;
import com.bacteria_resistant.project.repository.PasswordResetTokenRepository;
import com.bacteria_resistant.project.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final LaboratoryRepository laboratoryRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(LaboratoryRepository laboratoryRepository,
                                PasswordResetTokenRepository tokenRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.laboratoryRepository = laboratoryRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void processForgotPassword(String identifier) {
        Optional<Laboratory> labOpt = laboratoryRepository.findOptionalByTaxId(identifier);
        if (labOpt.isEmpty()) {
            labOpt = laboratoryRepository.findByEmail(identifier);
        }


        if (labOpt.isPresent()) {
            Laboratory lab = labOpt.get();
            String token = UUID.randomUUID().toString();

            PasswordResetToken resetToken = new PasswordResetToken(token, lab, 30); // 30 minutos
            tokenRepository.save(resetToken);

            emailService.sendPasswordResetEmail(lab.getEmail(), token);
        }
    }

    @Transactional
    public void resetPassword(String tokenString, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByTokenAndUsedFalse(tokenString)
                .orElseThrow(() -> new InvalidTokenException("Token de recuperação inválido ou já utilizado."));

        if (resetToken.isExpired()) {
            throw new InvalidTokenException("Token expirado. Solicite uma nova recuperação.");
        }

        Laboratory lab = resetToken.getLaboratory();
        lab.setPassword(passwordEncoder.encode(newPassword));
        laboratoryRepository.save(lab);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}