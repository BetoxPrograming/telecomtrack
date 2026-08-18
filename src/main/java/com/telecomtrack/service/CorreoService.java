package com.telecomtrack.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {

    private final JavaMailSender mailSender;
    private final String usuarioCorreo;
    private final String passwordCorreo;

    public CorreoService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String usuarioCorreo,
            @Value("${spring.mail.password:}") String passwordCorreo) {
        this.mailSender = mailSender;
        this.usuarioCorreo = usuarioCorreo;
        this.passwordCorreo = passwordCorreo;
    }

    public boolean estaConfigurado() {
        return usuarioCorreo != null && !usuarioCorreo.isBlank()
                && passwordCorreo != null && !passwordCorreo.isBlank();
    }

    public void enviarCorreoHtml(
            String para,
            String asunto,
            String contenido) throws MessagingException {

        if (!estaConfigurado()) {
            return;
        }

        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper correo = new MimeMessageHelper(mensaje, true);

        correo.setFrom(usuarioCorreo);
        correo.setTo(para);
        correo.setSubject(asunto);
        correo.setText(contenido, true);
        mailSender.send(mensaje);
    }
}
