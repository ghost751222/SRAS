package com.consiliuminc.sras.repository.postgres;

import com.consiliuminc.sras.entities.postgres.QaDesignQatermhit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QaDesignQatermhitRepository extends JpaRepository<QaDesignQatermhit, Integer> {


    List<QaDesignQatermhit> findByJobID(Integer jobId);
}
