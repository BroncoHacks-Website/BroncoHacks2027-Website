package org.broncohacks.portal;

import org.springframework.cglib.core.Local;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Team {
    Team(String t_teamName){
        teamName = t_teamName;
        inviteCode = UUID.randomUUID().toString().substring(0,10).toUpperCase();
        LocalDateTime codeCreatedAt = LocalDateTime.now();
        teamID = idCounter;
        idCounter++;
    }

//    static private int MAX_TEAM_SIZE = 4;
    private static int idCounter;

    private User captain;
    private ArrayList<User> members = new ArrayList<User>();
    private String teamName;
    private int teamID;
    private String inviteCode;

    private boolean complete;

    


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

    public int size(){
        return members.size();
    }

    public String getInviteCode() {return inviteCode; }

    // returns true if member is added successfully, false otherwise
    public boolean addMember(User user, int maxSize) {
        if (members.size() < maxSize) {
            members.add(user);
            return true;
        }

        return false;
    }

    public boolean removeMember(String usernameToRemove){
        for(User member : members){
            if(member.getUsername().equals(usernameToRemove)){
                members.remove(member);
                return true;
            }
        }
        return false;
    }
    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

}
