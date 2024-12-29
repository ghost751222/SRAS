package com.consiliuminc.sras.repository.postgres;

import com.consiliuminc.sras.entities.postgres.QaDesignQatermBoundpsaemodel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QaDesignQatermBoundpsaemodelRepository extends JpaRepository<QaDesignQatermBoundpsaemodel, Integer> {


    List<QaDesignQatermBoundpsaemodel> findByQaTermID(Integer term_id);
}
