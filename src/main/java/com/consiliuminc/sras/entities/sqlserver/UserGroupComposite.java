package com.consiliuminc.sras.entities.sqlserver;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserGroupComposite implements Serializable {

    private Integer userid;

    private Integer groupid;


    public UserGroupComposite() {

    }

    public UserGroupComposite(Integer userid, Integer groupid) {
        this.userid = userid;
        this.groupid = groupid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
