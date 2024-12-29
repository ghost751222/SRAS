package com.consiliuminc.sras.controllers;

import com.consiliuminc.sras.entities.sqlserver.UserGroup;
import com.consiliuminc.sras.service.sqlserver.UserGroupService;
import com.consiliuminc.sras.vo.ResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/userGroup")
public class UserGroupController {


    private UserGroupService userGroupService;

    @Autowired
    public UserGroupController(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public List<UserGroup> findUserGroup(@PathVariable Integer userId){
        return userGroupService.findByUserId(userId);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResVo insert(UserGroup userGroup) {
        userGroupService.save(userGroup);
        ResVo vo = new ResVo(HttpStatus.OK, "Success");
        return vo;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public ResVo update(UserGroup userGroup) {
        userGroupService.save(userGroup);
        ResVo vo = new ResVo(HttpStatus.OK, "Success");
        return vo;
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ResponseBody
    public ResVo delete(UserGroup userGroup) {
        userGroupService.delete(userGroup);
        ResVo vo = new ResVo(HttpStatus.OK, "Success");
        return vo;
    }
}
