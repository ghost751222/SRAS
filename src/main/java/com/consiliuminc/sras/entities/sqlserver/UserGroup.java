package com.consiliuminc.sras.entities.sqlserver;


import javax.persistence.*;

@Entity
@Table(name = "sras_user_group")
public class UserGroup {


    @EmbeddedId
    private UserGroupComposite userGroupComposite;

    @ManyToOne
    @MapsId("userid")
    @JoinColumn(name = "userid")
    User user;



    @ManyToOne
    @MapsId("groupid")
    @JoinColumn(name = "groupid")
    Group group;



    public UserGroup(){
        this.userGroupComposite = new UserGroupComposite(0,0);
    }
    public UserGroupComposite getUserGroupComposite() {
        return userGroupComposite;
    }


    public void setUserGroupComposite(UserGroupComposite userGroupComposite) {
        this.userGroupComposite = userGroupComposite;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userGroupComposite.setUserid(this.user.getId());
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
        this.userGroupComposite.setGroupid(this.group.getId());
    }
}

