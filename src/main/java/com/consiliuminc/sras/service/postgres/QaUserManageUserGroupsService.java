package com.consiliuminc.sras.service.postgres;


import com.consiliuminc.sras.entities.postgres.QaUserManageUserGroups;
import com.consiliuminc.sras.entities.postgres.QaUserManageUserRoles;
import com.consiliuminc.sras.repository.postgres.QaUserManageUserGroupsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QaUserManageUserGroupsService {

    private QaUserManageUserGroupsRepository qaUserManageUserGroupsRepository;


    @Autowired
    public QaUserManageUserGroupsService(QaUserManageUserGroupsRepository qaUserManageUserGroupsRepository) {
        this.qaUserManageUserGroupsRepository = qaUserManageUserGroupsRepository;
    }


    public List<QaUserManageUserGroups> findAll() {
        return qaUserManageUserGroupsRepository.findAll();
    }


    public List<QaUserManageUserGroups> findByUserID(Integer userID) {
        return qaUserManageUserGroupsRepository.findByUserID(userID);
    }

    public void deleteAll(List<QaUserManageUserGroups> qaUserManageUserGroupsList) {
        qaUserManageUserGroupsRepository.deleteAll(qaUserManageUserGroupsList);
    }

    public QaUserManageUserGroups save(QaUserManageUserGroups qaUserManageUserGroups) {
        return qaUserManageUserGroupsRepository.save(qaUserManageUserGroups);
    }

    public Integer deleteByUserID(Integer userID) {
        return qaUserManageUserGroupsRepository.deleteByUserID(userID);
    }
}
