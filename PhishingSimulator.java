package org.example;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

public class PhishingSimulator extends JFrame {

    // GUI Components
    private JTextField txtSmtpServer, txtPort, txtUsername, txtFrom, txtRecipient;
    private JPasswordField txtPassword;
    private JButton btnSend;
    private JTextArea txtLog;

    // HTML email template
    private static final String HTML_TEMPLATE = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Urgent: Account Suspension Notice</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: Arial, sans-serif;\n" +
            "            background-color: #f4f4f4;\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "        }\n" +
            "        .email-container {\n" +
            "            max-width: 600px;\n" +
            "            margin: 20px auto;\n" +
            "            background: #ffffff;\n" +
            "            padding: 20px;\n" +
            "            border-radius: 8px;\n" +
            "            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);\n" +
            "        }\n" +
            "        h2 {\n" +
            "            color: #d9534f;\n" +
            "            text-align: center;\n" +
            "        }\n" +
            "        p {\n" +
            "            font-size: 16px;\n" +
            "            color: #333;\n" +
            "            line-height: 1.6;\n" +
            "        }\n" +
            "        .warning {\n" +
            "            color: red;\n" +
            "            font-weight: bold;\n" +
            "        }\n" +
            "        .button {\n" +
            "            display: block;\n" +
            "            width: 80%;\n" +
            "            text-align: center;\n" +
            "            background: #d9534f;\n" +
            "            color: #ffffff;\n" +
            "            padding: 15px;\n" +
            "            text-decoration: none;\n" +
            "            font-size: 18px;\n" +
            "            font-weight: bold;\n" +
            "            border-radius: 5px;\n" +
            "            margin: 20px auto;\n" +
            "            border: none;\n" +
            "            cursor: pointer;\n" +
            "        }\n" +
            "        .button:hover {\n" +
            "            background: #c9302c;\n" +
            "        }\n" +
            "        .popup {\n" +
            "            display: none;\n" +
            "            position: fixed;\n" +
            "            top: 50%;\n" +
            "            left: 50%;\n" +
            "            transform: translate(-50%, -50%);\n" +
            "            background: #fff;\n" +
            "            padding: 20px;\n" +
            "            border-radius: 8px;\n" +
            "            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.2);\n" +
            "            text-align: center;\n" +
            "            width: 80%;\n" +
            "            max-width: 400px;\n" +
            "        }\n" +
            "        .popup p {\n" +
            "            font-size: 16px;\n" +
            "            color: #333;\n" +
            "        }\n" +
            "        .popup button {\n" +
            "            background: #d9534f;\n" +
            "            color: white;\n" +
            "            border: none;\n" +
            "            padding: 10px 20px;\n" +
            "            border-radius: 5px;\n" +
            "            cursor: pointer;\n" +
            "            margin-top: 10px;\n" +
            "        }\n" +
            "        .popup button:hover {\n" +
            "            background: #c9302c;\n" +
            "        }\n" +
            "        .footer {\n" +
            "            font-size: 12px;\n" +
            "            color: #777;\n" +
            "            text-align: center;\n" +
            "            margin-top: 20px;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"email-container\">\n" +
            "        <h2>⚠ Urgent: Your Account Will Be Suspended!</h2>\n" +
            "        <p>Dear <strong>[Recipient's Name]</strong>,</p>\n" +
            "        <p>We detected <span class=\"warning\">unusual login activity</span> on your account. To prevent unauthorized access, we have temporarily restricted it.</p>\n" +
            "        <p><strong>You must verify your identity within 24 hours to avoid permanent suspension.</strong></p>\n" +
            "        \n" +
            "        <!-- Fake button with JS -->\n" +
            "        <button class=\"button\" onclick=\"showPopup()\">Verify My Account</button>\n" +
            "        \n" +
            "        <p><strong>What You Need to Do:</strong></p>\n" +
            "        <ul>\n" +
            "            <li>Click the button above.</li>\n" +
            "            <li>Log in with your username and password.</li>\n" +
            "            <li>Confirm your personal details for verification.</li>\n" +
            "        </ul>\n" +
            "        <p class=\"warning\">Do not ignore this warning. Failure to act will result in account termination.</p>\n" +
            "        <p>Sincerely,</p>\n" +
            "        <p>The [Bank/Service] Security Team</p>\n" +
            "        <p class=\"footer\">*This is an automated message. Replies are not monitored.*</p>\n" +
            "    </div>\n" +
            "\n" +
            "    <!-- Popup Message -->\n" +
            "    <div class=\"popup\" id=\"popup\">\n" +
            "        <p>This phishing attack worked because it created a sense of urgency and fear, making the victim believe their account was at risk of suspension. The email appeared to come from an official source, using authority and trust to pressure the recipient into clicking the link. The fake verification process tricked the user into entering their credentials, unknowingly handing them over to the attacker. By disguising the malicious link and using professional formatting, the email seemed legitimate, lowering the victim’s suspicion and increasing the chances of success.</p>\n" +
            "        <button onclick=\"closePopup()\">Close</button>\n" +
            "    </div>\n" +
            "\n" +
            "    <script>\n" +
            "        function showPopup() {\n" +
            "            document.getElementById(\"popup\").style.display = \"block\";\n" +
            "        }\n" +
            "\n" +
            "        function closePopup() {\n" +
            "            document.getElementById(\"popup\").style.display = \"none\";\n" +
            "        }\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>\n";

    public PhishingSimulator() {
        setTitle("Phishing Simulation Email Sender");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("SMTP Server:"), gbc);

        gbc.gridx = 1;
        txtSmtpServer = new JTextField("smtp.example.com", 20);
        panel.add(txtSmtpServer, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Port:"), gbc);

        gbc.gridx = 1;
        txtPort = new JTextField("587", 20);
        panel.add(txtPort, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField("your_username", 20);
        panel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        panel.add(txtPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("From Email:"), gbc);

        gbc.gridx = 1;
        txtFrom = new JTextField("sender@example.com", 20);
        panel.add(txtFrom, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Recipient Email:"), gbc);

        gbc.gridx = 1;
        txtRecipient = new JTextField("recipient@example.com", 20);
        panel.add(txtRecipient, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        btnSend = new JButton("Send Email");
        panel.add(btnSend, gbc);

        txtLog = new JTextArea(10, 50);
        txtLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtLog);

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendEmail();
            }
        });

        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void sendEmail() {
        String smtpServer = txtSmtpServer.getText().trim();
        String port = txtPort.getText().trim();
        final String username = txtUsername.getText().trim();
        final String password = new String(txtPassword.getPassword());
        String fromEmail = txtFrom.getText().trim();
        String recipientEmail = txtRecipient.getText().trim();

        // Extract recipient's name from email (everything before @)
        String recipientName = recipientEmail.split("@")[0];

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Urgent: Your Account Will Be Suspended!");

            // Replace placeholder with recipient name
            String personalizedHtml = HTML_TEMPLATE.replace("[Recipient's Name]", recipientName);

            message.setContent(personalizedHtml, "text/html; charset=UTF-8");

            Transport.send(message);

            txtLog.append("Email sent successfully to " + recipientEmail + " (User: " + recipientName + ")\n");
        } catch (MessagingException ex) {
            txtLog.append("Error sending email: " + ex.getMessage() + "\n");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhishingSimulator().setVisible(true));
    }
}
