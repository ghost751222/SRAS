package com.consiliuminc.sras.service.postgres;


import com.consiliuminc.sras.entities.postgres.QaDesignQatemplate;
import com.consiliuminc.sras.repository.postgres.QaDesignQatemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QaDesignQatemplateService {

    private QaDesignQatemplateRepository qaDesignQatemplateRepository;


    @Autowired
    public QaDesignQatemplateService(QaDesignQatemplateRepository qaDesignQatemplateRepository) {
        this.qaDesignQatemplateRepository = qaDesignQatemplateRepository;
    }


    public List<QaDesignQatemplate> findAll() {
        return qaDesignQatemplateRepository.findAll();
    }


}
