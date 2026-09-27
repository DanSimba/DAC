package com.monsterbank.ms_email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String rementente;

    public String sendEmailText(String dest, String assunto, String message){
        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(rementente);
            simpleMailMessage.setTo(dest);
            simpleMailMessage.setSubject(assunto);
            simpleMailMessage.setText(message);
            javaMailSender.send(simpleMailMessage);
            return "Enviado";
        } catch(Exception e){
            return "Erro ao enviar email";
        }
    };

}

