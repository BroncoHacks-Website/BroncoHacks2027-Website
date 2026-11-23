package org.broncohacks.portal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Team {
  private static int MAX_TEAM_SIZE = 4;
  private static int idCounter;

  private User captain;
  private ArrayList<User> members = new ArrayList<User>();
  private String teamName;
  private int teamID;
  private String inviteCode;

  public Team(String teamName) {
    LocalDateTime codeCreatedAt = LocalDateTime.now();

    this.teamName = teamName;
    this.inviteCode = UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    this.teamID = idCounter;
    Team.idCounter++;
  }

  public User getCaptain() {
    return captain;
  }

  public ArrayList<User> getMembers() {
    return members;
  }

  public String getTeamName() {
    return teamName;
  }

  public int getTeamID() {
    return teamID;
  }

  public int size() {
    return members.size();
  }

  public String getInviteCode() {
    return inviteCode;
  }

  // returns true if member is added successfully, false otherwise
  public boolean addMember(User newMember) {
    if (members.size() < MAX_TEAM_SIZE) {
      members.add(newMember);
      return true;
    } else {
      return false;
    }
  }

  public boolean removeMember(String emailToRemove) {
    for (User member : members) {
      if (member.getEmail().equals(emailToRemove)) {
        members.remove(member);
        return true;
      }
    }
    return false;
  }
}
