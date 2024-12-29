package com.consiliuminc.sras.repository.postgres;

import com.consiliuminc.sras.entities.postgres.QaUserManageUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QaUserManageUserRepository extends JpaRepository<QaUserManageUser, Integer> {
    List<QaUserManageUser> findByName(String name);
    List<QaUserManageUser> findByUsername(String username);
}
