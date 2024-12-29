package com.consiliuminc.sras.controllers;

import com.consiliuminc.sras.entities.sqlserver.Group;
import com.consiliuminc.sras.service.sqlserver.FunctionService;
import com.consiliuminc.sras.service.sqlserver.GroupService;
import com.consiliuminc.sras.vo.ResVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/group")
public class GroupController {


    private GroupService groupService;

    private FunctionService functionService;

    @Autowired
    public GroupController(GroupService groupService, FunctionService functionService) {
        this.groupService = groupService;
        this.functionService = functionService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView group() {
        ModelAndView groupPage = new ModelAndView();
        groupPage.setViewName("group");
        groupPage.addObject("groups", groupService.findAll());
        groupPage.addObject("functions", functionService.findAll());
        return groupPage;
    }


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResVo insert(@RequestBody Group group) {
        groupService.saveGroupAndGroupFunction(group);
        ResVo vo = new ResVo(HttpStatus.OK, "Success");
        return vo;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseBody
    public ResVo update(@RequestBody Group group) {
        groupService.saveGroupAndGroupFunction(group);
        ResVo vo = new ResVo(HttpStatus.OK, "Success");
        return vo;
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    @ResponseBody
    public ResVo delete(@RequestBody Group group) {
        groupService.deleteGroupAndGroupFunction(group);
        ResVo vo = new ResVo(HttpStatus.OK, "Success");
        return vo;
    }
}
