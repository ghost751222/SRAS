package com.consiliuminc.sras.service.postgres;


import com.consiliuminc.sras.entities.postgres.QaDesignQatermBoundpsaemodel;
import com.consiliuminc.sras.repository.postgres.QaDesignQatermBoundpsaemodelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QaDesignQatermBoundpsaemodelService {

    private QaDesignQatermBoundpsaemodelRepository qaDesignQatermBoundpsaemodelRepository;


    @Autowired
    public QaDesignQatermBoundpsaemodelService(QaDesignQatermBoundpsaemodelRepository qaDesignQatermBoundpsaemodelRepository) {
        this.qaDesignQatermBoundpsaemodelRepository = qaDesignQatermBoundpsaemodelRepository;
    }


    public List<QaDesignQatermBoundpsaemodel> findAll() {
        return qaDesignQatermBoundpsaemodelRepository.findAll();
    }




    public List<QaDesignQatermBoundpsaemodel> findByQaTermID(Integer term_id) {
        return qaDesignQatermBoundpsaemodelRepository.findByQaTermID(term_id);
    }
}
