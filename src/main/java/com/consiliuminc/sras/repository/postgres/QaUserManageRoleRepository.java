package com.consiliuminc.sras.repository.postgres;

import com.consiliuminc.sras.entities.postgres.QaUserManageRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QaUserManageRoleRepository extends JpaRepository<QaUserManageRole, Integer> {

}
