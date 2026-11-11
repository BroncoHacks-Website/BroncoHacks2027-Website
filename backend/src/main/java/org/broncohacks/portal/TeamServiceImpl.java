package org.broncohacks.portal;

import java.util.List;
import java.util.Optional;
import org.broncohacks.portal.auth.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class TeamServiceImpl implements TeamService {
  private final TeamRepository teamRepository;
  private final UserRepository userRepository;

  public TeamServiceImpl(TeamRepository teamRepository, UserRepository userRepository) {
    this.teamRepository = teamRepository;
    this.userRepository = userRepository;
  }

  public TeamResponse listTeams() {
    List<Team> teamList = teamRepository.findAll();
    TeamResponse response = new TeamResponse(true, "Teams listed successfully", teamList);
    return response;
  }

  public TeamResponse createTeam(CreateTeamRequest request) {
    String newTeamName = request.getTeamName();
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
    TeamResponse response = new TeamResponse(true, "New team created successfully", responseData);
    return response;
  }

  public TeamResponse joinTeam(JoinTeamRequest request) {
    Team teamToJoin = null;
    Optional<User> userToJoin = null;

    // Search teams
    for (Team team : teamRepository.findAll()) {
      if (team.getInviteCode().equals(request.getInviteCode())) {
        teamToJoin = team;
        break;
      }
    }

    // Search users
    userToJoin = userRepository.findByEmail(request.getNewMemberEmail());

    // Invalid invite code
    if (teamToJoin == null) {
      TeamResponse error = new TeamResponse(false, "Invalid invite code", null);
      return error;
    }
    // User doesn't exist
    if (userToJoin.isEmpty()) {
      TeamResponse error = new TeamResponse(false, "User specified does not exist.", null);
      return error;
    }

    // Successful!
    if (teamToJoin.addMember(userToJoin.get())) {
      TeamResponse response =
          new TeamResponse(true, "User added to team successfully", List.of(teamToJoin));
      return response;
    }
    // Team is full
    else {
      TeamResponse error = new TeamResponse(false, "The requested team is full.", null);
      return error;
    }
  }

  public TeamResponse leaveTeam(LeaveTeamRequest request) {
    Team teamToLeave = null;
    Optional<User> userToLeave = null;

    for (Team team : teamRepository.findAll()) {
      if (team.getTeamName().equals(request.getTeamName())) {
        teamToLeave = team;
        break;
      }
    }

    userToLeave = userRepository.findByEmail(request.getEmail());

    if (teamToLeave == null) {
      TeamResponse error = new TeamResponse(false, "Team not found", null);
      return error;
    }
    if (userToLeave.isEmpty()) {
      TeamResponse error = new TeamResponse(false, "User not found", null);
      return error;
    }

    teamToLeave.removeMember(userToLeave.get().getEmail());
    List<Team> responseData = List.of(teamToLeave);
    TeamResponse response = new TeamResponse(true, "Left team successfully", responseData);
    return response;
  }
}
