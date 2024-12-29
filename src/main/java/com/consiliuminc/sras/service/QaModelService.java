package com.consiliuminc.sras.service;


import com.consiliuminc.sras.entities.postgres.QaDesignQatermBoundpsaemodel;
import com.consiliuminc.sras.entities.postgres.QaDesignQatermhit;
import com.consiliuminc.sras.entities.postgres.QaInterfacePsaemodel;
import com.consiliuminc.sras.service.postgres.QaDesignQatermBoundpsaemodelService;
import com.consiliuminc.sras.service.postgres.QaDesignQatermhitService;
import com.consiliuminc.sras.service.postgres.QaInterfacePsaemodelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QaModelService {

    private QaDesignQatermhitService qaDesignQatermhitService;

    private QaDesignQatermBoundpsaemodelService qaDesignQatermBoundpsaemodelService;

    private QaInterfacePsaemodelService qaInterfacePsaemodelService;

    @Autowired
    public QaModelService(QaDesignQatermhitService qaDesignQatermhitService, QaDesignQatermBoundpsaemodelService qaDesignQatermBoundpsaemodelService, QaInterfacePsaemodelService qaInterfacePsaemodelService) {
        this.qaDesignQatermhitService = qaDesignQatermhitService;
        this.qaDesignQatermBoundpsaemodelService = qaDesignQatermBoundpsaemodelService;
        this.qaInterfacePsaemodelService = qaInterfacePsaemodelService;
    }


    //取得質檢模版的所有模型
    public List<String> getAllQaModel(int qa_task_job_id) {
        List<String> models = new ArrayList<>();
        List<QaDesignQatermhit> qaDesignQatermhits = qaDesignQatermhitService.findByJobID(qa_task_job_id);
        for (QaDesignQatermhit qaDesignQatermhit : qaDesignQatermhits) {
            List<QaDesignQatermBoundpsaemodel> qaDesignQatermBoundpsaemodels = qaDesignQatermBoundpsaemodelService.findByQaTermID(qaDesignQatermhit.getTerm_ID());
            for (QaDesignQatermBoundpsaemodel qaDesignQatermBoundpsaemodel : qaDesignQatermBoundpsaemodels) {
                List<QaInterfacePsaemodel> qaInterfacePsaemodels = qaInterfacePsaemodelService.findById(qaDesignQatermBoundpsaemodel.getPsaemodelID());
                for (QaInterfacePsaemodel qaInterfacePsaemodel : qaInterfacePsaemodels) {
                    if (qaInterfacePsaemodel.isOnline()) {
                        models.add(qaInterfacePsaemodel.getName());
                    }
                }
            }

        }
        return models;
    }

    //取得質檢模版命中的所有模型
    public List<String> getAllHitQaModel(int qa_task_job_id) {
        List<String> models = new ArrayList<>();
        List<QaDesignQatermhit> qaDesignQatermhits = qaDesignQatermhitService.findByJobID(qa_task_job_id);
        for (QaDesignQatermhit qaDesignQatermhit : qaDesignQatermhits) {
            if (qaDesignQatermhit.getHuman_Hit().equalsIgnoreCase("true")) {
                List<QaDesignQatermBoundpsaemodel> qaDesignQatermBoundpsaemodels = qaDesignQatermBoundpsaemodelService.findByQaTermID(qaDesignQatermhit.getTerm_ID());
                for (QaDesignQatermBoundpsaemodel qaDesignQatermBoundpsaemodel : qaDesignQatermBoundpsaemodels) {
                    List<QaInterfacePsaemodel> qaInterfacePsaemodels = qaInterfacePsaemodelService.findById(qaDesignQatermBoundpsaemodel.getPsaemodelID());
                    for (QaInterfacePsaemodel qaInterfacePsaemodel : qaInterfacePsaemodels) {
                        if (qaInterfacePsaemodel.isOnline()) {
                            models.add(qaInterfacePsaemodel.getName());
                        }
                    }
                }
            }
        }

        return models;
    }

    //取得質檢模版的所有人工命中模型
    public List<String> getAllQaModelByHumanHitIsTrue(int qa_task_job_id) {
        List<String> models = new ArrayList<>();
        List<QaDesignQatermhit> qaDesignQatermhits = qaDesignQatermhitService.findByJobID(qa_task_job_id);
        for (QaDesignQatermhit qaDesignQatermhit : qaDesignQatermhits) {
            if ("True".equalsIgnoreCase(qaDesignQatermhit.getHuman_Hit())) {
                List<QaDesignQatermBoundpsaemodel> qaDesignQatermBoundpsaemodels = qaDesignQatermBoundpsaemodelService.findByQaTermID(qaDesignQatermhit.getTerm_ID());
                for (QaDesignQatermBoundpsaemodel qaDesignQatermBoundpsaemodel : qaDesignQatermBoundpsaemodels) {
                    List<QaInterfacePsaemodel> qaInterfacePsaemodels = qaInterfacePsaemodelService.findById(qaDesignQatermBoundpsaemodel.getPsaemodelID());
                    for (QaInterfacePsaemodel qaInterfacePsaemodel : qaInterfacePsaemodels) {
                        if (qaInterfacePsaemodel.isOnline() && qaInterfacePsaemodel.getType().equals(1)) {
                            models.add(qaInterfacePsaemodel.getName());
                        }
                    }
                }
            }

        }
        return models;
    }

    //取得質檢模版的所有人工未命中模型
    public List<String> getAllQaModelByHumanHitIsFalse(int qa_task_job_id) {
        List<String> models = new ArrayList<>();
        List<QaDesignQatermhit> qaDesignQatermhits = qaDesignQatermhitService.findByJobID(qa_task_job_id);
        for (QaDesignQatermhit qaDesignQatermhit : qaDesignQatermhits) {
            if ("false".equalsIgnoreCase(qaDesignQatermhit.getHuman_Hit())) {
                List<QaDesignQatermBoundpsaemodel> qaDesignQatermBoundpsaemodels = qaDesignQatermBoundpsaemodelService.findByQaTermID(qaDesignQatermhit.getTerm_ID());
                for (QaDesignQatermBoundpsaemodel qaDesignQatermBoundpsaemodel : qaDesignQatermBoundpsaemodels) {
                    List<QaInterfacePsaemodel> qaInterfacePsaemodels = qaInterfacePsaemodelService.findById(qaDesignQatermBoundpsaemodel.getPsaemodelID());
                    for (QaInterfacePsaemodel qaInterfacePsaemodel : qaInterfacePsaemodels) {
                        if (qaInterfacePsaemodel.isOnline() && qaInterfacePsaemodel.getType().equals(1)) {
                            models.add(qaInterfacePsaemodel.getName());
                        }
                    }
                }
            }

        }
        return models;
    }


    //取得質檢模版的所有人人工打分命中以及未命中
    public Map<Boolean, List<String>> getAllQaGroupModel(int qa_task_job_id) {
   // public List<String> getAllQaGroupModelByHumanHit(int qa_task_job_id) {
        List<String> models ;
        Map<Boolean, List<String>> modelsMap = new HashMap<>();

        modelsMap.put(true,new ArrayList<>());
        modelsMap.put(false,new ArrayList<>());

        List<QaDesignQatermhit> qaDesignQatermhits = qaDesignQatermhitService.findByJobID(qa_task_job_id);
        boolean IsFound;
        for (QaDesignQatermhit qaDesignQatermhit : qaDesignQatermhits) {
            IsFound = false;
            models= new ArrayList<>();
            List<QaDesignQatermBoundpsaemodel> qaDesignQatermBoundpsaemodels = qaDesignQatermBoundpsaemodelService.findByQaTermID(qaDesignQatermhit.getTerm_ID());
            for (QaDesignQatermBoundpsaemodel qaDesignQatermBoundpsaemodel : qaDesignQatermBoundpsaemodels) {
                if (!qaDesignQatermBoundpsaemodel.getPsaemodelID().contains("group")) {
                    List<QaInterfacePsaemodel> qaInterfacePsaemodels = qaInterfacePsaemodelService.findById(qaDesignQatermBoundpsaemodel.getPsaemodelID());
                    List<String> groupModelName = getGroupModelName(qaInterfacePsaemodels);
                    if (groupModelName.size() > 0) {
                        models.addAll(groupModelName);
                        if (qaDesignQatermBoundpsaemodels.size() > 2) IsFound = true;
                    }
                }
                if (IsFound) break;
            }

            models =models.stream().distinct().collect(Collectors.toList());
            if ("true".equalsIgnoreCase(qaDesignQatermhit.getHuman_Hit())) {
                modelsMap.get(true).addAll(models);
            } else if ("false".equalsIgnoreCase(qaDesignQatermhit.getHuman_Hit())) {
                modelsMap.get(false).addAll(models);
            }


//            if (IsHit.equalsIgnoreCase(qaDesignQatermhit.getHuman_Hit())) {
//                IsFound =false;
//                List<QaDesignQatermBoundpsaemodel> qaDesignQatermBoundpsaemodels = qaDesignQatermBoundpsaemodelService.findByQaTermID(qaDesignQatermhit.getTerm_ID());
//                for (QaDesignQatermBoundpsaemodel qaDesignQatermBoundpsaemodel : qaDesignQatermBoundpsaemodels) {
//                    if(!qaDesignQatermBoundpsaemodel.getPsaemodelID().contains("group")){
//                        List<QaInterfacePsaemodel> qaInterfacePsaemodels = qaInterfacePsaemodelService.findById(qaDesignQatermBoundpsaemodel.getPsaemodelID());
//                        List<String> groupModelName = getGroupModelName(qaInterfacePsaemodels);
//                        if (groupModelName.size() > 0) {
//                            models.addAll(groupModelName);
//                            IsFound =true;
//                        }
//                    }
//
//                    if(IsFound) break;
//                }
//            }

        }
        return modelsMap;
        //return models.stream().distinct().collect(Collectors.toList());
    }


    private List<String> getGroupModelName(List<QaInterfacePsaemodel> qaInterfacePsaemodels) {
        List<String> groupModelsName = new ArrayList<>();

        for (QaInterfacePsaemodel qaInterfacePsaemodel : qaInterfacePsaemodels) {
            if (qaInterfacePsaemodel.getPid().contains("group")) {
                groupModelsName.addAll(this.getGroupModelName(this.qaInterfacePsaemodelService.findById(qaInterfacePsaemodel.getPid())));
            } else {
                if (qaInterfacePsaemodel.isOnline() && qaInterfacePsaemodel.getType().equals(2)) {
                    groupModelsName.add(qaInterfacePsaemodel.getName());
                }
            }
        }
        return groupModelsName;

    }
}
