package com.consiliuminc.sras.service.sqlserver;


import com.consiliuminc.sras.entities.sqlserver.MenuFunction;
import com.consiliuminc.sras.repository.sqlserver.MenuFunctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuFunctionService {

    private MenuFunctionRepository menuFunctionRepository;

    @Autowired
    public MenuFunctionService(MenuFunctionRepository menuFunctionRepository) {
        this.menuFunctionRepository = menuFunctionRepository;
    }

    public List<MenuFunction> findAll() {
        return menuFunctionRepository.findAll();
    }

    public List<MenuFunction> findAllOrderBySeq() {
        return this.menuFunctionRepository.findAllOrderBySeq();
    }

    public MenuFunction save(MenuFunction menuFunction) {
        return this.menuFunctionRepository.save(menuFunction);
    }


}
