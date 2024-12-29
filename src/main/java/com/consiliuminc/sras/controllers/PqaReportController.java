package com.consiliuminc.sras.controllers;

import com.consiliuminc.sras.service.ReportService;
import com.consiliuminc.sras.service.ScfReportService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping(value = "/pqareport")
public class PqaReportController {



    private ScfReportService scfReportService ;
    private Boolean IsExport = false;

    @Autowired
    public PqaReportController( ScfReportService scfReportService) {
        this.scfReportService = scfReportService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String page() {
        return "pqareport";
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public ResponseEntity<byte[]> report(@RequestParam String date, @RequestParam String sales_sdate, @RequestParam String sales_edate) throws Exception {


        if (IsExport) {
            return ResponseEntity.ok().body("report is already produced by another session".getBytes(StandardCharsets.UTF_8));
        } else {
            try {
                IsExport = true;
                ByteArrayOutputStream outputStream = scfReportService.exportReport(date, sales_sdate, sales_edate);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s.xlsx", date.replace("-", Strings.EMPTY)))
                        .body(outputStream.toByteArray());
            } catch (Exception e) {
                throw e;
            } finally {
                IsExport = false;
            }
        }
    }
}
