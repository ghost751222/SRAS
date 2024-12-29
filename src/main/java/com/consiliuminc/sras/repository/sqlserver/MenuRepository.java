package com.consiliuminc.sras.repository.sqlserver;


import com.consiliuminc.sras.entities.sqlserver.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {


}