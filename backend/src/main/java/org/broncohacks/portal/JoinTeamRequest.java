package org.broncohacks.portal;

import jakarta.validation.constraints.NotBlank;

public class JoinTeamRequest {
  @NotBlank private String inviteCode;

  public void setInviteCode(String inviteCode) {
    this.inviteCode = inviteCode;
  }

  public String getInviteCode() {
    return inviteCode;
  }
}
