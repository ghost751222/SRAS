package com.consiliuminc.sras.service.postgres;


import com.consiliuminc.sras.entities.postgres.QaInterfaceDim;
import com.consiliuminc.sras.entities.postgres.QaTaskJob;
import com.consiliuminc.sras.repository.postgres.QaTaskJobRepository;
import com.consiliuminc.sras.service.ResultSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class QaTaskJobService {




    private QaTaskJobRepository qaTaskJobRepository;
    private QaInterfaceDimService qaInterfaceDimService;

    @Autowired
    ResultSetService resultSetService;

   // private EntityManagerFactory entityManagerFactory;

    @Autowired
    public QaTaskJobService(QaTaskJobRepository qaTaskJobRepository,QaInterfaceDimService qaInterfaceDimService) {
        this.qaTaskJobRepository = qaTaskJobRepository;
        this.qaInterfaceDimService = qaInterfaceDimService;
    }




    public List<QaTaskJob> findAll() {
        List<QaInterfaceDim> qaInterfaceDims = qaInterfaceDimService.findAll();
        List<QaTaskJob> qaTaskJobs = resultSetService.setQaTaskJobTaskValue(qaTaskJobRepository.findAll(),qaInterfaceDims);
        return qaTaskJobs;
    }

    public List<QaTaskJob> findByTaskDateRange(LocalDate sDate, LocalDate eDate) {
        List<QaInterfaceDim> qaInterfaceDims = qaInterfaceDimService.findAll();
        List<QaTaskJob> qaTaskJobs = resultSetService.setQaTaskJobTaskValue(qaTaskJobRepository.findByTaskDateRange(sDate, eDate),qaInterfaceDims);
        return qaTaskJobs;
    }

    public List<QaTaskJob> findByTaskTLSubmitTime(LocalDate start, LocalDate end) {
        List<QaInterfaceDim> qaInterfaceDims = qaInterfaceDimService.findAll();
        List<QaTaskJob> qaTaskJobs = resultSetService.setQaTaskJobTaskValue(qaTaskJobRepository.findByTaskTLSubmitTime(start, end),qaInterfaceDims);
        return qaTaskJobs;
    }

}
