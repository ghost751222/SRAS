package com.consiliuminc.sras.vo;

import java.util.List;

public class StandardModelGroupVo {


    private Integer count;
    private String groupName;
    private List<String> groupModels;


    public StandardModelGroupVo(Integer count, String groupName, List<String> groupModels) {
        this.count = count;
        this.groupName = groupName;
        this.groupModels = groupModels;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getGroupModels() {
        return groupModels;
    }

    public void setGroupModels(List<String> groupModels) {
        this.groupModels = groupModels;
    }
}
