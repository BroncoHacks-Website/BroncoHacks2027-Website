package org.broncohacks.portal;

import jakarta.validation.constraints.NotBlank;

public class LeaveTeamRequest {
  @NotBlank private String teamName;

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }
}
