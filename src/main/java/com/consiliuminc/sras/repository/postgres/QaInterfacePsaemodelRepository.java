package com.consiliuminc.sras.repository.postgres;

import com.consiliuminc.sras.entities.postgres.QaInterfaceDim;
import com.consiliuminc.sras.entities.postgres.QaInterfacePsaemodel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QaInterfacePsaemodelRepository extends JpaRepository<QaInterfacePsaemodel, String> {

    List<QaInterfacePsaemodel> findAllById(Iterable<String> iterable);

}
