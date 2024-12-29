package com.consiliuminc.sras.controllers;

import com.consiliuminc.sras.entities.postgres.QaDesignQatemplate;
import com.consiliuminc.sras.entities.postgres.QaInterfaceDim;
import com.consiliuminc.sras.entities.postgres.QaInterfaceRoleDim;
import com.consiliuminc.sras.entities.postgres.QaTaskJob;
import com.consiliuminc.sras.service.postgres.QaDesignQatemplateService;
import com.consiliuminc.sras.service.postgres.QaInterfaceDimService;
import com.consiliuminc.sras.service.postgres.QaInterfaceRoleDimService;
import com.consiliuminc.sras.service.postgres.QaTaskJobService;
import com.consiliuminc.sras.util.JacksonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/pqa")
public class PqaController {


    private static final Logger logger = LoggerFactory.getLogger(PqaController.class);

    private QaTaskJobService qaTaskJobService;
    private QaInterfaceDimService qaInterfaceDimService;
    private QaDesignQatemplateService qaDesignQatemplateService;
    private QaInterfaceRoleDimService qaInterfaceRoleDimService;


    @Autowired
    public PqaController(QaTaskJobService qaTaskJobService, QaInterfaceDimService qaInterfaceDimService, QaDesignQatemplateService qaDesignQatemplateService, QaInterfaceRoleDimService qaInterfaceRoleDimService) {
        this.qaTaskJobService = qaTaskJobService;
        this.qaInterfaceDimService = qaInterfaceDimService;
        this.qaDesignQatemplateService = qaDesignQatemplateService;
        this.qaInterfaceRoleDimService = qaInterfaceRoleDimService;
    }


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String page() {

        return "pqa";
    }


    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<QaTaskJob> findAll(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate taskSDate,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate taskEDate,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tlSDate,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tlEDate,
                                   @RequestParam String templateId, @RequestParam String state,@RequestParam String applicationFormID) {


        List<QaTaskJob> vos = qaTaskJobService.findByTaskDateRange(taskSDate, taskEDate);
        if (!StringUtils.isEmpty(templateId)) {
            int qId = Integer.parseInt(templateId);
            vos = vos.stream().filter(v -> v.getQatemplate_id() == qId).collect(Collectors.toList());
        }


        if (!StringUtils.isEmpty(state)) {
            int sId = Integer.parseInt(state);
            vos = vos.stream().filter(v -> v.getState() == sId).collect(Collectors.toList());
        }
        if (!StringUtils.isEmpty(applicationFormID)) {
            vos = vos.stream().filter(v -> {
                try {
                   return JacksonUtils.toJsonNode(v.getTask()).get("psae_ApplicationFormID").asText().equalsIgnoreCase(applicationFormID);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }).collect(Collectors.toList());
        }
        return vos;

    }


    @RequestMapping(value = "/findColumn", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<QaInterfaceDim> findColumn() {
        List<QaInterfaceDim> qaInterfaceDims = qaInterfaceDimService.findAllOrderByOrder();
        List<QaInterfaceRoleDim> qaInterfaceRoleDims = qaInterfaceRoleDimService.findAllByRoleId(4);

        Iterator<QaInterfaceDim> i = qaInterfaceDims.iterator();
        while (i.hasNext()) {
            QaInterfaceDim qaInterfaceDim = i.next(); // must be called before you can call i.remove()
            QaInterfaceRoleDim vo = qaInterfaceRoleDims.stream().filter(qaInterfaceRoleDim -> qaInterfaceRoleDim.getDimId().equals(qaInterfaceDim.getId())).findAny().orElse(null);

            Integer IsDisplay = vo == null ? 0 : vo.getIsDisplay();
            if (IsDisplay.equals(0)) i.remove();
        }
        return qaInterfaceDims;
       // return qaInterfaceDimService.findAll();

    }

    @RequestMapping(value = "/findTemplate", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<QaDesignQatemplate> findTemplate() {
        return qaDesignQatemplateService.findAll();
    }
}
