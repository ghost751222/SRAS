package com.consiliuminc.sras.entities.postgres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "qa_interface_psaemodel")
public class QaInterfacePsaemodel {

    @Id
    @Column(columnDefinition = "nvarchar(233)")
    private String id;
    @Column(columnDefinition = "nvarchar(256)")
    private String name;
    @Column(columnDefinition = "nvarchar(256)")
    private String pid;
    private Integer type;
    private boolean online;
    private Integer flag;
    @Column(columnDefinition = "nvarchar(256)")
    private String model_user_group;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getModel_user_group() {
        return model_user_group;
    }

    public void setModel_user_group(String model_user_group) {
        this.model_user_group = model_user_group;
    }
}
