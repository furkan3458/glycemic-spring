package com.glycemic.mail;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class EmailServiceImpl implements EmailService{
	
	@Autowired
    private JavaMailSender emailSender;

	@Value("${spring.application.name}")
	private String APP;
	
	@Value("${spring.mail.username}")
	private String from;
	
	@Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;
	
    public void sendSimpleMessageWithTemplate(String to, String subject, Map<String,Object> templateModel) throws MessagingException {
    	
    	MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        
        String htmlBody = thymeleafTemplateEngine.process("mail-welcome.html", thymeleafContext);

        helper.setTo(to);
        try {
			helper.setFrom(from,APP);
		} catch (UnsupportedEncodingException | MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        
        
        emailSender.send(message);
    }

	@Override
	public void sendSimpleMessage(String to, String subject, String text) {
		 SimpleMailMessage message = new SimpleMailMessage();
         message.setFrom(APP);
         message.setTo(to);
         message.setSubject(subject);
         message.setText(text);
         
         emailSender.send(message);
	}

}
