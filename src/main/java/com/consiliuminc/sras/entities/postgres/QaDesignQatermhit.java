package com.consiliuminc.sras.entities.postgres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "qa_design_qatermhit")
public class QaDesignQatermhit {
    @Id
    private Integer id;
    @Column(name = "job_id")
    private Integer jobID;
    @Column(columnDefinition = "nvarchar(512)")
    private String human_Hit;
    private double human_Hit_Score;
    private Integer human_Machine_Diff;
    @Column(columnDefinition = "nvarchar(512)")
    private String machine_Hit;
    private double machine_Hit_Score;
    private Integer term_ID;
    @Column(columnDefinition = "nvarchar(512)")
    private String term_Name;
    private Integer term_PID;
    private Integer term_Property;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getJobID() {
        return jobID;
    }

    public void setJobID(Integer jobId) {
        this.jobID = jobId;
    }

    public String getHuman_Hit() {
        return human_Hit;
    }

    public void setHuman_Hit(String human_Hit) {
        this.human_Hit = human_Hit;
    }

    public double getHuman_Hit_Score() {
        return human_Hit_Score;
    }

    public void setHuman_Hit_Score(double human_Hit_Score) {
        this.human_Hit_Score = human_Hit_Score;
    }

    public Integer getHuman_Machine_Diff() {
        return human_Machine_Diff;
    }

    public void setHuman_Machine_Diff(Integer human_Machine_Diff) {
        this.human_Machine_Diff = human_Machine_Diff;
    }

    public String getMachine_Hit() {
        return machine_Hit;
    }

    public void setMachine_Hit(String machine_Hit) {
        this.machine_Hit = machine_Hit;
    }

    public double getMachine_Hit_Score() {
        return machine_Hit_Score;
    }

    public void setMachine_Hit_Score(double machine_Hit_Score) {
        this.machine_Hit_Score = machine_Hit_Score;
    }

    public Integer getTerm_ID() {
        return term_ID;
    }

    public void setTerm_ID(Integer term_ID) {
        this.term_ID = term_ID;
    }

    public String getTerm_Name() {
        return term_Name;
    }

    public void setTerm_Name(String term_Name) {
        this.term_Name = term_Name;
    }

    public Integer getTerm_PID() {
        return term_PID;
    }

    public void setTerm_PID(Integer term_PID) {
        this.term_PID = term_PID;
    }

    public Integer getTerm_Property() {
        return term_Property;
    }

    public void setTerm_Property(Integer term_Property) {
        this.term_Property = term_Property;
    }
}

