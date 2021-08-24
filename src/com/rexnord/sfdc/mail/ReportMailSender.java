package com.rexnord.sfdc.mail;
 
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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


import org.apache.log4j.Logger;
 
public class ReportMailSender {
 
static Properties props;	
final static Logger logger = Logger.getLogger(ReportMailSender.class);
    
public static void sendEmailWithAttachments(String host, String port,
            final String userName, final String password, String toAddress,
            String subject, String message, String[] attachFiles)
            throws AddressException, MessagingException {
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", userName);
        properties.put("mail.password", password);
 
        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);
 
        // creates a new e-mail message
        Message msg = new MimeMessage(session);
 
        msg.setFrom(new InternetAddress(userName));
        msg.addRecipients(Message.RecipientType.CC, 
                InternetAddress.parse(toAddress));
        //msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
 
        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");
 
        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
 
        // adds attachments
        if (attachFiles != null && attachFiles.length > 0) {
            for (String filePath : attachFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();
 
                try {
                    attachPart.attachFile(filePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
 
                multipart.addBodyPart(attachPart);
            }
        }
 
        // sets the multi-part as e-mail's content
        msg.setContent(multipart);
 
        // sends the e-mail
       Transport.send(msg);
 
    }
 
    /**
     * Test sending e-mail with attachments
     */
    public void sendReport(String propertiesFile) {
    	
    	
    	 props = new Properties();
    	 try {

    	   props.load(new FileInputStream("properties/" + propertiesFile));
    	   
    	   	   
    		
       	// SMTP info
           String host = props.getProperty("host").trim();
           String port = props.getProperty("port").trim();
           String mailFrom = props.getProperty("mailFrom").trim();
           String password = props.getProperty("password").trim();
    
           // message info
           String mailTo = props.getProperty("mailTo").trim();
           String subject = props.getProperty("subject").trim();
           String message = props.getProperty("message").trim();
           String folderLocation = props.getProperty("dropFolder").trim();
           String filename = props.getProperty("filestoAttach").trim();
    	
        // attachments
        
        String attachFiles[] =  new String[1];
   		String DATE_FORMAT = "yyyyMMdd";
   		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
   		Calendar cal = Calendar.getInstance(); // today
   		String dateExt = sdf.format(cal.getTime()).toString() +".csv";
   		int qCount =0;
    	attachFiles[qCount] = folderLocation+ filename + dateExt;
    	//logger.info("Attachment File Name : " + attachFiles[qCount]);
    	    	
    	
   		sendEmailWithAttachments(host, port, mailFrom, password, mailTo,
                subject, message, attachFiles);
           logger.info("SFDC Report Email sent.");
                      
        } catch (Exception ex) {
        	logger.error("SFDC Report mail not sent.");
        	logger.error(ex.getMessage());
        }
    }
}