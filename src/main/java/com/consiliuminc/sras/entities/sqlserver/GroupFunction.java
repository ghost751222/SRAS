package com.consiliuminc.sras.entities.sqlserver;


import javax.persistence.*;


@Table(name = "sras_group_function")
@Entity
public class GroupFunction {

    @EmbeddedId
    private GroupFunctionComposite groupFunctionComposite;


    @ManyToOne
    @MapsId("program_name")
    @JoinColumn(name = "program_name")
    Function function;



    @ManyToOne
    @MapsId("groupid")
    @JoinColumn(name = "groupid")
    Group group;


    public GroupFunction(){
        this.groupFunctionComposite = new GroupFunctionComposite();
    }
    public GroupFunctionComposite getGroupFunctionComposite() {
        return groupFunctionComposite;
    }

    public void setGroupFunctionComposite(GroupFunctionComposite groupFunctionComposite) {
        this.groupFunctionComposite = groupFunctionComposite;
    }


    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.groupFunctionComposite.setProgram_name(function.getProgram_name());
        this.function = function;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.groupFunctionComposite.setGroupid(group.getId());
        this.group = group;
    }
}
