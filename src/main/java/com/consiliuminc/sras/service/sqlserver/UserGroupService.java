package com.consiliuminc.sras.service.sqlserver;


import com.consiliuminc.sras.entities.sqlserver.User;
import com.consiliuminc.sras.entities.sqlserver.UserGroup;
import com.consiliuminc.sras.repository.sqlserver.UserGroupRepository;
import com.consiliuminc.sras.repository.sqlserver.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserGroupService {

    private UserGroupRepository userGroupRepository;

    @Autowired
    public UserGroupService(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    public List<UserGroup> findAll() {
        return userGroupRepository.findAll();
    }

    public UserGroup save(UserGroup userGroup) {
        return this.userGroupRepository.save(userGroup);
    }

    public void delete(UserGroup userGroup) {
        this.userGroupRepository.delete(userGroup);
    }
    public void deleteByUserId(Integer userId) {
        this.userGroupRepository.deleteByUserId(userId);
    }


    public Integer deleteByGroupId(Integer groupId) {
       return this.userGroupRepository.deleteByGroupId(groupId);
    }

    public List<UserGroup> findByUserId(Integer userId){
        return userGroupRepository.findByUserId(userId);
    }

    public List<UserGroup> findByUserIdOrderBy(Integer userId){
        return userGroupRepository.findByUserIdOrderBy(userId);
    }

}
