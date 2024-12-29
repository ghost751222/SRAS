package com.consiliuminc.sras.entities.sqlserver;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Table(name = "sras_user")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String account;
    private String password;
    @Column(unique = false , columnDefinition="nvarchar(255)")
    private String name;


    @Transient
    private List<Integer> userGroups;


//    @OneToMany(mappedBy = "user")
    @OneToMany(fetch=FetchType.LAZY,mappedBy = "user")
    Set<UserGroup> userGroup;

//    public Set<UserGroup> getUserGroup() {
//        return userGroup;
//    }
//
//    public void setUserGroup(Set<UserGroup> userGroup) {
//        this.userGroup = userGroup;
//    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<Integer> userGroups) {
        this.userGroups = userGroups;
    }
}
