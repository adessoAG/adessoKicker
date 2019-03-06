package de.adesso.kicker.user;

import de.adesso.kicker.ranking.service.RankingService;
import de.adesso.kicker.user.controller.UserController;
import de.adesso.kicker.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, secure = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RankingService rankingService;

    @Test
    @WithMockUser
    void whenUserLoggedInReturnUser() throws Exception {
        // given
        var user = UserDummy.defaultUser();
        given(userService.getLoggedInUser()).willReturn(user);
        given(rankingService.getPositionOfPlayer(user.getRanking())).willReturn(1);

        // when
        var result = mockMvc.perform(get("/users/you"));

        // then
        result.andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("rankingPosition", 1));
    }

    @Test
    @WithMockUser
    void whenUserExistsReturnUser() throws Exception {
        // given
        var user = UserDummy.defaultUser();
        given(userService.getUserById(user.getUserId())).willReturn(user);
        given(rankingService.getPositionOfPlayer(user.getRanking())).willReturn(1);

        // when
        var result = mockMvc.perform(get("/users/u/{id}", user.getUserId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("rankingPosition", 1));
    }

}
