package com.consiliuminc.sras.controllers;


import com.consiliuminc.sras.service.PsttRecognizeService;
import com.consiliuminc.sras.util.FileUtils;
import com.consiliuminc.sras.util.WavUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.consiliuminc.sras.consts.AppConst.OUTPUT_PATH;
import static com.consiliuminc.sras.consts.AppConst.UPLOAD_PATH;

@Controller
@RequestMapping(value = "/recognize")
public class RecognizeController {


    private PsttRecognizeService psttService;


    @Autowired
    public RecognizeController(PsttRecognizeService psttService) {
        this.psttService = psttService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String page() {
        return "recognize";
    }


    @RequestMapping(value = "/recognizeUpload", headers = "Content-Type=multipart/form-data", method = RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile[] files) throws Exception {
        List<Path> upload_paths = FileUtils.saveMultipartFiles(UPLOAD_PATH, files);
        List<Path> output_paths= WavUtils.convertAudioToWav(OUTPUT_PATH,upload_paths);
        return psttService.sendDataToPstt(output_paths);

    }


}
