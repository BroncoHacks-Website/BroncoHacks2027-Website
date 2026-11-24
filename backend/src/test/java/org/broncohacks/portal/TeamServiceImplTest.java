package org.broncohacks.portal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TeamServiceImplTest {
  @Test
  void joinAndLeaveUseTheSuppliedAuthenticatedUser() {
    TeamRepository teams = mock(TeamRepository.class);
    TeamServiceImpl service = new TeamServiceImpl(teams);
    Team team = new Team("Test team");

    User first = new User("Bob", "hash", "first@test.com");
    User second = new User("Bob", "hash", "second@test.com");
    team.addMember(first);

    when(teams.findByInviteCode(team.getInviteCode())).thenReturn(Optional.of(team));
    when(teams.findByName(team.getTeamName())).thenReturn(Optional.of(team));

    JoinTeamRequest join = new JoinTeamRequest();
    join.setInviteCode(team.getInviteCode());

    assertTrue(service.joinTeam(join, second).isSuccessful());
    assertEquals(List.of(first, second), team.getMembers());

    LeaveTeamRequest leave = new LeaveTeamRequest();
    leave.setTeamName(team.getTeamName());

    assertTrue(service.leaveTeam(leave, second).isSuccessful());
    assertEquals(List.of(first), team.getMembers());

    assertFalse(service.leaveTeam(leave, second).isSuccessful());
    assertEquals(List.of(first), team.getMembers());
  }
}
