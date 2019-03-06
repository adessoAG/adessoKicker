package de.adesso.kicker.notification.matchverificationrequest;

import de.adesso.kicker.events.MatchCreatedEventDummy;
import de.adesso.kicker.events.match.MatchDeclinedEvent;
import de.adesso.kicker.events.match.MatchVerifiedEvent;
import de.adesso.kicker.match.persistence.Match;
import de.adesso.kicker.match.MatchDummy;
import de.adesso.kicker.notification.matchverificationrequest.persistence.MatchVerificationRequest;
import de.adesso.kicker.notification.matchverificationrequest.persistence.MatchVerificationRequestRepository;
import de.adesso.kicker.notification.matchverificationrequest.service.VerifyMatchService;
import de.adesso.kicker.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

public class VerifyMatchServiceTest {

    @Mock
    MatchVerificationRequestRepository matchVerificationRequestRepository;

    @Mock
    UserService userService;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    VerifyMatchService verifyMatchService;

    static MatchVerificationRequest createMatchVerification() {
        return MatchVerificationRequestDummy.matchVerificationRequest();
    }

    static List<MatchVerificationRequest> createMatchVerificationList() {
        return Collections.singletonList(MatchVerificationRequestDummy.matchVerificationRequest());
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("All requests that belong to the match should be deleted")
    void verifyMatchAndDeleteAllRequests() {
        // given
        var matchVerification = createMatchVerification();
        when(matchVerificationRequestRepository.getAllByMatch(matchVerification.getMatch()))
                .thenReturn(createMatchVerificationList());

        // when
        verifyMatchService.acceptRequest(matchVerification);

        // then
        verify(matchVerificationRequestRepository, times(createMatchVerificationList().size()))
                .delete(any(MatchVerificationRequest.class));
        verify(applicationEventPublisher, times(1)).publishEvent(any(MatchVerifiedEvent.class));
    }

    @Test
    @DisplayName("All players of the opponent team should receive a verification request")
    void sendRequestsToOpponents() {
        // given
        var match = MatchDummy.match();
        var matchCreatedEvent = MatchCreatedEventDummy.matchCreatedEvent(match);
        when(userService.getLoggedInUser()).thenReturn(match.getTeamAPlayer1());

        // when
        verifyMatchService.sendRequests(matchCreatedEvent);

        // then
        verify(matchVerificationRequestRepository, times(2)).save(any(MatchVerificationRequest.class));
    }

    @Test
    void sendRequestToOpponentsAsLoser() {
        var match = MatchDummy.matchTeamBWon();
        var matchCreatedEvent = MatchCreatedEventDummy.matchCreatedEvent(match);
        given(userService.getLoggedInUser()).willReturn(match.getTeamAPlayer1());

        // when
        verifyMatchService.sendRequests(matchCreatedEvent);

        // then
        then(matchVerificationRequestRepository).should(times(1)).save(any(MatchVerificationRequest.class));
    }

    @Test
    @DisplayName("The match of the request and all requests that belong to that match should be deleted "
            + "and every player that is not the current user should be returned in a list")
    void deleteMatchAndRequestsAndReturnUsersToInform() {
        // given
        var matchVerification = createMatchVerification();
        when(userService.getLoggedInUser()).thenReturn(matchVerification.getMatch().getTeamAPlayer1());
        when(matchVerificationRequestRepository.getAllByMatch(any(Match.class)))
                .thenReturn(createMatchVerificationList());

        // when
        verifyMatchService.declineRequest(matchVerification);

        // then
        verify(applicationEventPublisher, times(1)).publishEvent(any(MatchDeclinedEvent.class));
        verify(matchVerificationRequestRepository, times(2)).delete(any(MatchVerificationRequest.class));
        assertEquals(3, verifyMatchService.declineRequest(matchVerification).size());
    }
}