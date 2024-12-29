package com.consiliuminc.sras.entities.sqlserver;


import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "sras_function")
public class Function {

    @Id
    private String program_name;

    private String program_url;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String display_name;

    private Integer seq;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "function")
    Set<GroupFunction> groupFunctions;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "function")
    @OrderBy("seq ASC")
    Set<MenuFunction> menuFunctions;

    public Function() {

    }

    public Function(String program_name, String program_url, String display_name, Integer seq) {
        this.program_name = program_name;
        this.program_url = program_url;
        this.display_name = display_name;
        this.seq = seq;
    }


    public String getProgram_name() {
        return program_name;
    }

    public void setProgram_name(String program_name) {
        this.program_name = program_name;
    }

    public String getProgram_url() {
        return program_url;
    }

    public void setProgram_url(String program_url) {
        this.program_url = program_url;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }
}
