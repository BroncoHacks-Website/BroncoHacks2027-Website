package org.broncohacks.portal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.broncohacks.portal.auth.UserRepository;
import org.junit.jupiter.api.Test;

class TeamServiceImplTest {
  @Test
  void joinAndLeaveIdentifyMembersByEmailNotSharedName() {
    TeamRepository teams = mock(TeamRepository.class);
    UserRepository users = mock(UserRepository.class);
    TeamServiceImpl service = new TeamServiceImpl(teams, users);
    Team team = new Team("Test team");
    User first = new User("Bob", "hash", "first@test.com");
    User second = new User("Bob", "hash", "second@test.com");
    team.addMember(first);
    when(teams.findAll()).thenReturn(List.of(team));
    when(users.findByEmail("second@test.com")).thenReturn(Optional.of(second));

    JoinTeamRequest joinReq = new JoinTeamRequest();
    joinReq.setInviteCode(team.getInviteCode());
    joinReq.setNewMemberEmail("second@test.com");
    assertTrue(service.joinTeam(joinReq).isSuccessful());
    assertEquals(List.of(first, second), team.getMembers());

    LeaveTeamRequest leaveReq = new LeaveTeamRequest();
    leaveReq.setTeamName(team.getTeamName());
    leaveReq.setEmail("second@test.com");
    assertTrue(service.leaveTeam(leaveReq).isSuccessful());
    assertEquals(List.of(first), team.getMembers());
    assertFalse(team.removeMember("missing@test.com"));
    verify(users, times(2)).findByEmail("second@test.com");
  }
}
