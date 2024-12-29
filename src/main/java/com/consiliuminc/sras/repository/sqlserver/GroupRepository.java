package com.consiliuminc.sras.repository.sqlserver;

import com.consiliuminc.sras.entities.sqlserver.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    //@Query(value = "select * from sras_group where group_name =:groupName ", nativeQuery = true)
    List<Group> findByName(String groupName);

}
