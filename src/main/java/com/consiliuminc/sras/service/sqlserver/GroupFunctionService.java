package com.consiliuminc.sras.service.sqlserver;


import com.consiliuminc.sras.entities.sqlserver.GroupFunction;
import com.consiliuminc.sras.repository.sqlserver.GroupFunctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupFunctionService {

    private GroupFunctionRepository groupFunctionRepository;

    @Autowired
    public GroupFunctionService(GroupFunctionRepository groupFunctionRepository) {
        this.groupFunctionRepository = groupFunctionRepository;
    }
    public List<GroupFunction> findByGroupIdOrderBy(Integer groupId){
        return this.groupFunctionRepository.findByGroupIdOrderBy(groupId);
    }

    public List<GroupFunction> findByGroupId(Integer groupId){
        return this.groupFunctionRepository.findByGroupId(groupId);
    }
    public List<GroupFunction> findAll() {
        return groupFunctionRepository.findAll();
    }

    public GroupFunction save(GroupFunction groupFunction) {
        return this.groupFunctionRepository.save(groupFunction);
    }


}
