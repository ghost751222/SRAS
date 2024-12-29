package com.consiliuminc.sras.service.postgres;


import com.consiliuminc.sras.entities.postgres.QaUserManageRole;
import com.consiliuminc.sras.repository.postgres.QaUserManageRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QaUserManageRoleService {

    private QaUserManageRoleRepository qaUserManageRoleRepository;


    @Autowired
    public QaUserManageRoleService(QaUserManageRoleRepository qaUserManageRoleRepository) {
        this.qaUserManageRoleRepository = qaUserManageRoleRepository;
    }

    public List<QaUserManageRole> findAll() {
        return qaUserManageRoleRepository.findAll();
    }


}
