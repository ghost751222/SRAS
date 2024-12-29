package com.consiliuminc.sras.entities.sqlserver;


import javax.persistence.Embeddable;
import java.io.Serializable;


@Embeddable
public class GroupFunctionComposite implements Serializable {


    private Integer groupid;

    private String program_name;

    public GroupFunctionComposite() {

    }

    public GroupFunctionComposite(Integer groupid, String program_name) {
        this.groupid = groupid;
        this.program_name = program_name;
    }

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
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
