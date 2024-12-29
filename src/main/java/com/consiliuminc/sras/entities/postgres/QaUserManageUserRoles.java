package com.consiliuminc.sras.entities.postgres;


import javax.persistence.*;

@Entity
@Table(name = "qa_usermanage_user_roles")
public class QaUserManageUserRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "user_id")
    private Integer userID;
    @Column(name = "role_id")
    private Integer roleID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer user_id) {
        this.userID = user_id;
    }

    public Integer getRoleID() {
        return roleID;
    }

    public void setRoleID(Integer role_id) {
        this.roleID = role_id;
    }
}
