package org.broncohacks.portal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService){this.teamService = teamService;}

    @GetMapping
    public ResponseEntity<TeamResponse> listTeams() {
        TeamResponse response = teamService.listTeams();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        TeamResponse response = teamService.createTeam(request);

        if(response.isSuccessful()) {
            return ResponseEntity.ok(response);
        }
        else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    //Join request body consists of inviteCode and newMemberName (both strings)
    @PostMapping("/join")
    public ResponseEntity<TeamResponse> joinTeam(@Valid @RequestBody JoinTeamRequest request) {

        TeamResponse response = teamService.joinTeam(request);
        if(response.isSuccessful()) {
            return ResponseEntity.ok(response);
        }
        else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<TeamResponse> leaveTeam(@Valid @RequestBody LeaveTeamRequest request) {
        TeamResponse response = teamService.leaveTeam(request);
        if(response.isSuccessful()) {
            return ResponseEntity.ok(response);
        }
        else{
            return ResponseEntity.badRequest().body(response);
        }
    }
}

