package com.consiliuminc.sras.service.postgres;


import com.consiliuminc.sras.entities.postgres.QaUserManageUserRoles;
import com.consiliuminc.sras.repository.postgres.QaUserManageUserRolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QaUserManageUserRolesService {

    private QaUserManageUserRolesRepository qaUserManageUserRolesRepository;


    @Autowired
    public QaUserManageUserRolesService(QaUserManageUserRolesRepository qaUserManageUserRolesRepository) {
        this.qaUserManageUserRolesRepository = qaUserManageUserRolesRepository;
    }

    public List<QaUserManageUserRoles> findAll() {
        return qaUserManageUserRolesRepository.findAll();
    }


    public List<QaUserManageUserRoles> findByUserID(Integer userID) {
        return qaUserManageUserRolesRepository.findByUserID(userID);
    }

    public void deleteAll(List<QaUserManageUserRoles> qaUserManageUserRolesList) {
        qaUserManageUserRolesRepository.deleteAll(qaUserManageUserRolesList);
    }


    public Integer deleteByUserID(Integer userID){
        return qaUserManageUserRolesRepository.deleteByUserID(userID);
    }

    public QaUserManageUserRoles save(QaUserManageUserRoles qaUserManageUserRoles) {
        return qaUserManageUserRolesRepository.save(qaUserManageUserRoles);
    }
}
