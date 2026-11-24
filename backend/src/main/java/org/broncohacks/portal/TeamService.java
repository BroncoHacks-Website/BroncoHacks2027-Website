package org.broncohacks.portal;

public interface TeamService {
  TeamResponse listTeams();

  TeamResponse createTeam(CreateTeamRequest request);

  TeamResponse joinTeam(JoinTeamRequest request, User user);

  TeamResponse leaveTeam(LeaveTeamRequest request, User user);
}
