package com.consiliuminc.sras.service.postgres;


import com.consiliuminc.sras.entities.postgres.QaInterfacePsaemodel;
import com.consiliuminc.sras.entities.postgres.QaTaskJob;
import com.consiliuminc.sras.repository.postgres.QaInterfacePsaemodelRepository;
import com.consiliuminc.sras.repository.postgres.QaTaskJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class QaInterfacePsaemodelService {

    private QaInterfacePsaemodelRepository qaTaskJobRepository;


    @Autowired
    public QaInterfacePsaemodelService(QaInterfacePsaemodelRepository qaTaskJobRepository) {
        this.qaTaskJobRepository = qaTaskJobRepository;
    }


    public List<QaInterfacePsaemodel> findAll() {
        return qaTaskJobRepository.findAll();
    }


    public List<QaInterfacePsaemodel> findById(String ID) {
        return qaTaskJobRepository.findAllById(Arrays.asList(ID));
    }

}
