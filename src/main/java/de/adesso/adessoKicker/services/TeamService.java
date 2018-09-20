package de.adesso.adessoKicker.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.JpaQueryCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.adesso.adessoKicker.objects.Team;
import de.adesso.adessoKicker.repositories.TeamRepository;

@Service
public class TeamService {

	@Autowired
	TeamRepository teamRepository;
	
	@Autowired
	private final EntityManager entityManager;
	
	@Autowired
	public TeamService(EntityManager entityManager)
	{
		super();
		this.entityManager = entityManager;
	}
	
	public void initializeTeamSearch() {
		try {
			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Transactional
	public List<Team> teamSearch(String searchTerm) {
		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Team.class).get();
	
		Query luceneQuery =	queryBuilder.keyword().fuzzy().withEditDistanceUpTo(1).onField("teamName").matching(searchTerm).createQuery();
		javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Team.class);
		
	
	//execute search 
	List<Team> TeamList = null;
	try {
		TeamList = jpaQuery.getResultList();
	} catch (NoResultException nre) {
		// do nothing
	
	}
	
		return TeamList;
	}
	private List<Team> teams = new ArrayList<>();
	
	public List<Team> getAllTeams()
	{
		teams = new ArrayList<Team>();
		teamRepository.findAll().forEach(teams::add);
		return teams;
	}
	
	public Team getOneTeam(long id)
	{
		return teamRepository.findById(id).get();
		
	}
	
	public void addTeam(Team team)
	{
		teamRepository.save(team);
	}
	
	public void deleteTeam(long id)
	{
		teamRepository.deleteById(id);
	}
	
	public void updateTeam(Team team, long id)
	{
		teamRepository.save(teamRepository.findById(id).get());
	}
	
	public Team findByTeamName(String teamName)
	{
		return teamRepository.findByTeamName(teamName);
	}

	public Team findTeamByUserId(long id) {

	    return new Team();
    }
	
}