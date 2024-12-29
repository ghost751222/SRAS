package com.consiliuminc.sras.entities.postgres;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "qa_design_qaterm_boundpsaemodel")
@Entity
public class QaDesignQatermBoundpsaemodel {
    @Id
    private Integer id;
    @Column(name = "qaterm_id")
    private Integer qaTermID;
    @Column(name = "psaemodel_id",columnDefinition = "NVARCHAR(233)")
    private String psaemodelID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQaTermID() {
        return qaTermID;
    }

    public void setQaTermID(Integer qaTermID) {
        this.qaTermID = qaTermID;
    }

    public String getPsaemodelID() {
        return psaemodelID;
    }

    public void setPsaemodelID(String psaemodelID) {
        this.psaemodelID = psaemodelID;
    }
}
