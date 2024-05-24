package dev.roy.coinkeeper.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;

    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            mimeMessage.setFrom(new InternetAddress("Coin-Keeper@coin-keeper.dev"));
            mimeMessage.setRecipients(Message.RecipientType.TO, to);
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(content, "text/html; charset=utf-8");
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("Error in sending email: {}", e.getMessage());
        }
    }

    @Async
    public void sendOTPViaEmail(String name, String email, Integer otp) {
        String sub = "Welcome to Coin Keeper";
        String htmlContent =
                """
                            <div
                              style="
                                background-color: azure;
                                padding: 1rem 1.5rem;
                                border-radius: 0.5rem;
                                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                              "
                            >
                              <header>
                                <h1 style="display: flex; flex-wrap: wrap">
                                  <span style="margin-right: 0.5rem">Welcome to</span>
                                  <div style="display: flex; flex-wrap: wrap">
                                    <span style="font-weight: 800; color: #166534">Coin</span>
                                    <span style="font-weight: 800; color: #eab308">Keeper</span>
                                  </div>
                                </h1>
                              </header>
                              <h3>Hello %s,</h3>
                              <p style="line-height: 40px;">
                                Please use the OTP
                                <span
                                  style="
                                    background-color: white;
                                    padding: 5px 10px;
                                    font-size: 24px;
                                    font-weight: 600;
                                    letter-spacing: 0.15em;
                                    border-radius: 5px;
                                    box-shadow: 0px 1px 1px 2px rgba(0, 0, 0, 0.5);
                                    box-shadow: rgba(100, 100, 111, 0.2) 0px 7px 29px 0px;
                                  "
                                  >%d</span
                                >
                                for completing the process. (<em style="color: red;"
                                  >Valid for 15 minutes</em
                                >
                                ⏱️)
                              </p>
                              <br />
                              <div>
                                <p><i>Thanks,</i>😊</p>
                                <p>Team CoinKeeper</p>
                              </div>
                            </div>
                        """.formatted(name, otp);
        sendEmail(email, sub, htmlContent);
        log.info("Sending OTP via email for user: {}", email);
    }
}
