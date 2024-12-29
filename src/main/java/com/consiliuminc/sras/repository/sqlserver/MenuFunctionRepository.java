package com.consiliuminc.sras.repository.sqlserver;


import com.consiliuminc.sras.entities.sqlserver.GroupFunction;
import com.consiliuminc.sras.entities.sqlserver.MenuFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.OrderBy;
import java.util.List;

@Repository
public interface MenuFunctionRepository extends JpaRepository<MenuFunction, String> {



    @Query(value = "SELECT sras_menu_function.menu_name,sras_menu_function.program_name " +
            "  FROM sras_menu_function,sras_menu, sras_function " +
            "  where sras_menu_function.menu_name = sras_menu.menu_name " +
            "  and sras_menu_function.program_name = sras_function.program_name " +
            "  order by sras_menu.seq desc , sras_function.seq asc", nativeQuery = true)
    List<MenuFunction> findAllOrderBySeq();
}