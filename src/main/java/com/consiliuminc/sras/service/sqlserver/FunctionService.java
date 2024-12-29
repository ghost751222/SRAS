package com.consiliuminc.sras.service.sqlserver;


import com.consiliuminc.sras.entities.sqlserver.Function;
import com.consiliuminc.sras.repository.sqlserver.FunctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FunctionService {

    private FunctionRepository functionRepository;


    @Autowired
    public FunctionService(FunctionRepository functionRepository) {
        this.functionRepository = functionRepository;
    }


    public List<Function> findAll() {
        return this.functionRepository.findAll();
    }

    public Function save(Function function) {
        return this.functionRepository.save(function);
    }

}
