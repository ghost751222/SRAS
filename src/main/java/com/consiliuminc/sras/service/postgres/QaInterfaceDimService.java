package com.consiliuminc.sras.service.postgres;


import com.consiliuminc.sras.entities.postgres.QaInterfaceDim;
import com.consiliuminc.sras.repository.postgres.QaInterfaceDimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QaInterfaceDimService {

    private QaInterfaceDimRepository qaInterfaceDimRepository;


    @Autowired
    public QaInterfaceDimService(QaInterfaceDimRepository qaInterfaceDimRepository) {
        this.qaInterfaceDimRepository = qaInterfaceDimRepository;
    }


    public List<QaInterfaceDim> findAll() {
        return qaInterfaceDimRepository.findAll();
    }

    public List<QaInterfaceDim> findAllOrderByOrder() {
        return qaInterfaceDimRepository.findAllOrderByOrder();
    }
}
