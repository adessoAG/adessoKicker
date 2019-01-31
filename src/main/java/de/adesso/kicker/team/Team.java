package de.adesso.kicker.team;

import de.adesso.kicker.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String teamId;

    @NotNull
    @Size(min = 1, max = 30, message = "Der Teamname muss zwischen 1 - 30 Zeichen lang sein.")
    private String teamName;

    private long teamWins;

    private long teamLosses;

    @OneToOne(targetEntity = User.class, cascade = CascadeType.ALL)
    private User playerA;

    @OneToOne(targetEntity = User.class, cascade = CascadeType.ALL)
    private User playerB;

    public Team() {
    }

    public Team(String teamName, User playerA, User playerB) {

        this.teamName = teamName;
        this.playerA = playerA;
        this.playerB = playerB;
        this.teamWins = 0;
        this.teamLosses = 0;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public long getTeamWins() {
        return teamWins;
    }

    public void setTeamWins(long teamWins) {
        this.teamWins = teamWins;
    }

    public long getTeamLosses() {
        return teamLosses;
    }

    public void setTeamLosses(long teamLosses) {
        this.teamLosses = teamLosses;
    }

    public User getPlayerA() {
        return playerA;
    }

    public void setPlayerA(User playerA) {
        this.playerA = playerA;
    }

    public User getPlayerB() {
        return playerB;
    }

    public void setPlayerB(User playerB) {
        this.playerB = playerB;
    }

    @Override
    public String toString() {
        return "Team{" + "teamId=" + teamId + ", teamName='" + teamName + '\'' + ", teamWins=" + teamWins
                + ", teamLosses=" + teamLosses + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Team team = (Team) o;
        return teamId == team.teamId && teamWins == team.teamWins && teamLosses == team.teamLosses
                && Objects.equals(teamName, team.teamName) && Objects.equals(playerA, team.playerA)
                && Objects.equals(playerB, team.playerB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamId, teamName, teamWins, teamLosses, playerA, playerB);
    }
}
