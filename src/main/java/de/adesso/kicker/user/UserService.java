package de.adesso.kicker.user;

import de.adesso.kicker.ranking.Ranking;
import de.adesso.kicker.user.exception.UserNotFoundException;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        var users = new ArrayList<User>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public User getLoggedInUser() {
        var principal = getPrincipal();
        User user;
        try {
            user = getUserById(principal.getName());
        } catch (UserNotFoundException e) {
            user = createUser();
        }
        return user;
    }

    private KeycloakPrincipal getPrincipal() {
        return (KeycloakPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private User createUser() {
        var userAccessToken = getAccessToken();
        var userId = userAccessToken.getPreferredUsername();
        var firstName = userAccessToken.getGivenName();
        var lastName = userAccessToken.getFamilyName();
        var email = userAccessToken.getEmail();
        User user = new User(userId, firstName, lastName, email, new Ranking());
        return saveUser(user);
    }

    private AccessToken getAccessToken() {
        var simpleKeycloakAccount = (SimpleKeycloakAccount) SecurityContextHolder.getContext()
                .getAuthentication()
                .getDetails();
        return simpleKeycloakAccount.getKeycloakSecurityContext().getToken();
    }

    private User saveUser(User user) {
        return userRepository.save(user);
    }
}
