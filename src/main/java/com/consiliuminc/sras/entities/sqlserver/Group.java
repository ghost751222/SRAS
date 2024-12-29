package com.consiliuminc.sras.entities.sqlserver;


import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.util.List;
import java.util.Set;


@Table(name = "sras_group")
@Entity
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

   // @Nationalized
    @Column(name = "group_name",columnDefinition = "NVARCHAR(128)")
    private String name;

    @Transient
    private List<String> functions;


    @OneToMany(fetch=FetchType.LAZY,mappedBy = "group")
    Set<GroupFunction> groupFunctions;


   // @OneToMany(mappedBy = "group")
    @OneToMany(fetch=FetchType.LAZY,mappedBy = "group")
    Set<UserGroup> groups;

//    public Set<UserGroup> getGroups() {
//        return groups;
//    }
//
//    public void setGroups(Set<UserGroup> groups) {
//        this.groups = groups;
//    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFunctions() {
        return functions;
    }

    public void setFunctions(List<String> functions) {
        this.functions = functions;
    }
}
