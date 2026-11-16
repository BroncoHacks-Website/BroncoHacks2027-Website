package org.broncohacks.portal;

import org.broncohacks.portal.auth.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService{
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamProperties teamProperties;

    public TeamServiceImpl(TeamRepository teamRepository, UserRepository userRepository, TeamProperties teamProperties) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamProperties = teamProperties;
    }

    public TeamResponse listTeams() {

        boolean successful = true;
        String message = "Teams listed successfully";
        List<Team> teamList = teamRepository.findAll();
        for (Team team : teamList) {
            team.setComplete(team.size() >= teamProperties.minSize());
        }
        TeamResponse response = new TeamResponse(successful, message, teamList);
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
        newTeam.setComplete(newTeam.size() >= teamProperties.minSize());
        teamRepository.save(newTeam);
        List<Team> responseData = List.of(newTeam);
        TeamResponse response = new TeamResponse(true, "New team created successfully", responseData);
        return response;
    }

    public TeamResponse joinTeam( JoinTeamRequest request) {
        Team teamToJoin = null;
        Optional<User> userToJoin = null;

        //Search teams
        for (Team team : teamRepository.findAll()) {
            if (team.getInviteCode().equals(request.getInviteCode())) {
                teamToJoin = team;
                break;
            }
        }

        //Search users
        userToJoin = userRepository.findByUsername(request.getNewMemberName());

        //Invalid invite code
        if (teamToJoin == null) {
            TeamResponse error = new TeamResponse(false, "Invalid invite code", null);
            return error;
        }
        //User doesn't exist
        if (userToJoin.isEmpty()) {
            TeamResponse error = new TeamResponse(false, "User specified does not exist.", null);
            return error;
        }

        //Successful!
        if (teamToJoin.addMember(userToJoin.get(), teamProperties.maxSize())) {

            teamToJoin.setComplete(teamToJoin.size() >= teamProperties.minSize());

            TeamResponse response = new TeamResponse(true, "User added to team successfully",
                    List.of(teamToJoin));
            return response;
        }
        //Team is full
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

        userToLeave = userRepository.findByUsername(request.getUserName());

        if (teamToLeave == null) {
            TeamResponse error = new TeamResponse(false, "Team not found", null);
            return error;
        }
        if (userToLeave.isEmpty()) {
            TeamResponse error = new TeamResponse(false, "User not found", null);
            return error;
        }

        teamToLeave.removeMember(userToLeave.get().getUsername());
        teamToLeave.setComplete(teamToLeave.size() >= teamProperties.minSize());
        List<Team> responseData = List.of(teamToLeave);
        TeamResponse response = new TeamResponse(true, "Left team successfully", responseData);
        return response;
    }
}

