package com.consiliuminc.sras.entities.sqlserver;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ADS_Users")
public class ADSUsers {
    @Id
    private String UserID;
    private String UserName;
    private String SalerID;
    private String TeamCode;
    private String TeamLeader;
    private String DeskCode;
    private String DeskLeader;
    private String GroupCode;
    private String GroupLeader;
    private String Enable;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getSalerID() {
        return SalerID;
    }

    public void setSalerID(String salerID) {
        SalerID = salerID;
    }

    public String getTeamCode() {
        return TeamCode;
    }

    public void setTeamCode(String teamCode) {
        TeamCode = teamCode;
    }

    public String getTeamLeader() {
        return TeamLeader;
    }

    public void setTeamLeader(String teamLeader) {
        TeamLeader = teamLeader;
    }

    public String getDeskCode() {
        return DeskCode;
    }

    public void setDeskCode(String deskCode) {
        DeskCode = deskCode;
    }

    public String getDeskLeader() {
        return DeskLeader;
    }

    public void setDeskLeader(String deskLeader) {
        DeskLeader = deskLeader;
    }

    public String getGroupCode() {
        return GroupCode;
    }

    public void setGroupCode(String groupCode) {
        GroupCode = groupCode;
    }

    public String getGroupLeader() {
        return GroupLeader;
    }

    public void setGroupLeader(String groupLeader) {
        GroupLeader = groupLeader;
    }

    public String getEnable() {
        return Enable;
    }

    public void setEnable(String enable) {
        Enable = enable;
    }
}
