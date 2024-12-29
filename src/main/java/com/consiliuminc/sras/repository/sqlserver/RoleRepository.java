package com.consiliuminc.sras.repository.sqlserver;

import com.consiliuminc.sras.entities.sqlserver.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {


}
