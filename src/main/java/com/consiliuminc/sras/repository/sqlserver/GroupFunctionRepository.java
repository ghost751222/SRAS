package com.consiliuminc.sras.repository.sqlserver;

import com.consiliuminc.sras.entities.sqlserver.GroupFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupFunctionRepository extends JpaRepository<GroupFunction, Long> {

    @Query(value = "SELECT sras_group_function.* " +
            "                          FROM sras_menu_function,sras_menu, sras_function,sras_group_function " +
            "                         where sras_menu_function.menu_name = sras_menu.menu_name " +
            "                          and sras_menu_function.program_name = sras_function.program_name " +
            " and sras_group_function.program_name = sras_function.program_name " +
            " and sras_group_function.group.id = :groupid" +
            "                          order by sras_menu.seq desc , sras_function.seq asc", nativeQuery = true)
    List<GroupFunction> findByGroupIdOrderBy(@Param("groupid") Integer groupId);


    @Query(value = "select * from sras_group_function where groupid = :groupid ", nativeQuery = true)
    List<GroupFunction> findByGroupId(@Param("groupid") Integer groupId);

    @Modifying
    @Query(value = "delete from sras_group_function where groupid = :groupid", nativeQuery = true)
    Integer deleteByGroupId(@Param("groupid") Integer groupId);
}
