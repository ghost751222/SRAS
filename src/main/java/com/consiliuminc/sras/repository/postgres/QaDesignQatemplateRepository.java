package com.consiliuminc.sras.repository.postgres;

import com.consiliuminc.sras.entities.postgres.QaDesignQatemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QaDesignQatemplateRepository extends JpaRepository<QaDesignQatemplate, Long> {


}
