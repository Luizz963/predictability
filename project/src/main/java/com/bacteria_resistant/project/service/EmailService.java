package com.bacteria_resistant.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;


        if (mailSender == null) {
            System.out.println("==================================================");
            System.out.println("SIMULAÇÃO DE ENVIO DE E-MAIL (DEV)");
            System.out.println("Para: " + toEmail);
            System.out.println("Link de Recuperação: " + resetLink);
            System.out.println("==================================================");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("BacResist_IA - Recuperação de Senha");
        message.setText("Olá,\n\nRecebemos uma solicitação para redefinir a senha da sua instituição no BacResist_IA.\n\n" +
                "Clique no link abaixo para criar uma nova senha (link válido por 30 minutos):\n" +
                resetLink + "\n\n" +
                "Se você não solicitou essa alteração, ignore esta mensagem.");

        mailSender.send(message);
    }
}