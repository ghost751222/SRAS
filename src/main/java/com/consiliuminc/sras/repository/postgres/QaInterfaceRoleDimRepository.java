package com.consiliuminc.sras.repository.postgres;

import com.consiliuminc.sras.entities.postgres.QaInterfaceRoleDim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QaInterfaceRoleDimRepository extends JpaRepository<QaInterfaceRoleDim, Integer> {

    List<QaInterfaceRoleDim> findAllByRoleId(Integer roleId);
}
