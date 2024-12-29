package com.consiliuminc.sras.controllers;


import com.consiliuminc.sras.entities.postgres.QaDesignQatemplate;
import com.consiliuminc.sras.entities.postgres.QaInterfaceDim;
import com.consiliuminc.sras.entities.postgres.QaTaskJob;
import com.consiliuminc.sras.service.postgres.QaDesignQatemplateService;
import com.consiliuminc.sras.service.postgres.QaInterfaceDimService;
import com.consiliuminc.sras.service.postgres.QaTaskJobService;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

//@RestController
//@RequestMapping("/api")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    private QaTaskJobService qaTaskJobService;


    private QaInterfaceDimService qaInterfaceDimService;


    private QaDesignQatemplateService qaDesignQatemplateService;

    @Autowired
    public ApiController(QaTaskJobService qaTaskJobService, QaInterfaceDimService qaInterfaceDimService, QaDesignQatemplateService qaDesignQatemplateService) {
        this.qaTaskJobService = qaTaskJobService;
        this.qaInterfaceDimService = qaInterfaceDimService;
        this.qaDesignQatemplateService = qaDesignQatemplateService;
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public List<QaTaskJob> findAll(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate sDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate eDate, @RequestParam String templateId) {

        List<QaTaskJob> vos = qaTaskJobService.findByTaskDateRange(sDate, eDate);
        if (Strings.isEmpty(templateId)) {
            return vos;
        } else {
            int qId = Integer.parseInt(templateId);
            List<QaTaskJob> q = vos.stream().filter(v -> v.getQatemplate_id() == qId).collect(Collectors.toList());

            return q;

        }

    }


    @RequestMapping(value = "/findColumn", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<QaInterfaceDim> findColumn() {
        return qaInterfaceDimService.findAll();

    }

    @RequestMapping(value = "/findTemplate", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<QaDesignQatemplate> findTemplate() {
        return qaDesignQatemplateService.findAll();
    }
}
