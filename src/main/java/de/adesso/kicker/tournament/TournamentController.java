package de.adesso.kicker.tournament;

import de.adesso.kicker.team.TeamService;
import de.adesso.kicker.tournament.lastmanstanding.LastManStandingController;
import de.adesso.kicker.tournament.singleelimination.SingleEliminationController;
import de.adesso.kicker.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private SingleEliminationController singleEliminationController;

    @Autowired
    private LastManStandingController lastManStandingController;

    @Autowired
    private UserService userService;

    public TournamentController() {
    }

    @GetMapping("/tournaments/create")
    public ModelAndView chooseFormat() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("tournamentFormats", TournamentFormats.values());
        modelAndView.setViewName("tournament/create");
        return modelAndView;
    }

    @PostMapping("/tournaments/create")
    public ModelAndView getTournamentFormat(@RequestParam("tournamentFormat") String tournamentFormat) {
        ModelAndView modelAndView = new ModelAndView();
        switch (tournamentFormat) {
        case "SINGLEELIMINATION":
            modelAndView.setViewName("redirect:/tournaments/create/singleelimination");
            return modelAndView;

        case "LASTMANSTANDING":
            modelAndView.setViewName("redirect:/tournaments/create/lastmanstanding");
            return modelAndView;

        default:
            modelAndView.addObject("tournamentFormats", TournamentFormats.values());
            modelAndView.setViewName("tournament/create");
            return modelAndView;
        }
    }

    @GetMapping("/tournaments")
    public ModelAndView showTournamentList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("tournaments", tournamentService.getAllTournaments());
        modelAndView.setViewName("tournament/list");
        return modelAndView;
    }

    @GetMapping("/tournaments/{tournamentId}")
    public ModelAndView tournamentPage(@PathVariable("tournamentId") long id) {
        ModelAndView modelAndView = new ModelAndView();
        Tournament tournament = tournamentService.getTournamentById(id);
        String format = tournament.getFormat();
        switch (format) {

        case "SINGLEELIMINATION":
            System.out.println("SE: " + tournament.getFormat());
            return singleEliminationController.getSingleEliminationPage(tournament);

        case "LASTMANSTANDING":
            System.out.println("LMS: " + tournament.getFormat());
            return lastManStandingController.getLastManStandingPage(tournament);

        default:
            modelAndView.setViewName("redirect:/tournaments/list");
            return modelAndView;
        }
    }

    @GetMapping("tournaments/{tournamentId}/join")
    public ModelAndView showAddTeam(@PathVariable("tournamentId") long id) {
        ModelAndView modelAndView = new ModelAndView();
        Tournament tournament = tournamentService.getTournamentById(id);
        String format = tournament.getFormat();
        switch (format) {

        case ("SINGLEELIMINATION"):
            modelAndView = singleEliminationController.joinTournament(tournament);
            return modelAndView;

        case ("LASTMANSTANDING"):
            modelAndView = lastManStandingController.joinTournament(tournament);
        }
        return modelAndView;
    }

    @PostMapping("tournaments/{tournamentId}/join")
    public ModelAndView addToTournament(@PathVariable("tournamentId") long id, Long teamId, Long userId) {
        ModelAndView modelAndView = new ModelAndView();
        Tournament tournament = tournamentService.getTournamentById(id);
        String format = tournament.getFormat();
        switch (format) {
        case ("SINGLEELIMINATION"):
            return (singleEliminationController.addTeamToTournament(tournament, teamService.findTeamByTeamId(teamId)));

        case ("LASTMANSTANDING"):
            return lastManStandingController.addPlayerToTournament(tournament, userService.getUserById(userId));
        default:
            return modelAndView;
        }
    }

    @GetMapping("tournaments/{tournamentId}/tree")
    public ModelAndView showTournamentTree(@PathVariable("tournamentId") long id) {

        ModelAndView modelAndView = new ModelAndView();
        Tournament tournament = tournamentService.getTournamentById(id);
        String format = tournament.getFormat();
        switch (format) {
        case ("SINGLEELIMINATION"):
            modelAndView = singleEliminationController.showTree(tournament);
            return modelAndView;

        case ("LASTMANSTANDING"):
            modelAndView = lastManStandingController.showLivesMap(tournament);
            return modelAndView;

        default:
            modelAndView.setViewName("redirect:/tournaments");
            return modelAndView;
        }
    }

}
