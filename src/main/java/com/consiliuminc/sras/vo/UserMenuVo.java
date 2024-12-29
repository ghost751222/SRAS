package com.consiliuminc.sras.vo;


import com.consiliuminc.sras.entities.sqlserver.Function;
import com.consiliuminc.sras.entities.sqlserver.Menu;
import com.consiliuminc.sras.entities.sqlserver.UserGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMenuVo {


    private Integer userId;


    //private Map<String,List<Function>> menuMap;
    private Map<Menu, List<Function>> menuMap;


    private List<UserGroup> userGroups;


    public UserMenuVo() {
        this.userGroups = new ArrayList<>();
        menuMap = new HashMap<>();
    }

    public Map<Menu, List<Function>> getMenuMap() {
        return menuMap;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }


}
