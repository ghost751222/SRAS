package com.consiliuminc.sras.configs;


import com.consiliuminc.sras.vo.PsaeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PsaeConfig {


    @Value("${psae.casecontent.critical.key:@null}")
    private String criticalKey;


    @Value("${psae.lackOfOpenModelName:@null}")
    private String lackOfOpenModelName;

    @Value("${psae.openAsAssetModelName:@null}")
    private String openAsAssetModelName;


    @Value("${psae.ensureUnclearModelName:@null}")
    private String ensureUnclearModelName;


    @Value("${psae.positive.models}")
    List<String> psaePositiveModels;

    @Value("${psae.negative.models}")
    List<String> psaeNegativeModels;

    @Autowired
    private Environment env;

    @Bean
    public PsaeVo psaeVo() {
        PsaeVo vo = new PsaeVo();
        vo.setIp(env.getProperty("psae.config.ip"));
        vo.setPort(Integer.parseInt(env.getProperty("psae.config.port")));
        return vo;
    }


    public String getCriticalKey() {
        return criticalKey;
    }

    public String getLackOfOpenModelName() {
        return lackOfOpenModelName;
    }


    public String getOpenAsAssetModelName() {
        return openAsAssetModelName;
    }

    public String getEnsureUnclearModelName() {
        return ensureUnclearModelName;
    }


    public List<String> getPsaePositiveModels() {
        return psaePositiveModels;
    }


    public List<String> getPsaeNegativeModels() {
        return psaeNegativeModels;
    }
}
