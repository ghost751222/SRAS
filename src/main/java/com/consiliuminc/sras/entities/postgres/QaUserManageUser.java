package com.consiliuminc.sras.entities.postgres;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qa_usermanage_user")
public class QaUserManageUser {
    @Column(columnDefinition = "nvarchar(256)")
    private String email;
    private Integer groupId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "is_superuser")
    private Integer isSuperuser;
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    @Column(columnDefinition = "nvarchar(64)")
    private String name;
    @Column(columnDefinition = "nvarchar(128)")
    private String password;

    @Column(columnDefinition = "nvarchar(256)")
    private String roleCode;
    @Column(columnDefinition = "nvarchar(256)")
    private String status;
    @Column(columnDefinition = "nvarchar(30)")
    private String username;
    @Column(columnDefinition = "nvarchar(256)")
    private String workPlace;
    @Column(columnDefinition = "nvarchar(1024)")
    private String group_name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer value) {
        this.groupId = value;
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer value) {
        this.id = value;
    }

    public Integer getIsSuperuser() {
        return isSuperuser;
    }

    public void setIsSuperuser(Integer value) {
        this.isSuperuser = value;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime value) {
        this.lastLogin = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String value) {
        this.roleCode = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        this.status = value;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String value) {
        this.username = value;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String value) {
        this.workPlace = value;
    }


    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }
}

