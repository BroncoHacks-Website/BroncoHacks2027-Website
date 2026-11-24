package org.broncohacks.portal;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TeamServiceImpl implements TeamService {
  private final TeamRepository teamRepository;

  public TeamServiceImpl(TeamRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  public TeamResponse listTeams() {
    List<Team> teamList = teamRepository.findAll();
    TeamResponse response = new TeamResponse(true, "Teams listed successfully", teamList);
    return response;
  }

  public TeamResponse createTeam(CreateTeamRequest req) {
    String newTeamName = req.getTeamName();
    if (newTeamName.isBlank()) {
      TeamResponse error = new TeamResponse(false, "Team name cannot be blank", null);
      return error;
    }

    for (Team team : teamRepository.findAll()) {
      if (team.getTeamName().equals(newTeamName)) {
        TeamResponse error = new TeamResponse(false, "Team name already in use", null);
        return error;
      }
    }

    Team newTeam = new Team(newTeamName);
    teamRepository.save(newTeam);
    List<Team> responseData = List.of(newTeam);
    TeamResponse res = new TeamResponse(true, "New team created successfully", responseData);
    return res;
  }

  public TeamResponse joinTeam(JoinTeamRequest req, User user) {
    Optional<Team> res = teamRepository.findByInviteCode(req.getInviteCode());
    if (res.isEmpty()) {
      return new TeamResponse(false, "Invalid invite code", null);
    }

    Team team = res.get();
    if (!team.addMember(user)) {
      return new TeamResponse(false, "The requested team is full.", null);
    }

    return new TeamResponse(true, "User added to team successfully", List.of(team));
  }

  public TeamResponse leaveTeam(LeaveTeamRequest req, User user) {
    Optional<Team> res = teamRepository.findByName(req.getTeamName());
    if (res.isEmpty()) {
      return new TeamResponse(false, "Team not found", null);
    }

    Team team = res.get();
    if (!team.removeMember(user.getEmail())) {
      return new TeamResponse(false, "You are not a member of this team", null);
    }

    return new TeamResponse(true, "Left team successfully", List.of(team));
  }
}
