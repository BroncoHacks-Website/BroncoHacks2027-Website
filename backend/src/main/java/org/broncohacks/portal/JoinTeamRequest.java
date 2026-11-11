package org.broncohacks.portal;

import jakarta.validation.constraints.NotBlank;

public class JoinTeamRequest {
  @NotBlank private String inviteCode;
  @NotBlank private String newMemberEmail;

  public void setInviteCode(String inviteCode) {
    this.inviteCode = inviteCode;
  }

  public void setNewMemberEmail(String newMemberEmail) {
    this.newMemberEmail = newMemberEmail;
  }

  public String getInviteCode() {
    return inviteCode;
  }

  public String getNewMemberEmail() {
    return newMemberEmail;
  }
}
