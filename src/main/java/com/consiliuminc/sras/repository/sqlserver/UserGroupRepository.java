package com.consiliuminc.sras.repository.sqlserver;

import com.consiliuminc.sras.entities.sqlserver.User;
import com.consiliuminc.sras.entities.sqlserver.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    @Query(value = "select * from sras_user_group where userid = :userid", nativeQuery = true)
    List<UserGroup> findByUserId(@Param("userid") Integer userId);

    @Query(value = "select sras_user_group.* from sras_user_group,sras_group_function,sras_menu_function,sras_group,sras_function,sras_menu,sras_user " +
            "where  sras_group_function.groupid = sras_group.id and sras_group_function.program_name = sras_function.program_name " +
            "and  sras_menu_function.menu_name =sras_menu.menu_name and sras_menu_function.program_name = sras_function.program_name " +
            "and sras_user_group.userid =sras_user.id and sras_user_group.groupid = sras_group.id " +
            "and sras_user.id = :userid " +
            "order by sras_menu.seq asc ,sras_function.seq asc", nativeQuery = true)
    List<UserGroup> findByUserIdOrderBy(@Param("userid") Integer userId);


    @Modifying
    @Query(value = "delete from sras_user_group where userid = :userid", nativeQuery = true)
    Integer deleteByUserId(@Param("userid") Integer userId);

    @Modifying
    @Query(value = "delete from sras_user_group where groupid = :groupid", nativeQuery = true)
    Integer deleteByGroupId(@Param("groupid") Integer groupId);


}
