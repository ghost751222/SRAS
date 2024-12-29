package com.consiliuminc.sras.componets;

import com.consiliuminc.sras.entities.sqlserver.*;
import com.consiliuminc.sras.service.postgres.QaTaskJobService;
import com.consiliuminc.sras.service.sqlserver.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DefaultDataComponent {

    private UserService userService;
    private GroupService groupService;
    private UserGroupService userGroupService;
    private FunctionService functionService;
    private GroupFunctionService groupFunctionService;
    private MenuService menuService;
    private MenuFunctionService menuFunctionService;

    @Autowired
    private QaTaskJobService qaTaskJobService;

    @Autowired
    public DefaultDataComponent(UserService userService, GroupService groupService, UserGroupService userGroupService, FunctionService functionService, GroupFunctionService groupFunctionService, MenuService menuService, MenuFunctionService menuFunctionService) {
        this.userService = userService;
        this.groupService = groupService;
        this.userGroupService = userGroupService;
        this.functionService = functionService;
        this.groupFunctionService = groupFunctionService;
        this.menuService = menuService;
        this.menuFunctionService = menuFunctionService;
    }


    @PostConstruct
    private void postConstructMenu() throws SQLException {
        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu("systemMenu", "系統管理", 1));
        menus.add(new Menu("recognizeMenu", "文本轉譯管理", 2));
        menus.add(new Menu("pqaMenu", "語音質檢管理", 3));

        menus.forEach(menu -> {
            MenuFunction menuFunction = new MenuFunction();
            menuFunction.setMenu(menu);
            this.menuService.save(menu);
            this.getData(menu.getMenu_name()).forEach(func -> {
                menuFunction.setFunction(func);
                this.functionService.save(func);
                this.menuFunctionService.save(menuFunction);
            });
        });
    }

    @PostConstruct
    private void postConstructUser() {
        String groupName = "系統管理";
        List<Group> groups = this.groupService.findByName(groupName);
        Group group;
        User user;
        GroupFunction groupFunction;
        if (groups.size() == 0) {
            group = new Group();
            group.setName(groupName);
            this.groupService.save(group);
        } else {
            group = groups.get(0);
        }


        String account = "admin", password = "SrasAdmin", userName = "超級管理員";
        List<User> users = userService.findByAccount(account);
        if (users.size() == 0) {
            user = new User();
            user.setName(userName);
            user.setAccount(account);
            user.setPassword(password);
            this.userService.save(user);
        } else
            user = users.get(0);

        if (group != null && user != null) {
            UserGroup userGroup = new UserGroup();
            userGroup.setUser(user);
            userGroup.setGroup(group);
            this.userGroupService.save(userGroup);
        }

        List<MenuFunction> menuFunctions = this.menuFunctionService.findAll();
        Group finalGroup = group;
        menuFunctions.forEach(menuFunction -> {
            GroupFunction _groupFunction = new GroupFunction();
            _groupFunction.setGroup(finalGroup);
            _groupFunction.setFunction(menuFunction.getFunction());
            this.groupFunctionService.save(_groupFunction);
        });

        //create minimum group authorization
        groups = this.groupService.findByName("其他用戶群組");
        if (groups.size() == 0) {
            group = new Group();
            group.setName("其他用戶群組");
            this.groupService.save(group);
            groupFunction = new GroupFunction();
            groupFunction.setGroup(group);
            groupFunction.setFunction(new Function("recognize", "/recognize", "文本線上轉譯", 1));

            this.groupFunctionService.save(groupFunction);
        }
    }


    private List<Function> getData(String key) {
        Map<String, List<Function>> maps = new HashMap<>();
        maps.put("systemMenu", new ArrayList<>());
        maps.get("systemMenu").add(new Function("user", "/user", "用戶管理", 1));
        maps.get("systemMenu").add(new Function("group", "/group", "群組管理", 2));

        maps.put("recognizeMenu", new ArrayList<>());
        maps.get("recognizeMenu").add(new Function("recognize", "/recognize", "文本線上轉譯", 1));

        maps.put("pqaMenu", new ArrayList<>());
        maps.get("pqaMenu").add(new Function("pqa", "/pqa", "語音質檢查詢", 1));
        maps.get("pqaMenu").add(new Function("pqareport", "/pqareport", "語音質檢報表", 2));
        return maps.get(key);
    }


}
