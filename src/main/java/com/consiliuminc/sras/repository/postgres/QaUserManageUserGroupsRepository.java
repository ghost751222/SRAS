package com.consiliuminc.sras.repository.postgres;

import com.consiliuminc.sras.entities.postgres.QaUserManageUserGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QaUserManageUserGroupsRepository extends JpaRepository<QaUserManageUserGroups, Integer> {


    List<QaUserManageUserGroups> findByUserID(Integer userID);


    @Transactional
    @Modifying
    @Query(value = "delete from qa_usermanage_user_groups where user_id=:userID",nativeQuery = true)
    Integer deleteByUserID(Integer userID);
}
