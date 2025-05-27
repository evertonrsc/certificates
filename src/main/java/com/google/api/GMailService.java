package com.google.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.*;

public class GMailService {
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final String APPLICATION_NAME = "Gmail Java App";

    private final AuthService authService;

    public GMailService(AuthService authService) {
        this.authService = authService;
    }

    public Gmail createGmailClient() {
        List<String> scopes = List.of(GmailScopes.GMAIL_SEND);
        Credential credential = authService.getCredentials(scopes);

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void sendEmailWithAttachment(String from, String to, String subject, String bodyText, File attachment) {
        MimeMessage mimeMessage = createEmailWithAttachment(from, to, subject, bodyText, attachment);
        Message message = createMessageWithEmail(mimeMessage);
        Gmail gmailClient = createGmailClient();
        try {
            gmailClient.users().messages().send("me", message).execute();
            System.out.println("> Email sent successfully to " + to);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MimeMessage createEmailWithAttachment(String from, String to, String subject, String bodyText,
                                                  File attachment) {
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage email = new MimeMessage(session);
        try {
            email.setFrom(new InternetAddress(from));
            email.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
            email.setSubject(subject);

            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(bodyText, "text/html; charset=utf-8");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            FileDataSource fileDataSource = new FileDataSource(attachment);
            attachmentPart.setDataHandler(new DataHandler(fileDataSource));
            attachmentPart.setFileName(fileDataSource.getName());

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            multipart.addBodyPart(attachmentPart);

            email.setContent(multipart);
            return email;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private Message createMessageWithEmail(MimeMessage emailContent) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            emailContent.writeTo(outputStream);
            byte[] rawBytes = outputStream.toByteArray();
            String encodedEmail = Base64.getUrlEncoder().encodeToString(rawBytes);

            Message message = new Message();
            message.setRaw(encodedEmail);
            return message;
        } catch (IOException | MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
