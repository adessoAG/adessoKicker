package de.adesso.kicker.notification.MatchVerificationRequest;

import de.adesso.kicker.events.MatchCreatedEvent;
import de.adesso.kicker.events.MatchDeclinedEvent;
import de.adesso.kicker.events.MatchVerifiedEvent;
import de.adesso.kicker.match.Match;
import de.adesso.kicker.user.User;
import de.adesso.kicker.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VerifyMatchService {

    private MatchVerificationRequestRepository matchVerificationRequestRepository;

    private UserService userService;

    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public VerifyMatchService(MatchVerificationRequestRepository matchVerificationRequestRepository,
            UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.matchVerificationRequestRepository = matchVerificationRequestRepository;
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void acceptRequest(MatchVerificationRequest matchVerificationRequest) {
        sendMatchVerifiedEvent(matchVerificationRequest.getMatch());
        List<MatchVerificationRequest> requests = getRequestsByMatch(matchVerificationRequest.getMatch());
        for (MatchVerificationRequest request : requests) {
            deleteRequest(request);
        }
    }

    @EventListener
    public void sendRequests(MatchCreatedEvent matchCreatedEvent) {
        Match match = matchCreatedEvent.getMatch();
        User sender = userService.getLoggedInUser();
        List<User> receivers = new ArrayList<>();

        if (match.getWinners().contains(sender)) {
            receivers.addAll(match.getLosers());
        } else {
            receivers.addAll(match.getWinners());
        }
        for (User receiver : receivers) {
            MatchVerificationRequest request = new MatchVerificationRequest(sender, receiver, match);
            saveRequest(request);
        }
    }

    public List<User> declineRequest(MatchVerificationRequest matchVerificationRequest) {
        deleteRequest(matchVerificationRequest);
        sendMatchRequestDeclinedEvent(matchVerificationRequest.getMatch());
        List<MatchVerificationRequest> otherRequests = getRequestsByMatch(matchVerificationRequest.getMatch());
        List<User> usersToInform = new ArrayList<>();

        for (MatchVerificationRequest request : otherRequests) {
            deleteRequest(request);
        }
        for (User player : matchVerificationRequest.getMatch().getPlayers()) {
            if (!userService.getLoggedInUser().equals(player)) {
                usersToInform.add(player);
            }
        }
        return usersToInform;
    }

    private void sendMatchVerifiedEvent(Match match) {
        MatchVerifiedEvent matchVerifiedEvent = new MatchVerifiedEvent(this, match);
        applicationEventPublisher.publishEvent(matchVerifiedEvent);
    }

    private void sendMatchRequestDeclinedEvent(Match match) {
        MatchDeclinedEvent matchDeclinedEvent = new MatchDeclinedEvent(this, match);
        applicationEventPublisher.publishEvent(matchDeclinedEvent);
    }

    public List<MatchVerificationRequest> getRequestsByMatch(Match match) {
        return matchVerificationRequestRepository.getAllByMatch(match);
    }

    public void deleteRequest(MatchVerificationRequest matchVerificationRequest) {
        matchVerificationRequestRepository.delete(matchVerificationRequest);
    }

    public void saveRequest(MatchVerificationRequest matchVerificationRequest) {
        matchVerificationRequestRepository.save(matchVerificationRequest);
    }
}
