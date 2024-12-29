package com.consiliuminc.sras.service.postgres;


import com.consiliuminc.sras.entities.postgres.QaInterfaceRoleDim;
import com.consiliuminc.sras.repository.postgres.QaInterfaceRoleDimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QaInterfaceRoleDimService {

    private QaInterfaceRoleDimRepository qaInterfaceRoleDimRepository;


    @Autowired
    public QaInterfaceRoleDimService(QaInterfaceRoleDimRepository qaInterfaceRoleDimRepository) {
        this.qaInterfaceRoleDimRepository = qaInterfaceRoleDimRepository;
    }


    public List<QaInterfaceRoleDim> findAll() {
        return qaInterfaceRoleDimRepository.findAll();
    }


    public List<QaInterfaceRoleDim> findAllByRoleId(Integer roleId) {
        return this.qaInterfaceRoleDimRepository.findAllByRoleId(roleId);
    }


}
