package com.consiliuminc.sras.service.postgres;


import com.consiliuminc.sras.entities.postgres.QaDesignQatermhit;
import com.consiliuminc.sras.repository.postgres.QaDesignQatermhitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QaDesignQatermhitService {

    private QaDesignQatermhitRepository qaInterfaceDimRepository;


    @Autowired
    public QaDesignQatermhitService(QaDesignQatermhitRepository qaInterfaceDimRepository) {
        this.qaInterfaceDimRepository = qaInterfaceDimRepository;
    }


    public List<QaDesignQatermhit> findAll() {
        return qaInterfaceDimRepository.findAll();
    }


    public List<QaDesignQatermhit> findByJobID(Integer jobId){return qaInterfaceDimRepository.findByJobID(jobId);}

}
