package com.consiliuminc.sras.entities.postgres;

import javax.persistence.*;

@Entity
@Table(name = "qa_usermanage_user_groups")
public class QaUserManageUserGroups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "group_id")
    private Integer groupID;
    @Column(name = "user_id")
    private Integer userID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }
}
