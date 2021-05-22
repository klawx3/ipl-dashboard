package io.javabrains.ipldashboard.data;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import io.javabrains.ipldashboard.model.Team;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final EntityManager em;

    @Autowired
    public JobCompletionNotificationListener(EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            Map<String,Team> teamData = new HashMap<>();
            log.info("!!! JOB FINISHED! Time to verify the results");

            em.createQuery("SELECT DISTINCT m.team1 , count(*) FROM Match m GROUP BY m.team1",Object[].class)
                .getResultList()
                .stream()
                .map(e -> new Team((String) e[0], (long) e[1]))
                .forEach(team -> teamData.put(team.getTeamName(), team));

            em.createQuery("SELECT DISTINCT m.team1 , count(*) FROM Match m GROUP BY m.team1",Object[].class)
                .getResultList()
                .stream()
                .forEach(row -> teamData.get((String)row[0]).addMoreTotalMatches((long) row[1]));

            em.createQuery("SELECT m.matchWinner, count(*) FROM Match m GROUP BY m.matchWinner",Object[].class)
                .getResultList()
                .stream()
                .forEach( row -> {
                    var team = teamData.get(row[0]);
                    if(team != null){
                        team.setTotalWins((long) row[1]);
                    }
                });

            teamData.values().forEach(team -> em.persist(team));
        }
    }
}