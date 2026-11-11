package org.broncohacks.portal;

import jakarta.validation.constraints.NotBlank;

public class LeaveTeamRequest {
  @NotBlank private String teamName;
  @NotBlank private String email;

  public void setEmail(String email) {
    this.email = email;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  public String getEmail() {
    return email;
  }

  public String getTeamName() {
    return teamName;
  }
}
