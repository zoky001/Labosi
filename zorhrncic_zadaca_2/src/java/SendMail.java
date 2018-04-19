package org.foi.nwtis.dkermek.web;

import java.io.IOException;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SendMail extends HttpServlet {

    private String smtpHost;

    // Initialize the servlet with the hostname of the SMTP server
    // we'll be using the send the messages
    public void init(ServletConfig config)
            throws ServletException {
        super.init(config);

        smtpHost = config.getInitParameter("smtpHost");
        if (smtpHost == null || smtpHost.length() == 0) {
            smtpHost = "127.0.0.1";
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doPost(request, response);
    }

    public void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, java.io.IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String cc = request.getParameter("cc");
        String bcc = request.getParameter("bcc");
        String subject = request.getParameter("subject");
        String text = request.getParameter("text");

        String status;

        try {
            // Create the JavaMail session
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", smtpHost);

            Session session =
                    Session.getInstance(properties, null);

            // Construct the message
            MimeMessage message = new MimeMessage(session);

            // Set the from address
            Address fromAddress = new InternetAddress(from);
            message.setFrom(fromAddress);

            // Parse and set the recipient addresses
            Address[] toAddresses = InternetAddress.parse(to);
            message.setRecipients(Message.RecipientType.TO, toAddresses);


            Address[] ccAddresses = InternetAddress.parse(cc);
            message.setRecipients(Message.RecipientType.CC, ccAddresses);

            Address[] bccAddresses = InternetAddress.parse(bcc);
            message.setRecipients(Message.RecipientType.BCC, bccAddresses);

            // Set the subject and text
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);

            status = "Your message was sent.";

        } catch (AddressException e) {
            e.printStackTrace();
            status = "There was an error parsing the addresses.";
        } catch (SendFailedException e) {
            e.printStackTrace();
            status = "There was an error sending the message.";
        } catch (MessagingException e) {
            e.printStackTrace();
            status = "There was an unexpected error.";
        }

        // Output a status message
        response.setContentType("text/html");

        java.io.PrintWriter writer = response.getWriter();

        writer.println("<html><head><title>Status</title></head>");
        writer.println("<body><p>" + status + "</p></body></html>");

        writer.close();
    }
}