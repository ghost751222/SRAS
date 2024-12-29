package com.consiliuminc.sras.repository.postgres;

import com.consiliuminc.sras.entities.postgres.QaInterfaceDim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QaInterfaceDimRepository extends JpaRepository<QaInterfaceDim, Long> {

    @Query(value = "select * from qa_interface_dim order by \"order\" ", nativeQuery = true)
    List<QaInterfaceDim> findAllOrderByOrder();
}
