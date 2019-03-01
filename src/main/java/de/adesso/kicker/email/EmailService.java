package de.adesso.kicker.email;

import de.adesso.kicker.configurations.EmailConfig;
import de.adesso.kicker.events.match.MatchVerificationSentEvent;
import de.adesso.kicker.match.persistence.Match;
import de.adesso.kicker.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EmailService {

    private EmailConfig emailConfig;

    private final String ACCEPT_URL = "https://localhost/accept/";
    private final String DECLINE_URL = "https://localhost/decline/";

    private static SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

    @Autowired
    public EmailService(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    @EventListener
    public void sendVerification(MatchVerificationSentEvent matchVerificationSentEvent) {
        Match match = matchVerificationSentEvent.getMatchVerificationRequest().getMatch();

        JavaMailSenderImpl mailSender = emailConfig.setMailServerConfig();

        simpleMailMessage.setFrom(match.getTeamAPlayer1().getEmail());
        simpleMailMessage.setTo(matchVerificationSentEvent.getMatchVerificationRequest().getReceiver().getEmail());
        simpleMailMessage.setSubject(setSubject(match));
        simpleMailMessage.setText(verificationText(matchVerificationSentEvent));
        mailSender.send(simpleMailMessage);
    }

    private String setSubject(Match match) {
        return String.format("Verify Match: %s played on %s", match.getMatchId(), match.getDate().toString());
    }

    private String verificationText(MatchVerificationSentEvent matchVerificationSentEvent) {
        String acceptUrl = ACCEPT_URL + matchVerificationSentEvent.getMatchVerificationRequest().getNotificationId();
        String declineUrl = DECLINE_URL + matchVerificationSentEvent.getMatchVerificationRequest().getNotificationId();

        Match match = matchVerificationSentEvent.getMatchVerificationRequest().getMatch();

        String playerA1 = match.getTeamAPlayer1().getFullName();

        User userA2 = match.getTeamAPlayer2();

        String winnerText = getWinner(match);

        if (checkPlayerExist(userA2)) {
            String playerA2 = match.getTeamAPlayer2().getFullName();
            return String.format(
                    "Your recently played Match against %s and %s needs to be verified. Did %s win?\nClick here to verify -> %s to \nClick here decline -> %s",
                    playerA1, playerA2, winnerText, acceptUrl, declineUrl);
        } else {
            return String.format(
                    "Your recently played Match against %s needs to be verified. Did %s win?\nClick here to verify -> %s to \nClick here decline -> %s",
                    playerA1, winnerText, acceptUrl, declineUrl);
        }
    }

    private boolean checkPlayerExist(User user) {
        return !Objects.isNull(user);
    }

    private String getWinner(Match match) {
        return String.format("%s won and %s lost", match.getWinners(), match.getLosers());
    }
}
