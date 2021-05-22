package io.javabrains.ipldashboard;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.javabrains.ipldashboard.controller.TeamController;
import io.javabrains.ipldashboard.model.Team;
import io.javabrains.ipldashboard.repository.TeamRepository;

import static org.assertj.core.api.Assertions.*;
@SpringBootTest
public class TeamControllerTests {

    TeamRepository teamRepository;

    TeamController teamController;

    @Test
    public void testTeamRepository() {   
        teamRepository = mock(TeamRepository.class);     
        Iterable<Team> teams = List.of(new Team("t1", 1),new Team("t1", 1));
        when(teamRepository.findAll()).thenReturn(teams);

        assertThat(teamRepository.findAll())
            .isNotEmpty()
            .contains(new Team("t1", 1));
    }

    @Test
    public void testTeamController(){
        teamController = mock(TeamController.class);
        when(teamController.getTeam(anyString())).thenReturn(new Team());
        assertThat(teamController.getTeam("team x"))
            .isNotNull()
            .isInstanceOf(Team.class);
    }
}
