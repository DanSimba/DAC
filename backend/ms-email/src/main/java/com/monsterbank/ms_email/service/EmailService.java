package com.monsterbank.ms_email.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String rementente;

    public String sendEmailText(String dest, String assunto, String message){
        try{
            log.info("Iniciando envio de e-mail.");

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(rementente);
            simpleMailMessage.setTo(dest);
            simpleMailMessage.setSubject(assunto);
            simpleMailMessage.setText(message);
            javaMailSender.send(simpleMailMessage);

            log.info("Enviando e-mail destinatario: {}", dest);
            log.info("Enviando e-mail assunto: {}", assunto);
            log.info("Enviando e-mail mensagem: {}", message);
            return "Enviado";
        } catch(Exception e){
            log.error("Erro ao enviar email: {}", e);
            return "Erro ao enviar email";
        }
    };

    public void sendPassword(String dest, String password){
        String assunto = "Senha da conta";
        String mensagem = "A senha da sua conta é: "+password;
        sendEmailText(dest, assunto, mensagem);
    }

    public void sendManagerChange(String dest, String managerName){
        String assunto = "Novo gerente";
        String mensagem = "O gerente da sua conta é: "+managerName;
        sendEmailText(dest, assunto, mensagem);
    }

    public void sendManagerExclude(String dest, String managerName){
        String assunto = "Gerente removido";
        String mensagem = "O gerente da sua conta "+managerName+" foi removido";
        sendEmailText(dest, assunto, mensagem);
    }

}

