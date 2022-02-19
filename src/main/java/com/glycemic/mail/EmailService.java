package com.glycemic.mail;

import java.util.Map;

import javax.mail.MessagingException;

public interface EmailService {
	public void sendSimpleMessage(String to, String subject, String text);

	public void sendSimpleMessageWithTemplate(String to, String subject, Map<String,Object> templateModel) throws MessagingException;

}
