package org.foi.nwtis.dkermek.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class webmail extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String command = request.getParameter("command");

        try {
            if ("login".equalsIgnoreCase(command)) {
                doLogin(request, response);
            } else if ("list".equalsIgnoreCase(command)) {
                doList(request, response);
            } else if ("read".equalsIgnoreCase(command)) {
                doRead(request, response);
            } else if ("reply".equalsIgnoreCase(command)) {
                doReply(request, response);
            } else if ("send".equalsIgnoreCase(command)) {
                doSend(request, response);
            } else if ("logout".equalsIgnoreCase(command)) {
                doLogout(request, response);
            }
        } catch (MessagingException e) {
            throw new ServletException("MessagingException: " + e);
        }
    }
    private String defaultFrom;
    private Session session;
    private Store store;
    private Folder folder;

    private void doLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MessagingException {
        String smtp = request.getParameter("smtp");
        String pop3 = request.getParameter("pop3");
        String user = request.getParameter("user");
        String pass = request.getParameter("pass");

        // Save a default From address
        defaultFrom = user + "@" + pop3;

        // Start the session
        java.util.Properties properties = System.getProperties();
        properties.put("mail.smtp.host", smtp);
        session = Session.getInstance(properties, null);

        // Connect to the store
        store = session.getStore("pop3");
        store.connect(pop3, user, pass);

        // Open the INBOX folder
        folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);

        // List the messages
        doList(request, response);
    }
    private Message[] messages = null;

    private void doList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MessagingException {
        messages = folder.getMessages();

        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        // Start a table and print the header
        writer.println(
                "<html><head><title>list</title></head>"
                + "<body><table border=\"1\">"
                + "<tr>"
                + "<th>Date</th>"
                + "<th>From</th>"
                + "<th>Subject</th>"
                + "</tr>");

        // Print each message
        for (int i = 0; i < messages.length; ++i) {
            writer.println(
                    "<tr>"
                    + "<td>" + messages[i].getSentDate() + "</td>"
                    + "<td>" + messages[i].getFrom()[0] + "</td>"
                    + "<td><a href='" + request.getRequestURI()
                    + "?command=read&message=" + i + "'>"
                    + messages[i].getSubject() + "</a></td>"
                    + "</tr>");
        }

        // End the table
        writer.println("</table>");

        // Add a logout link
        writer.println("<p><a href='" + request.getRequestURI()
                + "?command=logout'>logout</a></p>");

        // End the page
        writer.println("</body></html>");

        writer.close();
    }

    private void doRead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MessagingException {

        int num = Integer.parseInt(request.getParameter("message"));

        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        MimeMessage message = (MimeMessage) messages[num];

        writer.println("<html><head><title>read: "
                + message.getSubject()
                + "</title></head><body>");

        // Print some select headers
        writer.println("<table border=\"1\">"
                + "<tr><th>Date: </th><td>"
                + message.getSentDate()
                + "</td></tr><tr><th>From: </th><td>"
                + message.getFrom()[0]
                + "</td></tr><tr><th>To: </th><td>"
                + message.getRecipients(
                Message.RecipientType.TO)[0]
                + "</td></tr><tr><th>Subject: </th><td>"
                + message.getSubject()
                + "</td></tr><tr><td colspan=\"2\"><p>");

        ContentType ct = new ContentType(message.getContentType());

        // If the text is in HTML, just print it
        if ("text/html".equalsIgnoreCase(ct.getBaseType())) {
            BufferedReader reader =
                    new BufferedReader(
                    new InputStreamReader(
                    message.getInputStream()));

            String s;

            while ((s = reader.readLine()) != null) {
                writer.println(s);
            }
        } else {
            Object o = message.getContent();

            // If the text is plain, just print it
            if (o instanceof String) {
                writer.println("<pre>" + o + "</pre>");
            } else {
                // Print the content type
                writer.println(message.getContentType());

                // If it is a multipart, list the parts
                if (o instanceof MimeMultipart) {
                    listParts((MimeMultipart) o, writer);
                }
            }
        }

        // End the message
        writer.println("</p></td></tr></table>");

        // Print a link to reply
        writer.println("<p><a href='"
                + request.getRequestURI()
                + "?command=reply&message=" + num + "'>reply</a> ");


        // Print a link to logout
        writer.println("<a href='"
                + request.getRequestURI()
                + "?command=logout'>logout</a></p>");

        // End the page
        writer.println("</body></html>");


        writer.close();
    }

    private void listParts(MimeMultipart mp, PrintWriter writer)
            throws MessagingException {

        writer.println("<ul>");

        for (int i = 0; i < mp.getCount(); ++i) {
            MimeBodyPart bp = (MimeBodyPart) mp.getBodyPart(i);
            writer.println("<li>" + bp.getContentType());
        }

        writer.println("</ul>");
    }

    private void doReply(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MessagingException {

        // Get the message we are replying to
        int num = Integer.parseInt(request.getParameter("message"));

        // Create a new messgage
        MimeMessage message = (MimeMessage) messages[num];

        String to = ((InternetAddress) message.getFrom()[0]).getAddress();

        String subject = "Re: " + message.getSubject();

        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println("<html><head><title>reply</title></head><body>");
        writer.println("<form action='" + request.getRequestURI()
                + "?command=send' method='get'>");
        writer.println("<input type='hidden' name='command' value='send'>");
        writer.println("From: <input name='from' value='"
                + defaultFrom + "' type='text'><br>");
        writer.println("To: <input name='to' value='" + to
                + "' type='text'><br>");
        writer.println("Cc: <input name='cc' type='text'><br>");
        writer.println("Bcc: <input name='bcc' type='text'><br>");
        writer.println("Subject: <input name='subject' value='"
                + subject + "' type='text'><br>");
        writer.println("<textarea name='text' cols='32' rows='8'></textarea><br>");


        // Print the Submit and Reset buttons
        writer.println("<input type='submit'><input type='reset'>");

        // End the page
        writer.println("</body></html>");

        writer.close();
    }

    private void doSend(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MessagingException {

        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String cc = request.getParameter("cc");
        String bcc = request.getParameter("bcc");
        String subject = request.getParameter("subject");
        String text = request.getParameter("text");

        // Construct a message
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(from));

        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
        message.setRecipients(Message.RecipientType.CC,
                InternetAddress.parse(cc));
        message.setRecipients(Message.RecipientType.BCC,
                InternetAddress.parse(bcc));

        message.setSubject(subject);

        message.setText(text);

        // Send the messge
        Transport.send(message);

        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        writer.println("<html><head><title>send</title></head><body>");
        writer.println("<p>Your message was sent.</p>");
        writer.println("</body></html>");
        writer.close();
    }

    private void doLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, MessagingException {

        // Close the folder and store
        folder.close(false);
        store.close();

        // Say goodbye
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        writer.println("<html><head><title>logout</title></head>");
        writer.println("<body><p>Goodbye.</p></body></html>");

        writer.close();
    }
}