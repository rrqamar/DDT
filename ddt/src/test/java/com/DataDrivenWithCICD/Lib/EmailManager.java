package com.DataDrivenWithCICD.Lib;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.testng.log4testng.Logger;

public class EmailManager {

	final static Logger logger = Logger.getLogger(EmailManager.class);
	private String toAddress = "";
	private String ccAddress = "";
	private String bccAddress = "";
	public List<String> attchmentFiles = new ArrayList<String>();
	

	private void setToAddress(String toEmailList) {
		toAddress = toEmailList;
	}

	public void sendEmail(List<String> attachments) {
		String emailMsgBody = "Test email by JavaMail API example. " + "<br><br> Regards, <br>Test Automation Team<br>";

		setToAddress("musabaytechtraining@gmail.com,test0014524@gmail.com");
		sendEmail("smtp.gmail.com", "587", "test0014524@gmail.com", "0123456789+-", "Automated Java Email for Selenium",
				emailMsgBody, attachments);
	}

	private InternetAddress[] setMultipleEmails(String emailAddress) {
		String multipleEmails[] = emailAddress.split(",");
		InternetAddress[] addresses = new InternetAddress[multipleEmails.length];
		try {
			for (int i = 0; i < multipleEmails.length; i++) {
				addresses[i] = new InternetAddress(multipleEmails[i]);
			}
		} catch (AddressException e) {
			logger.error("Adding multiple email addresses error!", e);
		}
		return addresses;
	}	
	
	public void sendEmail(String host, String port, final String emailUserID, final String emailUserPassword, String subject,
			String emailMsgBody, List<String> attachments) {
			try{
				// sets SMTP server properties
				Properties prop = new Properties();
				prop.put("mail.smtp.host", host);
				prop.put("mail.smtp.port", port);
				prop.put("mail.smtp.auth", "true");
				prop.put("mail.smtp.starttls.enable", "true");
				prop.put("mail.user", emailUserID);
				prop.put("mail.password", emailUserPassword);
				logger.info("step1> preparing email configuration...");
				
				//creates a new session with an authenticator
				Authenticator auth = new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication(){
						return new PasswordAuthentication(emailUserID, emailUserPassword);
					}
				};
				Session session = Session.getInstance(prop, auth);
				
				//Create a new e-mail message
				Message msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(emailUserID));
				msg.addRecipients(Message.RecipientType.TO, setMultipleEmails(toAddress));
				if(!ccAddress.equals(null) && !ccAddress.isEmpty())
				{
					msg.addRecipients(Message.RecipientType.CC, setMultipleEmails(ccAddress));
				}
				if(!bccAddress.equals(null) && !bccAddress.isEmpty())
				{
					msg.addRecipients(Message.RecipientType.BCC, setMultipleEmails(bccAddress));
				}
				
				msg.setSubject(subject);
				msg.setSentDate(new Date());
				
				// Creates message part
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(emailMsgBody, "text/html");
				
				// Creates multi-part
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				
				// Adds attachments
				if(attachments != null && attachments.size() > 0)
				{
					for(String temp : attachments)
					{
						MimeBodyPart attachPart = new MimeBodyPart();
						try{
						attachPart.attachFile(temp);
						}catch(IOException e)
						{
							logger.error("Oops, Attaching files to email error !!!", e);
						}
						multipart.addBodyPart(attachPart);
					}
					
				}
				
				logger.info("Step2> attaching report files & error screenshots...");
				// sets the multi-part as email's content
				msg.setContent(multipart);
				
				// Sends the e-mail
				logger.info("Step3> Sending e-mail in progress...");
				Transport.send(msg);
				logger.info("Step4> Sending e-mail complete...");			
				
			}catch(AddressException e)
			{
				logger.error("Sending email error !!!", e);
			}catch(MessagingException e)
			{
				logger.error("Sending email error !!!", e);
			}
	}	
}


