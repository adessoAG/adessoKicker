package de.adesso.adessoKicker.objects;

public class Team {

	long matchId;
	Datetime date;
	int teamAPoints;
	int teamBPoints;
	Team teamA;
	Team teamB;
	Team winner;
	String kicker;
	Tournament tournament;
	
	public Team() {
		teamA = new Team();
		teamB = new Team();
		tournament = new Tournament();
	}
	
	public Team(long matchId, Datetime date, int teamAPoints, int teamBPoints, Team winner, String kicker,
			Tournament tournament) {
		super();
		this.matchId = matchId;
		this.date = date;
		this.teamAPoints = teamAPoints;
		this.teamBPoints = teamBPoints;
		this.winner = winner;
		this.kicker = kicker;
		this.tournament = tournament;
	}
	public long getMatchId() {
		return matchId;
	}
	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}
	public Datetime getDate() {
		return date;
	}
	public void setDate(Datetime date) {
		this.date = date;
	}
	public int getTeamAPoints() {
		return teamAPoints;
	}
	public void setTeamAPoints(int teamAPoints) {
		this.teamAPoints = teamAPoints;
	}
	public int getTeamBPoints() {
		return teamBPoints;
	}
	public void setTeamBPoints(int teamBPoints) {
		this.teamBPoints = teamBPoints;
	}
	public Team getWinner() {
		return winner;
	}
	public void setWinner(Team winner) {
		this.winner = winner;
	}
	public String getKicker() {
		return kicker;
	}
	public void setKicker(String kicker) {
		this.kicker = kicker;
	}
	public Tournament getTournament() {
		return tournament;
	}
	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}
	
}