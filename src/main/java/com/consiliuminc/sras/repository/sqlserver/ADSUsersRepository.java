package com.consiliuminc.sras.repository.sqlserver;

import com.consiliuminc.sras.entities.sqlserver.ADSUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ADSUsersRepository extends JpaRepository<ADSUsers, String> {

    @Query(value = "Select distinct c.DeskCode,c.TeamCode from ADS_Users c where c.Enable='Y' and c.DeskCode=:deskCode order by c.TeamCode", nativeQuery = true)
    List<String> findDistinctDeskCodeTeamCodeByDeskCode(String deskCode);


    @Query(value = "Select distinct c.GroupCode,c.TeamCode from ADS_Users c where c.Enable='Y' and c.GroupCode=:groupCode order by c.TeamCode", nativeQuery = true)
    List<String> findDistinctGroupCodeTeamCodeByGroupCode(String groupCode);
}
