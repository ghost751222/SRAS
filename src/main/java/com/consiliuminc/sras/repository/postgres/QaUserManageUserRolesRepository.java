package com.consiliuminc.sras.repository.postgres;

import com.consiliuminc.sras.entities.postgres.QaUserManageUserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QaUserManageUserRolesRepository extends JpaRepository<QaUserManageUserRoles, Integer> {


    List<QaUserManageUserRoles> findByUserID(Integer userID);

    @Modifying
    @Transactional
    @Query(value = "delete from qa_usermanage_user_roles where user_id =:userID",nativeQuery = true)
    Integer deleteByUserID(Integer userID);
}
