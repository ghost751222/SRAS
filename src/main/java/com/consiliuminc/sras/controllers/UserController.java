package com.consiliuminc.sras.controllers;

import com.consiliuminc.sras.entities.sqlserver.Group;
import com.consiliuminc.sras.entities.sqlserver.User;
import com.consiliuminc.sras.entities.sqlserver.UserGroup;
import com.consiliuminc.sras.service.sqlserver.GroupService;
import com.consiliuminc.sras.service.sqlserver.UserGroupService;
import com.consiliuminc.sras.service.sqlserver.UserService;
import com.consiliuminc.sras.util.JacksonUtils;
import com.consiliuminc.sras.vo.ResVo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/user")


public class UserController {


    private UserService userService;

    private GroupService groupService;

    private UserGroupService userGroupService;

    @Autowired
    public UserController(UserService userService, GroupService groupService, UserGroupService userGroupService) {
        this.userService = userService;
        this.groupService = groupService;
        this.userGroupService = userGroupService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView page() {
        ModelAndView userPage = new ModelAndView();
        userPage.setViewName("user");
        List<User> users = userService.findAll();
        List<Group> groups = groupService.findAll();
        users.forEach(user -> {
            List<UserGroup> userGroups = userGroupService.findByUserId(user.getId());
            user.setUserGroups(new ArrayList<>());
            userGroups.forEach(userGroup -> {
                user.getUserGroups().add(userGroup.getUserGroupComposite().getGroupid());
            });
        });
        userPage.addObject("users", users);
        userPage.addObject("groups", groups);

        return userPage;
    }

    @RequestMapping(value = {"/{account}", "/"}, method = {RequestMethod.GET})
    @ResponseBody
    public List<User> query(@PathVariable(name = "account", required = false) String account) {

        if (Strings.isEmpty(account))
            return this.userService.findAll();
        else
            return this.userService.findByAccount(account);
    }


    @RequestMapping(value = "", headers = "Content-Type=application/json", method = {RequestMethod.POST})
    @ResponseBody
    public ResVo insert(@RequestBody User user) {
        ResVo vo = new ResVo();
        if (userService.isExist(user)) {
            vo.setStatus(HttpStatus.CREATED);
            vo.setMessage("Account is already exist");
            return vo;
        }

        userService.saveUserAndUserGroup(user);
        vo.setStatus(HttpStatus.OK);
        return vo;
    }


    @RequestMapping(value = "", headers = "Content-Type=application/json", method = {RequestMethod.PUT})
    @ResponseBody
    public ResVo update(@RequestBody User user) {
        ResVo vo = new ResVo();
        userService.saveUserAndUserGroup(user);
        vo.setStatus(HttpStatus.OK);
        return vo;
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ResponseBody
    public ResVo delete(@RequestBody User user) {
        userService.deleteUserAndUserGroup(user);
        ResVo vo = new ResVo(HttpStatus.OK, "Success");
        return vo;
    }


    @RequestMapping(value = "/resetpassword", method = RequestMethod.PUT)
    @ResponseBody
    public ResVo resetPassword(@RequestBody String password, HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        password = JacksonUtils.toJsonNode(password).get("password").asText();
        user.setPassword(password);
        userService.save(user);
        ResVo vo = new ResVo(HttpStatus.OK, "Success");
        return vo;
    }
}
