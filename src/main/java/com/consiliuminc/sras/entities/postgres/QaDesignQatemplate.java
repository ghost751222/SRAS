package com.consiliuminc.sras.entities.postgres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "qa_design_qatemplate")
public class QaDesignQatemplate {
    @Id
    private Integer id;
    @Column(columnDefinition = "NVARCHAR(512)")
    private String name;
    private Integer is_online;
    @Column(name = "\"group\"",columnDefinition = "NVARCHAR(64)")
    private String group;
    @Column(columnDefinition = "NVARCHAR(1024)")
    private String product;
    private LocalDateTime auto_online_date;
    private LocalDateTime auto_offline_date;
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

    public Integer getIs_online() {
        return is_online;
    }

    public void setIs_online(Integer is_online) {
        this.is_online = is_online;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public LocalDateTime getAuto_online_date() {
        return auto_online_date;
    }

    public void setAuto_online_date(LocalDateTime auto_online_date) {
        this.auto_online_date = auto_online_date;
    }

    public LocalDateTime getAuto_offline_date() {
        return auto_offline_date;
    }

    public void setAuto_offline_date(LocalDateTime auto_offline_date) {
        this.auto_offline_date = auto_offline_date;
    }


}
